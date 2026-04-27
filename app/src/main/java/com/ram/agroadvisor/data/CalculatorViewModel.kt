package com.ram.agroadvisor.ui.screens.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.agroadvisor.data.CalculatorRequest
import com.ram.agroadvisor.data.CalculatorResponse
import com.ram.agroadvisor.data.remote.AgroApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CalculatorUiState {
    object Idle : CalculatorUiState()
    object Loading : CalculatorUiState()
    data class Success(val data: CalculatorResponse) : CalculatorUiState()
    data class Error(val message: String) : CalculatorUiState()
}

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val agroApi: AgroApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalculatorUiState>(CalculatorUiState.Idle)
    val uiState: StateFlow<CalculatorUiState> = _uiState

    fun calculate(
        cropType: String,
        growthStage: String,
        soilType: String,
        fieldSizeHectares: Double
    ) {
        viewModelScope.launch {
            _uiState.value = CalculatorUiState.Loading
            try {
                val response = agroApi.calculate(
                    CalculatorRequest(
                        cropType = cropType,
                        growthStage = growthStage,
                        soilType = soilType,
                        fieldSizeHectares = fieldSizeHectares
                    )
                )
                _uiState.value = CalculatorUiState.Success(response)
            } catch (e: retrofit2.HttpException) {
                val msg = when (e.code()) {
                    404 -> "Bu kombinasiya üçün məlumat tapılmadı"
                    400 -> "Yanlış məlumat daxil edildi"
                    500 -> "Server xətası baş verdi"
                    else -> "Xəta: ${e.code()}"
                }
                _uiState.value = CalculatorUiState.Error(msg)
            } catch (e: Exception) {
                _uiState.value = CalculatorUiState.Error(e.message ?: "Xəta baş verdi")
            }
        }
    }

    fun reset() {
        _uiState.value = CalculatorUiState.Idle
    }
}
