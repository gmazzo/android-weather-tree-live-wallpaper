package io.github.gmazzo.android.livewallpaper.weather.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.AppTheme
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditions
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.zhanghai.compose.preference.LocalPreferenceTheme
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.Preferences
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.defaultPreferenceFlow
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference

const val SETTING_LOCATION_ON = "location_on"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
internal fun SettingsScreen(
    preferences: MutableStateFlow<Preferences> = defaultPreferenceFlow(),
    weatherConditions: StateFlow<WeatherConditions> = MutableStateFlow(
        WeatherConditions(
            latitude = 37f, longitude = -2f, weatherType = WeatherType.RAIN
        )
    ),
    missingLocationPermission: MutableStateFlow<Boolean> = MutableStateFlow(true),
    onRequestLocationPermission: () -> Unit = {},
    onSetAsWallpaper: () -> Unit = {},
) {
    val weather by weatherConditions.collectAsState()
    val missingPermission by missingLocationPermission.collectAsState()

    AppTheme {
        ProvidePreferenceLocals(preferences) {
            Scaffold(
                topBar = { TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }) },
                bottomBar = {
                    val theme = LocalPreferenceTheme.current

                    Button(
                        modifier = Modifier.padding(theme.padding).fillMaxWidth(),
                        onClick = onSetAsWallpaper,) {
                        Text(text = stringResource(id = R.string.settings_set_as_wallpaper))
                    }
                }) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    preferenceCategory(key = "dynamic_scenes", title = {
                        Column {
                            Text(text = stringResource(id = R.string.settings_dynamic_scenes))
                            Text(
                                text = stringResource(id = R.string.settings_dynamic_scenes_summary),
                                fontWeight = FontWeight.Light
                            )
                        }
                    })
                    switchPreference(
                        key = SETTING_LOCATION_ON,
                        defaultValue = false,
                        icon = {
                            Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null)
                        },
                        title = { Text(text = stringResource(id = R.string.settings_use_location)) },
                        summary = {
                            Text(text = stringResource(id = R.string.settings_use_location_summary))
                            if (weather.isValid) {
                                SuggestionChip(label = {
                                    Text(
                                        text = stringResource(
                                            R.string.settings_use_location_current,
                                            weather.latitude,
                                            weather.longitude
                                        )
                                    )
                                }, icon = {
                                    val iconId = when (weather.weatherType.scene) {
                                        SceneMode.CLEAR -> R.drawable.ic_weather_clear
                                        SceneMode.CLOUDY -> R.drawable.ic_weather_cloudy
                                        SceneMode.FOG -> R.drawable.ic_weather_fog
                                        SceneMode.RAIN -> R.drawable.ic_weather_rain
                                        SceneMode.SNOW -> R.drawable.ic_weather_snow
                                        SceneMode.STORM -> R.drawable.ic_weather_storm
                                    }

                                    Icon(
                                        painter = painterResource(iconId),
                                        contentDescription = weather.weatherType.name
                                    )
                                }, onClick = {})
                            }
                        },
                    )
                    if (missingPermission) {
                        missingLocationPermission(onRequestLocationPermission)
                    }
                }
            }
        }
    }
}

private fun LazyListScope.missingLocationPermission(onRequestLocationPermission: () -> Unit) =
    item {
        val theme = LocalPreferenceTheme.current

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
            modifier = Modifier.padding(theme.padding),
        ) {
            Preference(icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null
                )
            },
                title = { Text(stringResource(R.string.settings_location_permission_required)) },
                widgetContainer = {
                    Button(
                        modifier = Modifier.padding(theme.horizontalSpacing),
                        onClick = onRequestLocationPermission,
                    ) {
                        Text(text = stringResource(R.string.settings_location_permission_grant))
                    }
                })
        }
    }
