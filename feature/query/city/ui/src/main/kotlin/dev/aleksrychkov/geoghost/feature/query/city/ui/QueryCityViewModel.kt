package dev.aleksrychkov.geoghost.feature.query.city.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aleksrychkov.geoghost.core.exception.NoInternetException
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.query.city.QueryCityFactory
import dev.aleksrychkov.geoghost.feature.query.city.QueryCityUseCase
import dev.aleksrychkov.geoghost.feature.query.city.model.CityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
internal class QueryCityViewModel(
    private val onCityLatLngClicked: (LatLng) -> Unit,
) : ViewModel() {
    private companion object {
        const val TAG = "QueryCityViewModel"
        const val DEBOUNCE_DELAY_MILLIS = 500L
    }

    private val _state = MutableStateFlow(QueryCityState())
    val state = _state.asStateFlow()

    private val queryFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val queryCityUseCase: QueryCityUseCase by lazy {
        QueryCityFactory.instance.provideQueryCityUseCase()
    }

    fun startQueryFlow() {
        queryFlow
            .debounce { if (it.isBlank()) 0 else DEBOUNCE_DELAY_MILLIS }
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    handleQueryResult(emptyList())
                } else {
                    _state.emit(_state.value.copy(queryInProgress = true))
                    val res = queryCityUseCase.queryCity(query)

                    handleQueryException(res.exceptionOrNull())
                    handleQueryResult(res.getOrNull())
                }
                flowOf(Unit)
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun submitQuery(query: String) {
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(
                    query = query,
                    queryInProgress = true,
                    showGeneralError = false,
                    showInternetError = false,
                )
            )
            queryFlow.emit(query)
        }
    }

    fun onCityClicked(cityResult: CityEntity) {
        onCityLatLngClicked(cityResult.latLng)
    }

    fun onRetryClicked() {
        submitQuery(_state.value.query)
    }

    private fun handleQueryResult(result: List<CityEntity>?) {
        if (result == null) return
        viewModelScope.launch {
            val query = _state.value.query
            _state.emit(QueryCityState(query = query, queryResult = result))
        }
    }

    private fun handleQueryException(throwable: Throwable?) {
        if (throwable == null) return
        Log.e(TAG, "Failed to query city", throwable)
        viewModelScope.launch {
            val query = _state.value.query
            val isNoInternetException = throwable is NoInternetException
            val isGeneralException = !isNoInternetException
            _state.emit(
                QueryCityState(
                    query = query,
                    showInternetError = isNoInternetException,
                    showGeneralError = isGeneralException,
                )
            )
        }
    }
}
