package com.berco.spaceflightnews.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.berco.spaceflightnews.core.ui.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.readableWidth
import com.berco.spaceflightnews.core.ui.preview.PreviewSamples
import com.berco.spaceflightnews.core.ui.theme.OrganicRadius
import com.berco.spaceflightnews.core.ui.theme.SpaceflightTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPane(
    article: Article,
    dateLabel: String?,
    onBack: (() -> Unit)?,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onReadFullArticle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.readableWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        // Content runs edge to edge; the trailing spacer keeps the last line
        // reachable above the gesture bar.
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(OrganicIcons.ArrowLeft, contentDescription = stringResource(R.string.cd_back))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (article.isFavorite) {
                                OrganicIcons.HeartFilled
                            } else {
                                OrganicIcons.HeartOutline
                            },
                            contentDescription = if (article.isFavorite) {
                                stringResource(R.string.cd_remove_from_favorites)
                            } else {
                                stringResource(R.string.cd_add_to_favorites)
                            },
                            tint = if (article.isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                    IconButton(onClick = onShare) {
                        Icon(OrganicIcons.Share, contentDescription = stringResource(R.string.cd_share_article))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            WashedImage(
                url = article.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .height(268.dp)
                    .clip(RoundedCornerShape(OrganicRadius.Lg)),
            )

            Column(Modifier.padding(start = 24.dp, top = 22.dp, end = 24.dp, bottom = 32.dp)) {
                Text(article.title, style = MaterialTheme.typography.headlineLarge)

                Spacer(Modifier.height(16.dp))
                MetaBlock(article, dateLabel)

                if (article.summary.isNotBlank()) {
                    Spacer(Modifier.height(24.dp))
                    Text(article.summary, style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(Modifier.height(26.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.outlineVariant,
                    ).forEach { color ->
                        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
                    }
                }

                Spacer(Modifier.height(26.dp))
                Button(
                    onClick = onReadFullArticle,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 24.dp,
                        vertical = 16.dp,
                    ),
                ) {
                    Text(
                        stringResource(R.string.detail_read_full_article),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        OrganicIcons.ExternalLink,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.detail_attribution, article.newsSite),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun MetaBlock(article: Article, dateLabel: String?) {
    // Several feeds set the author to the publication itself; showing both just
    // repeats the name.
    val author = article.authors.firstOrNull()
        ?.takeIf { !it.equals(article.newsSite, ignoreCase = true) }

    Column {
        Text(
            text = article.newsSite.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        if (author != null || dateLabel != null) {
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (author != null) {
                    Text(
                        text = author,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        // Yields to the date so a long byline can never break it.
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (dateLabel != null) {
                        Box(
                            Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outline),
                        )
                    }
                }
                if (dateLabel != null) {
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Preview(name = "Detail", widthDp = 412, heightDp = 915)
@Composable
private fun DetailPanePreview() {
    SpaceflightTheme {
        DetailPane(
            article = PreviewSamples.article,
            dateLabel = "5 Sep 2026",
            onBack = {},
            onToggleFavorite = {},
            onShare = {},
            onReadFullArticle = {},
        )
    }
}

@Preview(name = "Detail — sparse", widthDp = 412, heightDp = 915)
@Composable
private fun DetailPaneSparsePreview() {
    SpaceflightTheme {
        DetailPane(
            article = PreviewSamples.sparse,
            dateLabel = null,
            onBack = null,
            onToggleFavorite = {},
            onShare = {},
            onReadFullArticle = {},
        )
    }
}
