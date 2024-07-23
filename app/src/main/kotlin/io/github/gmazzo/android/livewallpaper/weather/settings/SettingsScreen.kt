package io.github.gmazzo.android.livewallpaper.weather.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditions
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SettingsScreen(
    modal: Boolean = true,
    updateLocationEnabled: Boolean = true,
    weatherConditions: WeatherConditions = WeatherConditions(
        latitude = 37f, longitude = -2f, weatherType = WeatherType.RAIN
    ),
    missingLocationPermission: Boolean = true,
    updateLocationEnabledChange: (Boolean) -> Unit = {},
    onRequestLocationPermission: () -> Unit = {},
    onSetAsWallpaper: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    AppTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        }, bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = onSetAsWallpaper,
            ) {
                Text(text = stringResource(id = R.string.settings_set_as_wallpaper))
            }
        }) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (modal) {
                    Spacer(modifier = Modifier.weight(1f))
                }
                SettingsCategory(
                    title = { Text(text = stringResource(R.string.settings_scenes)) },
                )
                WeathersGallery(weatherConditions)
                SettingsItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                        )
                    },
                    title = { Text(text = stringResource(id = R.string.settings_use_location)) },
                    summary = { Text(text = stringResource(id = R.string.settings_use_location_summary)) },
                ) {
                    Switch(
                        checked = updateLocationEnabled,
                        onCheckedChange = updateLocationEnabledChange
                    )
                }
                if (missingLocationPermission) {
                    MissingLocationPermissionPanel(onRequestLocationPermission)
                }
            }
        }
    }
}

@Composable
private fun SettingsCategory(
    title: @Composable () -> Unit = {},
    summary: @Composable () -> Unit = {},
) = Surface(color = Color.Transparent) {
    Column(modifier = Modifier.padding(8.dp)) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.secondary,
            LocalTextStyle provides MaterialTheme.typography.labelLarge,
            content = title
        )
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.tertiary,
            LocalTextStyle provides MaterialTheme.typography.labelMedium,
            content = summary
        )
    }
}

@Composable
private fun SettingsItem(
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit = {},
    summary: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    widget: @Composable (() -> Unit)? = null,
) = Surface(color = Color.Transparent, onClick = onClick) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (icon != null) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                content = icon
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = (if (widget != null) 8 else 0).dp)
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                content = title
            )
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                content = summary
            )
        }
        widget?.invoke()
    }
}

@Composable
private fun WeathersGallery(weatherConditions: WeatherConditions) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        userScrollEnabled = false,
    ) {
        items(SceneMode.entries) { scene ->
            val selected = scene == weatherConditions.weatherType.scene

            OutlinedIconToggleButton(
                modifier = Modifier.aspectRatio(1f),
                shape = MaterialTheme.shapes.large,
                colors = IconButtonDefaults.outlinedIconToggleButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                ),
                checked = selected,
                onCheckedChange = {},
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(.6f),
                    imageVector = scene.icon,
                    contentDescription = scene.name,
                )
            }
        }
    }
}

@Composable
private fun MissingLocationPermissionPanel(onRequestLocationPermission: () -> Unit) = Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
) {
    SettingsItem(
        icon = { Icon(imageVector = Icons.Outlined.Warning, contentDescription = null) },
        summary = { Text(stringResource(R.string.settings_location_permission_required)) },
    ) {
        Button(onClick = onRequestLocationPermission) {
            Text(text = stringResource(R.string.settings_location_permission_grant))
        }
    }
}
