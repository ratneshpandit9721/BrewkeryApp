package com.brewkery.app.data.mapper

import com.brewkery.app.data.model.CategoryDto
import com.brewkery.app.data.model.CustomizationsDto
import com.brewkery.app.data.model.MenuItemDto
import com.brewkery.app.data.model.MenuMetaDto
import com.brewkery.app.data.model.MenuResponseDto
import com.brewkery.app.data.model.MilkOptionDto
import com.brewkery.app.data.model.SizeOptionDto
import com.brewkery.app.domain.model.Category
import com.brewkery.app.domain.model.Customizations
import com.brewkery.app.domain.model.Menu
import com.brewkery.app.domain.model.MenuItem
import com.brewkery.app.domain.model.MenuMeta
import com.brewkery.app.domain.model.MilkOption
import com.brewkery.app.domain.model.SizeOption

fun MenuResponseDto.toDomain(): Menu {
    val fallback = MenuMeta.fallback
    return Menu(
        meta = meta.toDomain(fallback),
        categories = categories.orEmpty().mapNotNull { it.toDomain() },
        items = items.orEmpty().mapNotNull { it.toDomain() },
    )
}

fun MenuMetaDto?.toDomain(fallback: MenuMeta): MenuMeta = MenuMeta(
    app = this?.app?.takeIf { it.isNotBlank() } ?: fallback.app,
    version = this?.version?.takeIf { it.isNotBlank() } ?: fallback.version,
    tagline = this?.tagline?.takeIf { it.isNotBlank() } ?: fallback.tagline,
    currency = this?.currency?.takeIf { it.isNotBlank() } ?: fallback.currency,
    currencySymbol = this?.currencySymbol?.takeIf { it.isNotBlank() } ?: fallback.currencySymbol,
    deliveryFee = this?.deliveryFee ?: fallback.deliveryFee,
    taxRatePercent = this?.taxRatePercent ?: fallback.taxRatePercent,
    estimatedDeliveryTime = this?.estimatedDeliveryTime?.takeIf { it.isNotBlank() }
        ?: fallback.estimatedDeliveryTime,
)

fun CategoryDto.toDomain(): Category? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val safeName = name?.takeIf { it.isNotBlank() } ?: return null
    return Category(
        id = safeId,
        name = safeName,
        icon = icon.orEmpty(),
        itemCount = itemCount ?: 0,
    )
}

fun MenuItemDto.toDomain(): MenuItem? {
    val safeId = id ?: return null
    val safeName = name?.takeIf { it.isNotBlank() } ?: return null
    return MenuItem(
        id = safeId,
        categoryId = categoryId.orEmpty(),
        name = safeName,
        tagline = tagline.orEmpty(),
        description = description.orEmpty(),
        basePrice = basePrice ?: 0.0,
        rating = rating ?: 0.0,
        reviewCount = reviewCount ?: 0,
        prepTime = prepTime.orEmpty(),
        calories = calories ?: 0,
        imageUrl = imageUrl.orEmpty(),
        badge = badge?.takeIf { it.isNotBlank() },
        ingredients = ingredients.orEmpty().filter { it.isNotBlank() },
        customizations = customizations.toDomain(),
    )
}

fun CustomizationsDto?.toDomain(): Customizations = Customizations(
    sizes = this?.sizes.orEmpty().mapNotNull { it.toDomain() },
    sugarLevels = this?.sugarLevels.orEmpty().filter { it.isNotBlank() },
    milkOptions = this?.milkOptions.orEmpty().mapNotNull { it.toDomain() },
)

fun SizeOptionDto.toDomain(): SizeOption? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val safeLabel = label?.takeIf { it.isNotBlank() } ?: return null
    return SizeOption(
        id = safeId,
        label = safeLabel,
        extraPrice = extraPrice ?: 0.0,
    )
}

fun MilkOptionDto.toDomain(): MilkOption? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val safeName = name?.takeIf { it.isNotBlank() } ?: return null
    return MilkOption(
        id = safeId,
        name = safeName,
        extraPrice = extraPrice ?: 0.0,
    )
}
