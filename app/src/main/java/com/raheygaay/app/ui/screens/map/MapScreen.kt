package com.raheygaay.app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import android.graphics.Color as AndroidColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.viewinterop.AndroidView
import com.raheygaay.app.BuildConfig
import com.raheygaay.app.R
import com.raheygaay.app.data.model.MapTraveler
import com.raheygaay.app.data.model.MapPerson
import com.raheygaay.app.ui.components.AppTextField
import com.raheygaay.app.ui.components.ErrorState
import com.raheygaay.app.ui.components.GlassCard
import com.raheygaay.app.ui.components.NetworkImage
import com.raheygaay.app.ui.components.PrimaryButton
import com.raheygaay.app.ui.components.SkeletonBlock
import com.raheygaay.app.ui.theme.BrandMint
import com.raheygaay.app.ui.theme.primaryGradientBrush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression.get
import com.mapbox.maps.extension.style.expressions.generated.Expression.has
import com.mapbox.maps.extension.style.expressions.generated.Expression.not
import com.mapbox.maps.extension.style.expressions.generated.Expression.toString
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs

private const val PEOPLE_SOURCE_ID = "people-source"
private const val CLUSTER_LAYER_ID = "people-clusters"
private const val CLUSTER_COUNT_LAYER_ID = "people-cluster-count"
private const val UNCLUSTERED_LAYER_ID = "people-unclustered"

@Composable
fun MapScreen(
    onContact: () -> Unit,
    showSkeleton: Boolean = false,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val state = uiState.value
    val content = state.content
    val showSkeletonState = showSkeleton || (state.isLoading && content == null)
    if (content == null) {
        if (showSkeletonState) {
            MapSkeleton()
        } else {
            ErrorState(
                title = stringResource(R.string.error_generic_title),
                message = stringResource(R.string.error_generic_body),
                buttonText = stringResource(R.string.error_retry),
                onRetry = { viewModel.retry() },
                details = if (BuildConfig.DEBUG) state.errorMessage else null
            )
        }
        return
    }
    val background = MaterialTheme.colorScheme.background
    val isDark = background.luminance() < 0.4f
    var mapLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(isDark) {
        mapLoaded = false
    }
    val showLoading = showSkeleton || !mapLoaded
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        EgyptMap(
            people = content.people,
            isDark = isDark,
            onMapLoaded = { mapLoaded = true }
        )
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            Spacer(modifier = Modifier.height(12.dp))
            SearchPanel()
            Spacer(modifier = Modifier.weight(1f))
            FloatingActions()
            Spacer(modifier = Modifier.height(12.dp))
            TravelerBottomSheet(traveler = content.traveler, onContact = onContact)
        }
        if (showLoading) {
            MapLoadingOverlay()
        }
    }
}

@Composable
private fun MapSkeleton() {
    val background = MaterialTheme.colorScheme.background
    val isDark = background.luminance() < 0.4f
    val mapBackground = remember(isDark, background) {
        val colors = if (isDark) {
            listOf(Color(0xFF1E293B), Color(0xFF0F172A), background)
        } else {
            listOf(Color(0xFFE0F2FE), Color(0xFFF0F9FF), background)
        }
        Brush.radialGradient(colors = colors)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mapBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            Spacer(modifier = Modifier.height(12.dp))
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(22.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(220.dp),
                shape = RoundedCornerShape(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MapLoadingOverlay() {
    val background = MaterialTheme.colorScheme.background
    val isDark = background.luminance() < 0.4f
    val overlayColor = if (isDark) {
        background.copy(alpha = 0.72f)
    } else {
        background.copy(alpha = 0.66f)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(overlayColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp),
                shape = RoundedCornerShape(12.dp)
            )
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(18.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Text(
                text = stringResource(R.string.map_loading),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SearchPanel() {
    val query = remember { mutableStateOf("") }
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        shadowElevation = 8.dp,
        contentPadding = 12.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextField(
                value = query.value,
                onValueChange = { query.value = it },
                placeholder = stringResource(R.string.common_search_placeholder),
                leadingIcon = Icons.Outlined.Search,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(primaryGradientBrush()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun FloatingActions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingActionIcon(icon = Icons.Outlined.MyLocation)
        FloatingActionIcon(icon = Icons.Outlined.Layers)
    }
}

@Composable
private fun EgyptMap(
    people: List<MapPerson>,
    isDark: Boolean,
    onMapLoaded: () -> Unit
) {
    val mapView = rememberMapViewWithLifecycle()
    val mapboxMap = remember(mapView) { mapView.getMapboxMap() }
    val styleUri = if (isDark) Style.DARK else Style.MAPBOX_STREETS

    LaunchedEffect(styleUri) {
        mapboxMap.loadStyleUri(styleUri) { style ->
            configureEgyptBounds(mapboxMap)
            addOrUpdatePeopleSource(style, people)
            onMapLoaded()
        }
    }

    LaunchedEffect(people) {
        mapboxMap.getStyle()?.let { style ->
            addOrUpdatePeopleSource(style, people)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    )
}

private fun configureEgyptBounds(mapboxMap: com.mapbox.maps.MapboxMap) {
    val bounds = CoordinateBounds(
        Point.fromLngLat(24.0, 22.0),
        Point.fromLngLat(36.9, 31.8)
    )
    mapboxMap.setBounds(
        CameraBoundsOptions.Builder()
            .bounds(bounds)
            .minZoom(4.5)
            .maxZoom(16.5)
            .build()
    )
    mapboxMap.setCamera(
        CameraOptions.Builder()
            .center(Point.fromLngLat(31.2357, 30.0444))
            .zoom(5.8)
            .build()
    )
}

private fun addOrUpdatePeopleSource(style: Style, people: List<MapPerson>) {
    val features = people.map { person ->
        Feature.fromGeometry(Point.fromLngLat(person.longitude, person.latitude)).apply {
            addStringProperty("id", person.id)
            addStringProperty("name", person.name)
        }
    }
    val collection = FeatureCollection.fromFeatures(features)
    val existing = style.getSourceAs<GeoJsonSource>(PEOPLE_SOURCE_ID)
    if (existing != null) {
        existing.featureCollection(collection)
        return
    }

    val source = geoJsonSource(PEOPLE_SOURCE_ID) {
        featureCollection(collection)
        cluster(true)
        clusterRadius(60)
        clusterMaxZoom(12)
    }
    style.addSource(source)

    if (style.getLayer(CLUSTER_LAYER_ID) == null) {
        style.addLayer(
            circleLayer(CLUSTER_LAYER_ID, PEOPLE_SOURCE_ID) {
                filter(has("point_count"))
                circleColor(AndroidColor.parseColor("#2563EB"))
                circleRadius(18.0)
                circleOpacity(0.85)
            }
        )
    }

    if (style.getLayer(CLUSTER_COUNT_LAYER_ID) == null) {
        style.addLayer(
            symbolLayer(CLUSTER_COUNT_LAYER_ID, PEOPLE_SOURCE_ID) {
                filter(has("point_count"))
                textField(toString(get("point_count")))
                textSize(12.0)
                textColor(AndroidColor.parseColor("#FFFFFF"))
                textAnchor(TextAnchor.CENTER)
            }
        )
    }

    if (style.getLayer(UNCLUSTERED_LAYER_ID) == null) {
        style.addLayer(
            circleLayer(UNCLUSTERED_LAYER_ID, PEOPLE_SOURCE_ID) {
                filter(not(has("point_count")))
                circleColor(AndroidColor.parseColor("#10B981"))
                circleRadius(6.0)
                circleStrokeColor(AndroidColor.parseColor("#FFFFFF"))
                circleStrokeWidth(1.4)
            }
        )
    }
}

@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(mapView) {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(31.2357, 30.0444))
                .zoom(5.8)
                .build()
        )
    }

    androidx.compose.runtime.DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_START -> mapView.onStart()
                androidx.lifecycle.Lifecycle.Event.ON_STOP -> mapView.onStop()
                androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    return mapView
}

@Composable
private fun FloatingActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .shadow(6.dp, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
    }
}


@Composable
private fun TravelerBottomSheet(
    traveler: MapTraveler,
    onContact: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    .align(Alignment.CenterHorizontally)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NetworkImage(
                        url = traveler.avatarUrl,
                        contentDescription = stringResource(traveler.nameRes),
                        size = 64.dp,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = stringResource(traveler.nameRes), style = MaterialTheme.typography.titleLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.common_age, "28"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(text = traveler.rating, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.common_trusted),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.common_active_now),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SheetInfoCard(
                    label = stringResource(R.string.common_origin),
                    value = stringResource(R.string.map_origin_value),
                    modifier = Modifier.weight(1f)
                )
                SheetInfoCard(
                    label = stringResource(R.string.common_destination),
                    value = stringResource(R.string.map_destination_value),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandMint.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                    contentDescription = null,
                    tint = BrandMint
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.common_traveling_tomorrow),
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandMint,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.common_departure_time),
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandMint.copy(alpha = 0.8f)
                    )
                }
            }
            Text(
                text = stringResource(traveler.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryButton(
                    text = stringResource(R.string.common_contact_name, stringResource(traveler.nameRes)),
                    onClick = onContact,
                    leadingIcon = Icons.AutoMirrored.Outlined.Chat,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetInfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
