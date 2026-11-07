package com.brewkery.app.ui.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewkery.app.domain.model.CartLine
import com.brewkery.app.domain.model.MenuMeta
import com.brewkery.app.domain.model.OrderPricing
import com.brewkery.app.domain.repository.BrewkeryRepository
import com.brewkery.app.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val lines: List<CartLine> = emptyList(),
    val currencySymbol: String = "$",
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val taxRatePercent: Double = MenuMeta.fallback.taxRatePercent,
    val deliveryFee: Double = MenuMeta.fallback.deliveryFee,
    val total: Double = 0.0,
    val estimatedDeliveryTime: String = MenuMeta.fallback.estimatedDeliveryTime,
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val menuRepository: BrewkeryRepository,
) : ViewModel() {

    private val meta = MutableStateFlow(MenuMeta.fallback)
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    val uiState: StateFlow<CartUiState> = combine(
        cartRepository.lines,
        meta,
    ) { lines, store ->
        val subtotal = OrderPricing.subtotal(lines)
        val tax = OrderPricing.tax(subtotal, store.taxRatePercent)
        CartUiState(
            lines = lines,
            currencySymbol = store.currencySymbol,
            subtotal = subtotal,
            tax = tax,
            taxRatePercent = store.taxRatePercent,
            deliveryFee = if (lines.isEmpty()) 0.0 else store.deliveryFee,
            total = if (lines.isEmpty()) 0.0 else OrderPricing.total(subtotal, tax, store.deliveryFee),
            estimatedDeliveryTime = store.estimatedDeliveryTime,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState(),
    )

    init {
        viewModelScope.launch {
            menuRepository.getMenu().onSuccess { menu -> meta.value = menu.meta }
        }
    }

    fun increment(lineId: String) {
        val line = cartRepository.lines.value.firstOrNull { it.lineId == lineId } ?: return
        cartRepository.updateQuantity(lineId, line.quantity + 1)
    }

    fun decrement(lineId: String) {
        val line = cartRepository.lines.value.firstOrNull { it.lineId == lineId } ?: return
        cartRepository.updateQuantity(lineId, line.quantity - 1)
    }

    fun remove(lineId: String) {
        cartRepository.remove(lineId)
    }

    fun placeOrder() {
        if (cartRepository.lines.value.isEmpty()) return
        val order = cartRepository.placeOrder(meta.value)
        _events.tryEmit(order.referenceId)
    }
}
