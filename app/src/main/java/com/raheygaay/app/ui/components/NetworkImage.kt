package com.raheygaay.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    size: Dp? = null
) {
    val context = LocalContext.current
    val sizePx = size?.let { with(LocalDensity.current) { it.roundToPx() } }
    val request = remember(url, sizePx, context) {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(false)
            .allowHardware(true)
            .precision(Precision.INEXACT)
            .apply {
                if (sizePx != null && sizePx > 0) {
                    size(sizePx, sizePx)
                }
            }
            .build()
    }
    val painter = rememberAsyncImagePainter(model = request)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}
