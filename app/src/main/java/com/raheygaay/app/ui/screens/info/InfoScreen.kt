package com.raheygaay.app.ui.screens.info

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.raheygaay.app.ui.screens.more.InfoPage
import com.raheygaay.app.R
import com.raheygaay.app.ui.components.InlineNavProgress

@Composable
fun InfoScreen(
    page: InfoPage,
    onBack: () -> Unit
) {
    val content = infoContentFor(page.id)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        InfoHeader(onBack = onBack)
        Text(
            text = stringResource(page.titleRes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(page.subtitleRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        InlineNavProgress()
        InfoIntro(text = stringResource(content.introRes))
        InfoSection(
            title = stringResource(R.string.info_section_key_points),
            bullets = content.keyPoints.map { stringResource(it) }
        )
        InfoSection(
            title = stringResource(R.string.info_section_next_steps),
            bullets = content.nextSteps.map { stringResource(it) }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun InfoHeader(onBack: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun InfoIntro(text: String) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun InfoSection(title: String, bullets: List<String>) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                bullets.forEach { bullet ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .padding(top = 6.dp)
                                .background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

private data class InfoContent(
    val introRes: Int,
    val keyPoints: List<Int>,
    val nextSteps: List<Int>
)

private fun infoContentFor(pageId: String): InfoContent {
    return when (pageId) {
        "company" -> InfoContent(
            introRes = R.string.info_company_intro,
            keyPoints = listOf(
                R.string.info_company_key_1,
                R.string.info_company_key_2,
                R.string.info_company_key_3
            ),
            nextSteps = listOf(
                R.string.info_company_next_1,
                R.string.info_company_next_2
            )
        )
        "about" -> InfoContent(
            introRes = R.string.info_about_intro,
            keyPoints = listOf(
                R.string.info_about_key_1,
                R.string.info_about_key_2,
                R.string.info_about_key_3
            ),
            nextSteps = listOf(
                R.string.info_about_next_1,
                R.string.info_about_next_2
            )
        )
        "careers" -> InfoContent(
            introRes = R.string.info_careers_intro,
            keyPoints = listOf(
                R.string.info_careers_key_1,
                R.string.info_careers_key_2,
                R.string.info_careers_key_3
            ),
            nextSteps = listOf(
                R.string.info_careers_next_1,
                R.string.info_careers_next_2
            )
        )
        "press" -> InfoContent(
            introRes = R.string.info_press_intro,
            keyPoints = listOf(
                R.string.info_press_key_1,
                R.string.info_press_key_2,
                R.string.info_press_key_3
            ),
            nextSteps = listOf(
                R.string.info_press_next_1,
                R.string.info_press_next_2
            )
        )
        "blog" -> InfoContent(
            introRes = R.string.info_blog_intro,
            keyPoints = listOf(
                R.string.info_blog_key_1,
                R.string.info_blog_key_2,
                R.string.info_blog_key_3
            ),
            nextSteps = listOf(
                R.string.info_blog_next_1,
                R.string.info_blog_next_2
            )
        )
        "support" -> InfoContent(
            introRes = R.string.info_support_intro,
            keyPoints = listOf(
                R.string.info_support_key_1,
                R.string.info_support_key_2,
                R.string.info_support_key_3
            ),
            nextSteps = listOf(
                R.string.info_support_next_1,
                R.string.info_support_next_2
            )
        )
        "help-center" -> InfoContent(
            introRes = R.string.info_help_center_intro,
            keyPoints = listOf(
                R.string.info_help_center_key_1,
                R.string.info_help_center_key_2,
                R.string.info_help_center_key_3
            ),
            nextSteps = listOf(
                R.string.info_help_center_next_1,
                R.string.info_help_center_next_2
            )
        )
        "safety" -> InfoContent(
            introRes = R.string.info_safety_intro,
            keyPoints = listOf(
                R.string.info_safety_key_1,
                R.string.info_safety_key_2,
                R.string.info_safety_key_3
            ),
            nextSteps = listOf(
                R.string.info_safety_next_1,
                R.string.info_safety_next_2
            )
        )
        "community" -> InfoContent(
            introRes = R.string.info_community_intro,
            keyPoints = listOf(
                R.string.info_community_key_1,
                R.string.info_community_key_2,
                R.string.info_community_key_3
            ),
            nextSteps = listOf(
                R.string.info_community_next_1,
                R.string.info_community_next_2
            )
        )
        "contact" -> InfoContent(
            introRes = R.string.info_contact_intro,
            keyPoints = listOf(
                R.string.info_contact_key_1,
                R.string.info_contact_key_2,
                R.string.info_contact_key_3
            ),
            nextSteps = listOf(
                R.string.info_contact_next_1,
                R.string.info_contact_next_2
            )
        )
        "terms" -> InfoContent(
            introRes = R.string.info_terms_intro,
            keyPoints = listOf(
                R.string.info_terms_key_1,
                R.string.info_terms_key_2,
                R.string.info_terms_key_3
            ),
            nextSteps = listOf(
                R.string.info_terms_next_1,
                R.string.info_terms_next_2
            )
        )
        "privacy" -> InfoContent(
            introRes = R.string.info_privacy_intro,
            keyPoints = listOf(
                R.string.info_privacy_key_1,
                R.string.info_privacy_key_2,
                R.string.info_privacy_key_3
            ),
            nextSteps = listOf(
                R.string.info_privacy_next_1,
                R.string.info_privacy_next_2
            )
        )
        "map" -> InfoContent(
            introRes = R.string.info_map_intro,
            keyPoints = listOf(
                R.string.info_map_key_1,
                R.string.info_map_key_2,
                R.string.info_map_key_3
            ),
            nextSteps = listOf(
                R.string.info_map_next_1,
                R.string.info_map_next_2
            )
        )
        "profile" -> InfoContent(
            introRes = R.string.info_profile_intro,
            keyPoints = listOf(
                R.string.info_profile_key_1,
                R.string.info_profile_key_2,
                R.string.info_profile_key_3
            ),
            nextSteps = listOf(
                R.string.info_profile_next_1,
                R.string.info_profile_next_2
            )
        )
        else -> InfoContent(
            introRes = R.string.info_company_intro,
            keyPoints = listOf(
                R.string.info_company_key_1,
                R.string.info_company_key_2,
                R.string.info_company_key_3
            ),
            nextSteps = listOf(
                R.string.info_company_next_1,
                R.string.info_company_next_2
            )
        )
    }
}
