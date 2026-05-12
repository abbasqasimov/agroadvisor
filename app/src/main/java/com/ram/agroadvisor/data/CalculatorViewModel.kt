package com.ram.agroadvisor.ui.screens.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.agroadvisor.data.CalculatorRequest
import com.ram.agroadvisor.data.CalculatorResponse
import com.ram.agroadvisor.data.model.CropRequirement
import com.ram.agroadvisor.data.model.SoilMultiplier
import com.ram.agroadvisor.data.remote.AgroApi
import com.ram.agroadvisor.data.remote.ApiErrorParser
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

/** State of the catalogue (dropdown options). Independent of the calculate result. */
sealed class LookupsState {
    data object Loading : LookupsState()
    data object Ready : LookupsState()
    data class Error(val message: String) : LookupsState()
}

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val agroApi: AgroApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalculatorUiState>(CalculatorUiState.Idle)
    val uiState: StateFlow<CalculatorUiState> = _uiState

    private val _cropRequirements = MutableStateFlow<List<CropRequirement>>(emptyList())
    val cropRequirements: StateFlow<List<CropRequirement>> = _cropRequirements

    private val _soilMultipliers = MutableStateFlow<List<SoilMultiplier>>(emptyList())
    val soilMultipliers: StateFlow<List<SoilMultiplier>> = _soilMultipliers

    private val _lookupsState = MutableStateFlow<LookupsState>(LookupsState.Loading)
    val lookupsState: StateFlow<LookupsState> = _lookupsState

    init {
        loadLookups()
    }

    /**
     * Fetches the supported (crop, stage) combinations and the supported soil
     * types in parallel. Idempotent — safe to call again on a Retry button.
     */
    fun loadLookups() {
        viewModelScope.launch {
            _lookupsState.value = LookupsState.Loading
            try {
                val crops = agroApi.getCropRequirements()
                val soils = agroApi.getSoilMultipliers()
                _cropRequirements.value = crops
                _soilMultipliers.value = soils
                _lookupsState.value = LookupsState.Ready
            } catch (e: retrofit2.HttpException) {
                _lookupsState.value = LookupsState.Error(
                    ApiErrorParser.parse(e, "Siyahılar yüklənmədi: ${e.code()}")
                )
            } catch (e: Exception) {
                _lookupsState.value = LookupsState.Error(e.message ?: "Bağlantı xətası")
            }
        }
    }

    /** Distinct crop names from the catalogue, in the order the server returned them. */
    fun availableCropTypes(): List<String> =
        _cropRequirements.value.map { it.cropType }.distinct()

    /** Growth stages supported for a given crop. Empty until the user picks a crop. */
    fun growthStagesFor(cropType: String): List<String> =
        _cropRequirements.value
            .filter { it.cropType == cropType }
            .map { it.growthStage }
            .distinct()

    /** Soil types from the catalogue. */
    fun availableSoilTypes(): List<String> =
        _soilMultipliers.value.map { it.soilType }.distinct()

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
                    401 -> "Sessiyanın müddəti bitib. Yenidən daxil olun"
                    404 -> ApiErrorParser.parse(e, "Bu kombinasiya üçün məlumat tapılmadı")
                    400 -> ApiErrorParser.parse(e, "Yanlış məlumat daxil edildi")
                    500 -> "Server xətası baş verdi"
                    else -> ApiErrorParser.parse(e, "Xəta: ${e.code()}")
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
