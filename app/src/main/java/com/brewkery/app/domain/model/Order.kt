package com.brewkery.app.domain.model

data class PlacedOrder(
    val referenceId: String,
    val lines: List<CartLine>,
    val subtotal: Double,
    val tax: Double,
    val taxRatePercent: Double,
    val deliveryFee: Double,
    val total: Double,
    val currencySymbol: String,
    val estimatedDeliveryTime: String,
)

object OrderPricing {
    fun subtotal(lines: List<CartLine>): Double = lines.sumOf { it.lineTotal }.roundCents()

    fun tax(subtotal: Double, taxRatePercent: Double): Double =
        (subtotal * taxRatePercent / 100.0).roundCents()

    fun total(subtotal: Double, tax: Double, deliveryFee: Double): Double =
        (subtotal + tax + deliveryFee).roundCents()
}

fun formatMoney(symbol: String, amount: Double): String =
    symbol + String.format(java.util.Locale.US, "%.2f", amount)

fun formatPercent(rate: Double): String =
    if (rate % 1.0 == 0.0) {
        rate.toInt().toString()
    } else {
        String.format(java.util.Locale.US, "%.1f", rate)
    }
