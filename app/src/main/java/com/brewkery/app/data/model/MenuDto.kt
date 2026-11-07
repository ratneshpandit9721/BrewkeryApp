package com.brewkery.app.data.model

import com.google.gson.annotations.SerializedName

data class MenuResponseDto(
    @SerializedName("meta") val meta: MenuMetaDto?,
    @SerializedName("categories") val categories: List<CategoryDto>?,
    @SerializedName("items") val items: List<MenuItemDto>?,
)

data class MenuMetaDto(
    @SerializedName("app") val app: String?,
    @SerializedName("version") val version: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("currency_symbol") val currencySymbol: String?,
    @SerializedName("delivery_fee") val deliveryFee: Double?,
    @SerializedName("tax_rate_percent") val taxRatePercent: Double?,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: String?,
)

data class CategoryDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("item_count") val itemCount: Int?,
)

data class MenuItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("base_price") val basePrice: Double?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("review_count") val reviewCount: Int?,
    @SerializedName("prep_time") val prepTime: String?,
    @SerializedName("calories") val calories: Int?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("badge") val badge: String?,
    @SerializedName("ingredients") val ingredients: List<String>?,
    @SerializedName("customizations") val customizations: CustomizationsDto?,
)

data class CustomizationsDto(
    @SerializedName("sizes") val sizes: List<SizeOptionDto>?,
    @SerializedName("sugar_levels") val sugarLevels: List<String>?,
    @SerializedName("milk_options") val milkOptions: List<MilkOptionDto>?,
)

data class SizeOptionDto(
    @SerializedName("id") val id: String?,
    @SerializedName("label") val label: String?,
    @SerializedName("extra_price") val extraPrice: Double?,
)

data class MilkOptionDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("extra_price") val extraPrice: Double?,
)
