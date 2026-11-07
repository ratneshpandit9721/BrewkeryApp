package com.brewkery.app.data.repository

import com.brewkery.app.domain.model.CartLine
import com.brewkery.app.domain.model.MenuItem
import com.brewkery.app.domain.model.MenuMeta
import com.brewkery.app.domain.model.MilkOption
import com.brewkery.app.domain.model.OrderPricing
import com.brewkery.app.domain.model.PlacedOrder
import com.brewkery.app.domain.model.SizeOption
import com.brewkery.app.domain.model.cartLineId
import com.brewkery.app.domain.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {

    private val _lines = MutableStateFlow<List<CartLine>>(emptyList())
    override val lines: StateFlow<List<CartLine>> = _lines.asStateFlow()

    private val _lastOrder = MutableStateFlow<PlacedOrder?>(null)
    override val lastOrder: StateFlow<PlacedOrder?> = _lastOrder.asStateFlow()

    override fun add(
        item: MenuItem,
        quantity: Int,
        size: SizeOption,
        sugarLevel: String,
        milk: MilkOption,
    ) {
        val safeQuantity = quantity.coerceIn(1, MAX_QUANTITY)
        val lineId = cartLineId(item.id, size.id, sugarLevel, milk.id)
        _lines.update { current ->
            val index = current.indexOfFirst { it.lineId == lineId }
            if (index >= 0) {
                current.mapIndexed { i, line ->
                    if (i == index) {
                        line.copy(quantity = (line.quantity + safeQuantity).coerceAtMost(MAX_QUANTITY))
                    } else {
                        line
                    }
                }
            } else {
                current + CartLine(
                    lineId = lineId,
                    item = item,
                    quantity = safeQuantity,
                    size = size,
                    sugarLevel = sugarLevel,
                    milk = milk,
                )
            }
        }
    }

    override fun updateQuantity(lineId: String, quantity: Int) {
        if (quantity <= 0) {
            remove(lineId)
            return
        }
        _lines.update { current ->
            current.map { line ->
                if (line.lineId == lineId) line.copy(quantity = quantity.coerceAtMost(MAX_QUANTITY)) else line
            }
        }
    }

    override fun remove(lineId: String) {
        _lines.update { current -> current.filterNot { it.lineId == lineId } }
    }

    override fun clear() {
        _lines.value = emptyList()
    }

    override fun placeOrder(meta: MenuMeta): PlacedOrder {
        val current = _lines.value
        check(current.isNotEmpty()) { "Cart is empty" }
        val subtotal = OrderPricing.subtotal(current)
        val tax = OrderPricing.tax(subtotal, meta.taxRatePercent)
        val total = OrderPricing.total(subtotal, tax, meta.deliveryFee)
        val order = PlacedOrder(
            referenceId = newReferenceId(),
            lines = current.toList(),
            subtotal = subtotal,
            tax = tax,
            taxRatePercent = meta.taxRatePercent,
            deliveryFee = meta.deliveryFee,
            total = total,
            currencySymbol = meta.currencySymbol,
            estimatedDeliveryTime = meta.estimatedDeliveryTime,
        )
        _lastOrder.value = order
        _lines.value = emptyList()
        return order
    }

    private fun newReferenceId(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val body = buildString(6) {
            repeat(6) { append(alphabet[Random.nextInt(alphabet.length)]) }
        }
        return "BRW-$body"
    }

    private companion object {
        const val MAX_QUANTITY = 20
    }
}
