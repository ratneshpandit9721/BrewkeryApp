package com.brewkery.app.ui.screen.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewkery.app.domain.model.MenuItem
import com.brewkery.app.domain.model.MilkOption
import com.brewkery.app.domain.model.SizeOption
import com.brewkery.app.domain.model.roundCents
import com.brewkery.app.domain.model.sugarSurcharge
import com.brewkery.app.domain.repository.BrewkeryRepository
import com.brewkery.app.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val item: MenuItem? = null,
    val currencySymbol: String = "$",
    val quantity: Int = 1,
    val selectedSizeId: String? = null,
    val selectedSugar: String? = null,
    val selectedMilkId: String? = null,
) {
    val selectedSize: SizeOption?
        get() = item?.customizations?.sizes?.firstOrNull { it.id == selectedSizeId }

    val selectedMilk: MilkOption?
        get() = item?.customizations?.milkOptions?.firstOrNull { it.id == selectedMilkId }

    val unitPrice: Double
        get() {
            val base = item?.basePrice ?: return 0.0
            return (
                base +
                    (selectedSize?.extraPrice ?: 0.0) +
                    (selectedMilk?.extraPrice ?: 0.0) +
                    sugarSurcharge(selectedSugar)
                ).roundCents()
        }

    val lineTotal: Double
        get() = (unitPrice * quantity).roundCents()
}

sealed interface DetailsEvent {
    data class Added(val name: String) : DetailsEvent
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: BrewkeryRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle.get<Int>("itemId"))

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DetailsEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<DetailsEvent> = _events.asSharedFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val symbol = repository.getMenu().getOrNull()?.meta?.currencySymbol ?: "$"
            repository.getItem(itemId)
                .onSuccess { item ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = null,
                            item = item,
                            currencySymbol = symbol,
                            selectedSizeId = current.selectedSizeId
                                ?.takeIf { id -> item.customizations.sizes.any { it.id == id } }
                                ?: item.customizations.sizes.firstOrNull()?.id,
                            selectedSugar = current.selectedSugar
                                ?.takeIf { item.customizations.sugarLevels.contains(it) }
                                ?: item.customizations.sugarLevels.firstOrNull(),
                            selectedMilkId = current.selectedMilkId
                                ?.takeIf { id -> item.customizations.milkOptions.any { it.id == id } }
                                ?: item.customizations.milkOptions.firstOrNull()?.id,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "We couldn’t load this item.",
                        )
                    }
                }
        }
    }

    fun onQuantityChange(quantity: Int) {
        _uiState.update { it.copy(quantity = quantity.coerceIn(1, 20)) }
    }

    fun onSizeSelected(id: String) {
        _uiState.update { it.copy(selectedSizeId = id) }
    }

    fun onSugarSelected(level: String) {
        _uiState.update { it.copy(selectedSugar = level) }
    }

    fun onMilkSelected(id: String) {
        _uiState.update { it.copy(selectedMilkId = id) }
    }

    fun addToCart() {
        val state = _uiState.value
        val item = state.item ?: return
        val size = state.selectedSize ?: SizeOption("standard", "Standard", 0.0)
        val milk = state.selectedMilk ?: MilkOption("standard", "Standard", 0.0)
        val sugar = state.selectedSugar ?: "Standard"
        cartRepository.add(
            item = item,
            quantity = state.quantity,
            size = size,
            sugarLevel = sugar,
            milk = milk,
        )
        _events.tryEmit(DetailsEvent.Added(item.name))
    }
}
