package com.brewkery.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewkery.app.domain.model.Category
import com.brewkery.app.domain.model.Menu
import com.brewkery.app.domain.model.MenuItem
import com.brewkery.app.domain.repository.BrewkeryRepository
import com.brewkery.app.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val tagline: String = "Artisanal Coffee & Fresh Oven Bakes",
    val currencySymbol: String = "$",
    val categories: List<Category> = emptyList(),
    val items: List<MenuItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val cartCount: Int = 0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BrewkeryRepository,
    cartRepository: CartRepository,
) : ViewModel() {

    private val menuState = MutableStateFlow(MenuLoad())
    private val query = MutableStateFlow("")
    private val categoryId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        menuState,
        query,
        categoryId,
        cartRepository.lines,
    ) { menu, search, category, lines ->
        val catalog = menu.menu
        HomeUiState(
            isLoading = menu.isLoading,
            errorMessage = menu.errorMessage,
            tagline = catalog?.meta?.tagline ?: "Artisanal Coffee & Fresh Oven Bakes",
            currencySymbol = catalog?.meta?.currencySymbol ?: "$",
            categories = catalog?.categories.orEmpty(),
            items = catalog?.items.orEmpty().filter { item ->
                val matchesCategory = category == null || item.categoryId == category
                val matchesQuery = search.isBlank() ||
                    item.name.contains(search, ignoreCase = true) ||
                    item.tagline.contains(search, ignoreCase = true) ||
                    item.description.contains(search, ignoreCase = true)
                matchesCategory && matchesQuery
            },
            searchQuery = search,
            selectedCategoryId = category,
            cartCount = lines.sumOf { it.quantity },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            menuState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getMenu()
                .onSuccess { menu ->
                    menuState.value = MenuLoad(menu = menu, isLoading = false)
                }
                .onFailure { error ->
                    menuState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "We couldn’t load the menu.",
                        )
                    }
                }
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onCategorySelected(id: String?) {
        categoryId.value = id
    }

    private data class MenuLoad(
        val menu: Menu? = null,
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
    )
}
