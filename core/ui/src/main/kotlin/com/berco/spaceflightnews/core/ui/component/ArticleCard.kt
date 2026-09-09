package com.berco.spaceflightnews.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.berco.spaceflightnews.core.ui.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.berco.spaceflightnews.core.ui.preview.ArticleProvider
import com.berco.spaceflightnews.core.ui.preview.ComponentPreviews
import com.berco.spaceflightnews.core.ui.preview.PreviewSurface
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.theme.OrganicRadius
import com.berco.spaceflightnews.core.model.Article

@Composable
fun ArticleCard(
    article: Article,
    dateLabel: String?,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(OrganicRadius.Card),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 2.dp,
    ) {
        Box {
            Column(
                Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 8.dp),
            ) {
                WashedImage(
                    url = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(184.dp)
                        .clip(RoundedCornerShape(OrganicRadius.CardImage)),
                )

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp, top = 14.dp, end = 4.dp),
                )

                if (article.summary.isNotBlank()) {
                    Text(
                        text = article.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, end = 4.dp),
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp, top = 10.dp),
                ) {
                    SourceAndDate(article.newsSite, dateLabel, Modifier.weight(1f))
                    FavoriteButton(article.isFavorite, onToggleFavorite)
                }
            }

            if (pressed) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.09f)),
                )
            }
        }
    }
}

@Composable
private fun SourceAndDate(
    newsSite: String,
    dateLabel: String?,
    modifier: Modifier = Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = newsSite.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (dateLabel != null) {
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun FavoriteButton(isFavorite: Boolean, onToggle: () -> Unit) {
    // Resolved here because `semantics` is not a composable scope.
    val savedState = stringResource(
        if (isFavorite) R.string.state_saved else R.string.state_not_saved,
    )
    val favoriteAction = stringResource(
        if (isFavorite) R.string.cd_remove_from_favorites else R.string.cd_add_to_favorites,
    )

    IconButton(onClick = onToggle, modifier = Modifier.size(44.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .then(
                    if (isFavorite) {
                        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        Modifier
                    },
                )
                .semantics {
                    stateDescription = savedState
                },
        ) {
            Icon(
                imageVector = if (isFavorite) OrganicIcons.HeartFilled else OrganicIcons.HeartOutline,
                contentDescription = favoriteAction,
                tint = if (isFavorite) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(23.dp),
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun ArticleCardPreview(
    @PreviewParameter(ArticleProvider::class) article: Article,
) {
    PreviewSurface {
        ArticleCard(
            article = article,
            dateLabel = if (article.publishedAt == null) null else "2h ago",
            onClick = {},
            onToggleFavorite = {},
        )
    }
}
