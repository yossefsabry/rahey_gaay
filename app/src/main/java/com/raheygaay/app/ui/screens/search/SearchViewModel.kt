package com.raheygaay.app.ui.screens.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.DashboardPlace
import com.raheygaay.app.data.model.Traveler
import com.raheygaay.app.data.model.Trip
import com.raheygaay.app.data.repository.DashboardRepository
import com.raheygaay.app.data.repository.HomeRepository
import com.raheygaay.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SearchFilters(
    val name: String = "",
    val origin: String = "",
    val destination: String = "",
    val minRating: Float = 0f,
    val verifiedOnly: Boolean = false,
    val includeTravelers: Boolean = true,
    val includeTrips: Boolean = true,
    val includePlaces: Boolean = true
)

data class SearchUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val travelers: List<Traveler> = emptyList(),
    val trips: List<Trip> = emptyList(),
    val places: List<DashboardPlace> = emptyList(),
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val queryFlow = MutableStateFlow("")
    private val filtersFlow = MutableStateFlow(SearchFilters())
    private val dataFlow = MutableStateFlow(SearchData())
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        observeSearchResults()
        loadSearchData()
    }

    fun updateQuery(value: String) {
        queryFlow.value = value
        _uiState.value = _uiState.value.copy(query = value)
    }

    fun updateFilters(filters: SearchFilters) {
        filtersFlow.value = filters
        _uiState.value = _uiState.value.copy(filters = filters)
    }

    fun resetFilters() {
        val reset = SearchFilters()
        filtersFlow.value = reset
        _uiState.value = _uiState.value.copy(filters = reset)
    }

    fun retry() {
        loadSearchData()
    }

    private fun loadSearchData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                val homeDeferred = async { homeRepository.getHomeContent() }
                val profileDeferred = async { profileRepository.getProfile() }
                val dashboardDeferred = async { dashboardRepository.getDashboard() }
                Triple(
                    homeDeferred.await().travelers,
                    profileDeferred.await().trips,
                    dashboardDeferred.await().topPlaces
                )
            }.onSuccess { (travelers, trips, places) ->
                val searchData = buildSearchData(travelers, trips, places)
                dataFlow.value = searchData
                _uiState.value = _uiState.value.copy(isLoading = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage
                )
            }
        }
    }

    private fun observeSearchResults() {
        viewModelScope.launch {
            combine(
                queryFlow.debounce(120),
                filtersFlow,
                dataFlow
            ) { query, filters, data ->
                val trimmedQuery = query.trim()
                val filteredTravelers = if (filters.includeTravelers) {
                    data.travelerIndex.filter { matchesTraveler(it, trimmedQuery, filters) }.map { it.traveler }
                } else emptyList()
                val filteredTrips = if (filters.includeTrips) {
                    data.tripIndex.filter { matchesTrip(it, trimmedQuery, filters) }.map { it.trip }
                } else emptyList()
                val filteredPlaces = if (filters.includePlaces) {
                    data.placeIndex.filter { matchesPlace(it, trimmedQuery, filters) }.map { it.place }
                } else emptyList()
                Triple(filteredTravelers, filteredTrips, filteredPlaces)
            }.collect { (travelers, trips, places) ->
                _uiState.value = _uiState.value.copy(
                    travelers = travelers,
                    trips = trips,
                    places = places
                )
            }
        }
    }

    private suspend fun buildSearchData(
        travelers: List<Traveler>,
        trips: List<Trip>,
        places: List<DashboardPlace>
    ): SearchData = withContext(Dispatchers.Default) {
        val resources = context.resources
        SearchData(
            travelerIndex = travelers.map { traveler ->
                val name = resources.getString(traveler.nameRes)
                val route = resources.getString(traveler.routeRes)
                val rating = traveler.rating.toFloatOrNull() ?: 0f
                TravelerIndex(traveler, name, route, rating, traveler.isVerified)
            },
            tripIndex = trips.map { trip ->
                val title = resources.getString(trip.titleRes)
                val subtitle = resources.getString(trip.subtitleRes)
                val badge = resources.getString(trip.badgeRes)
                TripIndex(trip, title, subtitle, badge)
            },
            placeIndex = places.map { place ->
                val name = resources.getString(place.nameRes)
                PlaceIndex(place, name)
            }
        )
    }

    private fun matchesTraveler(index: TravelerIndex, query: String, filters: SearchFilters): Boolean {
        val matchesQuery = query.isBlank() || index.name.contains(query, true) || index.route.contains(query, true)
        val matchesName = filters.name.isBlank() || index.name.contains(filters.name, true)
        val matchesOrigin = filters.origin.isBlank() || index.route.contains(filters.origin, true)
        val matchesDestination = filters.destination.isBlank() || index.route.contains(filters.destination, true)
        val matchesRating = index.rating >= filters.minRating
        val matchesVerified = !filters.verifiedOnly || index.isVerified
        return matchesQuery && matchesName && matchesOrigin && matchesDestination && matchesRating && matchesVerified
    }

    private fun matchesTrip(index: TripIndex, query: String, filters: SearchFilters): Boolean {
        val matchesQuery = query.isBlank() || index.title.contains(query, true) || index.subtitle.contains(query, true) || index.badge.contains(query, true)
        val matchesOrigin = filters.origin.isBlank() || index.title.contains(filters.origin, true)
        val matchesDestination = filters.destination.isBlank() || index.title.contains(filters.destination, true)
        return matchesQuery && matchesOrigin && matchesDestination
    }

    private fun matchesPlace(index: PlaceIndex, query: String, filters: SearchFilters): Boolean {
        val matchesQuery = query.isBlank() || index.name.contains(query, true)
        val matchesOrigin = filters.origin.isBlank() || index.name.contains(filters.origin, true)
        val matchesDestination = filters.destination.isBlank() || index.name.contains(filters.destination, true)
        return matchesQuery && matchesOrigin && matchesDestination
    }
}

private data class SearchData(
    val travelerIndex: List<TravelerIndex> = emptyList(),
    val tripIndex: List<TripIndex> = emptyList(),
    val placeIndex: List<PlaceIndex> = emptyList()
)

private data class TravelerIndex(
    val traveler: Traveler,
    val name: String,
    val route: String,
    val rating: Float,
    val isVerified: Boolean
)

private data class TripIndex(
    val trip: Trip,
    val title: String,
    val subtitle: String,
    val badge: String
)

private data class PlaceIndex(
    val place: DashboardPlace,
    val name: String
)
