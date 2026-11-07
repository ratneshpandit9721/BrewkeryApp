package com.brewkery.app.ui.screen.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewkery.app.R
import com.brewkery.app.domain.model.formatMoney
import com.brewkery.app.ui.components.BrewkeryImage
import com.brewkery.app.ui.components.ErrorPane
import com.brewkery.app.ui.components.LoadingPane
import com.brewkery.app.ui.components.QuantityStepper
import com.brewkery.app.ui.theme.Amber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    onViewCart: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current
    var justAdded by remember { mutableStateOf(false) }
    val addedLabel = stringResource(R.string.added_to_cart)
    val viewLabel = stringResource(R.string.view_cart)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            val itemName = when (event) {
                is DetailsEvent.Added -> event.name
            }
            justAdded = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            val result = snackbarHostState.showSnackbar(
                message = "$addedLabel · $itemName",
                actionLabel = viewLabel,
                duration = SnackbarDuration.Short,
            )
            justAdded = false
            if (result == SnackbarResult.ActionPerformed) onViewCart()
        }
    }

    val buttonColor by animateColorAsState(
        targetValue = if (justAdded) {
            MaterialTheme.colorScheme.tertiary
        } else {
            MaterialTheme.colorScheme.primary
        },
        label = "addButton",
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val item = state.item
            if (item != null && !state.isLoading) {
                Surface(tonalElevation = 3.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(modifier = Modifier.weight(0.8f)) {
                            Text(
                                text = formatMoney(state.currencySymbol, state.lineTotal),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = stringResource(R.string.quantity) + " ${state.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Button(
                            onClick = viewModel::addToCart,
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        ) {
                            Text(
                                text = if (justAdded) {
                                    stringResource(R.string.added_to_cart)
                                } else {
                                    stringResource(R.string.add_to_cart)
                                },
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingPane(
                message = stringResource(R.string.loading_item),
                modifier = Modifier.padding(padding),
            )

            state.item == null -> ErrorPane(
                message = state.errorMessage ?: stringResource(R.string.item_load_error),
                onRetry = viewModel::load,
                modifier = Modifier.padding(padding),
            )

            else -> {
                val item = state.item!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding())
                        .verticalScroll(rememberScrollState()),
                ) {
                    Box {
                        BrewkeryImage(
                            url = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                        )
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(12.dp)
                                .clip(CircleShape)
                                .size(44.dp),
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            ) {
                                Box(
                                    modifier = Modifier.size(44.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back),
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (!item.badge.isNullOrBlank()) {
                            Text(
                                text = item.badge,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = item.tagline,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            InfoPill(
                                text = String.format(java.util.Locale.US, "%.1f", item.rating),
                                showStar = true,
                            )
                            if (item.reviewCount > 0) InfoPill(text = "${item.reviewCount} reviews")
                            if (item.calories > 0) InfoPill(text = "${item.calories} kcal")
                            if (item.prepTime.isNotBlank()) InfoPill(text = item.prepTime)
                        }
                        Text(text = item.description, style = MaterialTheme.typography.bodyLarge)
                        if (item.ingredients.isNotEmpty()) {
                            SectionTitle(stringResource(R.string.ingredients))
                            Text(
                                text = item.ingredients.joinToString("  ·  "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (item.customizations.sizes.isNotEmpty()) {
                            SectionTitle(stringResource(R.string.size))
                            OptionChips(
                                options = item.customizations.sizes.map { size ->
                                    val price = if (size.extraPrice > 0) {
                                        "  +${formatMoney(state.currencySymbol, size.extraPrice)}"
                                    } else {
                                        ""
                                    }
                                    size.id to size.label + price
                                },
                                selectedId = state.selectedSizeId,
                                onSelected = viewModel::onSizeSelected,
                            )
                        }
                        if (item.customizations.sugarLevels.isNotEmpty()) {
                            SectionTitle(stringResource(R.string.sweetness))
                            OptionChips(
                                options = item.customizations.sugarLevels.map { it to it },
                                selectedId = state.selectedSugar,
                                onSelected = viewModel::onSugarSelected,
                            )
                        }
                        if (item.customizations.milkOptions.isNotEmpty()) {
                            SectionTitle(stringResource(R.string.milk))
                            OptionChips(
                                options = item.customizations.milkOptions.map { milk ->
                                    val price = if (milk.extraPrice > 0) {
                                        "  +${formatMoney(state.currencySymbol, milk.extraPrice)}"
                                    } else {
                                        ""
                                    }
                                    milk.id to milk.name + price
                                },
                                selectedId = state.selectedMilkId,
                                onSelected = viewModel::onMilkSelected,
                            )
                        }
                        SectionTitle(stringResource(R.string.quantity))
                        QuantityStepper(
                            quantity = state.quantity,
                            onDecrement = { viewModel.onQuantityChange(state.quantity - 1) },
                            onIncrement = { viewModel.onQuantityChange(state.quantity + 1) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun InfoPill(
    text: String,
    showStar: Boolean = false,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showStar) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Amber,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(14.dp),
                )
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun OptionChips(
    options: List<Pair<String, String>>,
    selectedId: String?,
    onSelected: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (id, label) ->
            FilterChip(
                selected = selectedId == id,
                onClick = { onSelected(id) },
                label = { Text(label) },
            )
        }
    }
}
