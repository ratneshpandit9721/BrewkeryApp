package com.brewkery.app.domain.model

data class Menu(
    val meta: MenuMeta,
    val categories: List<Category>,
    val items: List<MenuItem>,
)

data class MenuMeta(
    val app: String,
    val version: String,
    val tagline: String,
    val currency: String,
    val currencySymbol: String,
    val deliveryFee: Double,
    val taxRatePercent: Double,
    val estimatedDeliveryTime: String,
) {
    companion object {
        val fallback = MenuMeta(
            app = "Brewkery",
            version = "1.0.0",
            tagline = "Artisanal Coffee & Fresh Oven Bakes",
            currency = "USD",
            currencySymbol = "$",
            deliveryFee = 2.50,
            taxRatePercent = 8.0,
            estimatedDeliveryTime = "20 - 30 mins",
        )
    }
}

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val itemCount: Int,
)

data class MenuItem(
    val id: Int,
    val categoryId: String,
    val name: String,
    val tagline: String,
    val description: String,
    val basePrice: Double,
    val rating: Double,
    val reviewCount: Int,
    val prepTime: String,
    val calories: Int,
    val imageUrl: String,
    val badge: String?,
    val ingredients: List<String>,
    val customizations: Customizations,
)

data class Customizations(
    val sizes: List<SizeOption>,
    val sugarLevels: List<String>,
    val milkOptions: List<MilkOption>,
)

data class SizeOption(
    val id: String,
    val label: String,
    val extraPrice: Double,
)

data class MilkOption(
    val id: String,
    val name: String,
    val extraPrice: Double,
)
