package com.berco.spaceflightnews.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.preview.PreviewSurface
import com.berco.spaceflightnews.core.ui.theme.OrganicColors

/**
 * The bottom bar's counterpart for medium and wider windows, where vertical
 * space is the scarce axis. Takes the same items so the two stay in step.
 */
@Composable
fun OrganicNavigationRail(
    items: List<BottomNavItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        header = null,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items.forEach { item ->
                val selected = item.key == selectedKey
                NavigationRailItem(
                    selected = selected,
                    onClick = { onSelect(item.key) },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = if (selected) {
                                MaterialTheme.typography.labelLarge
                            } else {
                                MaterialTheme.typography.labelMedium
                            },
                        )
                    },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = OrganicColors.Accent200,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier.padding(vertical = 6.dp),
                )
            }
        }
    }
}

@Preview(name = "Navigation rail", widthDp = 120, heightDp = 420, showBackground = true)
@Composable
private fun OrganicNavigationRailPreview() {
    val items = listOf(
        BottomNavItem("feed", "Feed", OrganicIcons.Newspaper),
        BottomNavItem("favorites", "Favorites", OrganicIcons.HeartOutline, OrganicIcons.HeartFilled),
    )
    PreviewSurface {
        OrganicNavigationRail(items, selectedKey = "feed", onSelect = {})
    }
}
