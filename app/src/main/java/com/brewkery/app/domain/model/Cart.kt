package com.brewkery.app.domain.model

data class CartLine(
    val lineId: String,
    val item: MenuItem,
    val quantity: Int,
    val size: SizeOption,
    val sugarLevel: String,
    val milk: MilkOption,
) {
    val unitPrice: Double
        get() = (
            item.basePrice +
                size.extraPrice +
                milk.extraPrice +
                sugarSurcharge(sugarLevel)
            ).roundCents()

    val lineTotal: Double
        get() = (unitPrice * quantity).roundCents()

    val optionsLabel: String
        get() = listOf(size.label, sugarLevel, milk.name)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
}

fun cartLineId(itemId: Int, sizeId: String, sugarLevel: String, milkId: String): String =
    "$itemId|$sizeId|$sugarLevel|$milkId"

private val sugarSurchargePattern = Regex("""\(\+\s*(\d+(?:\.\d+)?)\)""")

fun sugarSurcharge(label: String?): Double {
    if (label.isNullOrBlank()) return 0.0
    return sugarSurchargePattern.find(label)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
}

fun Double.roundCents(): Double = kotlin.math.round(this * 100.0) / 100.0
