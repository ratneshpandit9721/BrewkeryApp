package com.brewkery.app.ui.screen.success

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewkery.app.domain.model.PlacedOrder
import com.brewkery.app.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SuccessUiState(
    val referenceId: String,
    val order: PlacedOrder?,
)

@HiltViewModel
class SuccessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    cartRepository: CartRepository,
) : ViewModel() {

    private val orderId: String = checkNotNull(savedStateHandle.get<String>("orderId"))

    val uiState: StateFlow<SuccessUiState> = cartRepository.lastOrder
        .map { order ->
            SuccessUiState(
                referenceId = order?.referenceId ?: orderId,
                order = order?.takeIf { it.referenceId == orderId },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SuccessUiState(
                referenceId = cartRepository.lastOrder.value?.referenceId ?: orderId,
                order = cartRepository.lastOrder.value?.takeIf { it.referenceId == orderId },
            ),
        )
}
