package com.brewkery.app

import com.brewkery.app.domain.model.CartLine
import com.brewkery.app.domain.model.Customizations
import com.brewkery.app.domain.model.MenuItem
import com.brewkery.app.domain.model.MilkOption
import com.brewkery.app.domain.model.OrderPricing
import com.brewkery.app.domain.model.SizeOption
import com.brewkery.app.domain.model.sugarSurcharge
import org.junit.Assert.assertEquals
import org.junit.Test

class OrderPricingTest {

    @Test
    fun line_total_includes_size_milk_and_sugar_surcharge() {
        val line = sampleLine(quantity = 2)
        assertEquals(0.40, sugarSurcharge("Light Wildflower Honey (+0.40)"), 0.001)
        assertEquals(6.40, line.unitPrice, 0.001)
        assertEquals(12.80, line.lineTotal, 0.001)
    }

    @Test
    fun order_total_applies_tax_and_delivery() {
        val subtotal = OrderPricing.subtotal(listOf(sampleLine(quantity = 1)))
        val tax = OrderPricing.tax(subtotal, 8.0)
        val total = OrderPricing.total(subtotal, tax, 2.50)
        assertEquals(6.40, subtotal, 0.001)
        assertEquals(0.51, tax, 0.001)
        assertEquals(9.41, total, 0.001)
    }

    private fun sampleLine(quantity: Int): CartLine {
        val item = MenuItem(
            id = 1,
            categoryId = "cat_hot_coffee",
            name = "Latte",
            tagline = "",
            description = "",
            basePrice = 4.85,
            rating = 4.9,
            reviewCount = 10,
            prepTime = "5 mins",
            calories = 180,
            imageUrl = "",
            badge = null,
            ingredients = emptyList(),
            customizations = Customizations(emptyList(), emptyList(), emptyList()),
        )
        return CartLine(
            lineId = "1",
            item = item,
            quantity = quantity,
            size = SizeOption("sz_medium", "Grande", 0.65),
            sugarLevel = "Light Wildflower Honey (+0.40)",
            milk = MilkOption("m_almond", "Almond", 0.50),
        )
    }
}
