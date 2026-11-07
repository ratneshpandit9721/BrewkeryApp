package com.brewkery.app.ui.navigation

object Destinations {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val DETAILS = "details/{itemId}"
    const val CART = "cart"
    const val SUCCESS = "success/{orderId}"

    fun details(itemId: Int): String = "details/$itemId"

    fun success(orderId: String): String = "success/$orderId"
}
