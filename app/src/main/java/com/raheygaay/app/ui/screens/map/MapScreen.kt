package com.raheygaay.app.ui.screens.map

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
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
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.gestures
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PEOPLE_SOURCE_ID = "people-source"
private const val CLUSTER_LAYER_ID = "people-clusters"
private const val UNCLUSTERED_LAYER_ID = "people-unclustered"
private const val CLUSTER_ICON_ID = "cluster-icon"
private const val PERSON_ICON_PREFIX = "person-icon-"
private val PERSON_ICON_SIZE = 44.dp
private val CLUSTER_ICON_SIZE = 48.dp
private val MARKER_PALETTE = listOf(
    AndroidColor.parseColor("#0EA5E9"),
    AndroidColor.parseColor("#10B981"),
    AndroidColor.parseColor("#F59E0B"),
    AndroidColor.parseColor("#EF4444"),
    AndroidColor.parseColor("#14B8A6")
)

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
    var selectedPerson by remember { mutableStateOf<MapPerson?>(null) }
    LaunchedEffect(content) {
        selectedPerson = null
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        EgyptMap(
            people = content.people,
            isDark = isDark,
            onPersonSelected = { selectedPerson = it },
            onMapLoaded = { mapLoaded = true }
        )
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            Spacer(modifier = Modifier.height(12.dp))
            SearchPanel()
            Spacer(modifier = Modifier.weight(1f))
            FloatingActions()
            Spacer(modifier = Modifier.height(12.dp))
            TravelerBottomSheet(
                traveler = content.traveler,
                selectedPerson = selectedPerson,
                onContact = onContact
            )
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
    var query by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val collapse = {
        isExpanded = false
        focusManager.clearFocus()
    }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val expandedWidth = maxWidth
        val cardWidth by animateDpAsState(
            targetValue = if (isExpanded) expandedWidth else 56.dp,
            label = "searchWidth"
        )
        GlassCard(
            modifier = Modifier
                .width(cardWidth)
                .animateContentSize(),
            shape = MaterialTheme.shapes.large,
            shadowElevation = 8.dp,
            contentPadding = if (isExpanded) 12.dp else 6.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(
                    visible = !isExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(primaryGradientBrush())
                            .clickable { isExpanded = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandHorizontally(expandFrom = Alignment.Start) + fadeIn(),
                    exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                            AppTextField(
                                value = query,
                                onValueChange = { query = it },
                                placeholder = stringResource(R.string.common_search_placeholder),
                                leadingIcon = Icons.Outlined.Search,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = { collapse() },
                                onDone = { collapse() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onFocusChanged { focusState ->
                                    if (!focusState.isFocused && isExpanded) {
                                        collapse()
                                    }
                                }
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
    onPersonSelected: (MapPerson) -> Unit,
    onMapLoaded: () -> Unit
) {
    val mapView = rememberMapViewWithLifecycle()
    val mapboxMap = remember(mapView) { mapView.getMapboxMap() }
    val context = LocalContext.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val imageLoader = remember(context) { coil.ImageLoader(context) }
    val onPersonSelectedState = rememberUpdatedState(onPersonSelected)
    val peopleByIdState = rememberUpdatedState(people.associateBy { it.id })
    val iconCache = remember { mutableMapOf<String, Bitmap>() }
    val requestedAvatarIds = remember { mutableSetOf<String>() }
    val addedIconIds = remember { mutableSetOf<String>() }
    val styleUri = if (isDark) Style.DARK else Style.MAPBOX_STREETS

    androidx.compose.runtime.DisposableEffect(mapView) {
        val listener = com.mapbox.maps.plugin.gestures.OnMapClickListener { point ->
            handleMapClick(
                mapboxMap = mapboxMap,
                camera = mapView.camera,
                point = point,
                peopleById = peopleByIdState.value,
                onPersonSelected = onPersonSelectedState.value
            )
        }
        mapView.gestures.addOnMapClickListener(listener)
        onDispose {
            mapView.gestures.removeOnMapClickListener(listener)
        }
    }

    LaunchedEffect(styleUri) {
        addedIconIds.clear()
        mapboxMap.loadStyleUri(styleUri) { style ->
            configureEgyptBounds(mapboxMap)
            addOrUpdatePeopleSource(style, people)
            ensureClusterIcon(style, density, iconCache, addedIconIds)
            addOrUpdatePersonIcons(
                style = style,
                people = people,
                context = context,
                density = density,
                iconCache = iconCache,
                requestedAvatarIds = requestedAvatarIds,
                addedIconIds = addedIconIds,
                coroutineScope = coroutineScope,
                imageLoader = imageLoader
            )
            onMapLoaded()
        }
    }

    LaunchedEffect(people) {
        mapboxMap.getStyle()?.let { style ->
            addOrUpdatePeopleSource(style, people)
            ensureClusterIcon(style, density, iconCache, addedIconIds)
            addOrUpdatePersonIcons(
                style = style,
                people = people,
                context = context,
                density = density,
                iconCache = iconCache,
                requestedAvatarIds = requestedAvatarIds,
                addedIconIds = addedIconIds,
                coroutineScope = coroutineScope,
                imageLoader = imageLoader
            )
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
            addStringProperty("iconId", personIconId(person))
            addStringProperty("initials", nameInitials(person.name))
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
            symbolLayer(CLUSTER_LAYER_ID, PEOPLE_SOURCE_ID) {
                filter(Expression.has("point_count"))
                iconImage(CLUSTER_ICON_ID)
                iconSize(1.0)
                iconAllowOverlap(true)
                iconIgnorePlacement(true)
                iconAnchor(IconAnchor.CENTER)
            }
        )
    }

    if (style.getLayer(UNCLUSTERED_LAYER_ID) == null) {
        style.addLayer(
            symbolLayer(UNCLUSTERED_LAYER_ID, PEOPLE_SOURCE_ID) {
                filter(Expression.not(Expression.has("point_count")))
                iconImage(Expression.get("iconId"))
                iconSize(1.0)
                iconAllowOverlap(true)
                iconIgnorePlacement(true)
                iconAnchor(IconAnchor.CENTER)
            }
        )
    }
}

private fun handleMapClick(
    mapboxMap: com.mapbox.maps.MapboxMap,
    camera: com.mapbox.maps.plugin.animation.CameraAnimationsPlugin,
    point: Point,
    peopleById: Map<String, MapPerson>,
    onPersonSelected: (MapPerson) -> Unit
): Boolean {
    val screenPoint = mapboxMap.pixelForCoordinate(point)
    val geometry = RenderedQueryGeometry(ScreenCoordinate(screenPoint.x, screenPoint.y))
    val clusterOptions = RenderedQueryOptions(listOf(CLUSTER_LAYER_ID), null)
    mapboxMap.queryRenderedFeatures(geometry, clusterOptions) clusterResult@ { expected ->
        val clusterFeature = expected.value?.firstOrNull()?.feature
        if (clusterFeature != null) {
            val clusterPoint = (clusterFeature.geometry() as? Point) ?: point
            val nextZoom = (mapboxMap.cameraState.zoom + 1.2).coerceAtMost(16.5)
            camera.easeTo(
                CameraOptions.Builder()
                    .center(clusterPoint)
                    .zoom(nextZoom)
                    .build(),
                MapAnimationOptions.mapAnimationOptions { duration(420) }
            )
            return@clusterResult
        }
        val personOptions = RenderedQueryOptions(listOf(UNCLUSTERED_LAYER_ID), null)
        mapboxMap.queryRenderedFeatures(geometry, personOptions) peopleResult@ { peopleExpected ->
            val personFeature = peopleExpected.value?.firstOrNull()?.feature ?: return@peopleResult
            val personId = personFeature.getStringProperty("id")
            val person = peopleById[personId] ?: return@peopleResult
            onPersonSelected(person)
        }
    }
    return true
}

private fun ensureClusterIcon(
    style: Style,
    density: androidx.compose.ui.unit.Density,
    iconCache: MutableMap<String, Bitmap>,
    addedIconIds: MutableSet<String>
) {
    if (addedIconIds.contains(CLUSTER_ICON_ID)) {
        return
    }
    val sizePx = with(density) { CLUSTER_ICON_SIZE.roundToPx() }
    val clusterIcon = iconCache.getOrPut(CLUSTER_ICON_ID) {
        createClusterIconBitmap(sizePx)
    }
    safeAddImage(style, CLUSTER_ICON_ID, clusterIcon)
    addedIconIds.add(CLUSTER_ICON_ID)
}

private fun addOrUpdatePersonIcons(
    style: Style,
    people: List<MapPerson>,
    context: android.content.Context,
    density: androidx.compose.ui.unit.Density,
    iconCache: MutableMap<String, Bitmap>,
    requestedAvatarIds: MutableSet<String>,
    addedIconIds: MutableSet<String>,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    imageLoader: coil.ImageLoader
) {
    val sizePx = with(density) { PERSON_ICON_SIZE.roundToPx() }
    val borderColor = AndroidColor.parseColor("#FFFFFF")
    people.forEach { person ->
        val iconId = personIconId(person)
        val initials = nameInitials(person.name)
        val backgroundColor = colorForName(person.name)
        val placeholder = iconCache.getOrPut(iconId) {
            createInitialsMarkerBitmap(
                initials = initials,
                sizePx = sizePx,
                backgroundColor = backgroundColor,
                borderColor = borderColor
            )
        }
        if (!addedIconIds.contains(iconId)) {
            safeAddImage(style, iconId, placeholder)
            addedIconIds.add(iconId)
        }
        if (person.avatarUrl.isNotBlank() && requestedAvatarIds.add(iconId)) {
            coroutineScope.launch(Dispatchers.IO) {
                val avatarBitmap = loadAvatarBitmap(imageLoader, context, person.avatarUrl, sizePx)
                if (avatarBitmap != null) {
                    val avatarMarker = createAvatarMarkerBitmap(
                        avatar = avatarBitmap,
                        sizePx = sizePx,
                        borderColor = borderColor,
                        backgroundColor = backgroundColor
                    )
                    iconCache[iconId] = avatarMarker
                    withContext(Dispatchers.Main) {
                        safeAddImage(style, iconId, avatarMarker)
                    }
                }
            }
        }
    }
}

private fun safeAddImage(style: Style, imageId: String, bitmap: Bitmap) {
    runCatching { style.addImage(imageId, bitmap, false) }
}

private suspend fun loadAvatarBitmap(
    imageLoader: coil.ImageLoader,
    context: android.content.Context,
    url: String,
    sizePx: Int
): Bitmap? {
    val request = coil.request.ImageRequest.Builder(context)
        .data(url)
        .size(sizePx)
        .allowHardware(false)
        .build()
    val result = imageLoader.execute(request)
    return result.drawable?.toBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
}

private fun personIconId(person: MapPerson): String {
    return "$PERSON_ICON_PREFIX${person.id}"
}

private fun nameInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    if (parts.isEmpty()) {
        return ""
    }
    val firstWord = parts.first()
    val firstChar = firstWord.firstOrNull { it.isLetterOrDigit() }
    val lastChar = parts.last().firstOrNull { it.isLetter() }
    val initials = if (parts.size >= 2 && firstChar != null && lastChar != null) {
        "$firstChar$lastChar"
    } else {
        firstWord.filter { it.isLetterOrDigit() }.take(2)
    }
    return initials.uppercase()
}

private fun colorForName(name: String): Int {
    if (MARKER_PALETTE.isEmpty()) {
        return AndroidColor.parseColor("#0EA5E9")
    }
    val index = abs(name.hashCode()) % MARKER_PALETTE.size
    return MARKER_PALETTE[index]
}

private fun createInitialsMarkerBitmap(
    initials: String,
    sizePx: Int,
    backgroundColor: Int,
    borderColor: Int
): Bitmap {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val radius = sizePx / 2f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val borderWidth = sizePx * 0.08f

    paint.style = Paint.Style.FILL
    paint.color = backgroundColor
    canvas.drawCircle(radius, radius, radius, paint)

    paint.style = Paint.Style.STROKE
    paint.strokeWidth = borderWidth
    paint.color = borderColor
    canvas.drawCircle(radius, radius, radius - borderWidth / 2f, paint)

    if (initials.isNotBlank()) {
        paint.style = Paint.Style.FILL
        paint.color = AndroidColor.parseColor("#FFFFFF")
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = sizePx * 0.42f
        val textBounds = Rect()
        paint.getTextBounds(initials, 0, initials.length, textBounds)
        val textY = radius - textBounds.exactCenterY()
        canvas.drawText(initials, radius, textY, paint)
    }
    return bitmap
}

private fun createAvatarMarkerBitmap(
    avatar: Bitmap,
    sizePx: Int,
    borderColor: Int,
    backgroundColor: Int
): Bitmap {
    val output = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val radius = sizePx / 2f
    val borderWidth = sizePx * 0.08f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.style = Paint.Style.FILL
    paint.color = backgroundColor
    canvas.drawCircle(radius, radius, radius, paint)

    val scaled = Bitmap.createScaledBitmap(avatar, sizePx, sizePx, true)
    val shader = BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    paint.shader = shader
    canvas.drawCircle(radius, radius, radius - borderWidth, paint)

    paint.shader = null
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = borderWidth
    paint.color = borderColor
    canvas.drawCircle(radius, radius, radius - borderWidth / 2f, paint)
    return output
}

private fun createClusterIconBitmap(sizePx: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val radius = sizePx / 2f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.style = Paint.Style.FILL
    paint.color = AndroidColor.parseColor("#2563EB")
    canvas.drawCircle(radius, radius, radius, paint)

    paint.color = AndroidColor.parseColor("#FFFFFF")
    val headRadius = sizePx * 0.16f
    canvas.drawCircle(sizePx * 0.42f, sizePx * 0.42f, headRadius, paint)
    canvas.drawCircle(sizePx * 0.6f, sizePx * 0.46f, headRadius, paint)
    val bodyTop = sizePx * 0.58f
    val bodyLeft = sizePx * 0.28f
    val bodyRight = sizePx * 0.74f
    val bodyBottom = sizePx * 0.78f
    val cornerRadius = sizePx * 0.16f
    canvas.drawRoundRect(bodyLeft, bodyTop, bodyRight, bodyBottom, cornerRadius, cornerRadius, paint)
    return bitmap
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
    selectedPerson: MapPerson?,
    onContact: () -> Unit
) {
    val name = selectedPerson?.name ?: stringResource(traveler.nameRes)
    val avatarUrl = selectedPerson?.avatarUrl ?: traveler.avatarUrl
    val rating = selectedPerson?.rating ?: traveler.rating
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
                        url = avatarUrl,
                        contentDescription = name,
                        size = 64.dp,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = name, style = MaterialTheme.typography.titleLarge)
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
                            Text(text = rating, style = MaterialTheme.typography.labelMedium)
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
                    text = stringResource(R.string.common_contact_name, name),
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
