package com.brewkery.app.domain.repository

import com.brewkery.app.domain.model.CartLine
import com.brewkery.app.domain.model.MenuItem
import com.brewkery.app.domain.model.MenuMeta
import com.brewkery.app.domain.model.MilkOption
import com.brewkery.app.domain.model.PlacedOrder
import com.brewkery.app.domain.model.SizeOption
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val lines: StateFlow<List<CartLine>>
    val lastOrder: StateFlow<PlacedOrder?>

    fun add(
        item: MenuItem,
        quantity: Int,
        size: SizeOption,
        sugarLevel: String,
        milk: MilkOption,
    )

    fun updateQuantity(lineId: String, quantity: Int)

    fun remove(lineId: String)

    fun clear()

    fun placeOrder(meta: MenuMeta): PlacedOrder
}
