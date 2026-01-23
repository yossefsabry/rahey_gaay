package com.raheygaay.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.raheygaay.app.BuildConfig

@Composable
fun BrandLogo(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onPrimary
) {
    val context = LocalContext.current
    val logoRes = remember {
        context.resources.getIdentifier(BuildConfig.APP_LOGO, "drawable", context.packageName)
    }

    if (logoRes != 0) {
        Icon(
            painter = painterResource(id = logoRes),
            contentDescription = BuildConfig.APP_NAME,
            tint = tint,
            modifier = modifier
        )
    } else {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = BuildConfig.APP_NAME,
            tint = tint,
            modifier = modifier
        )
    }
}
