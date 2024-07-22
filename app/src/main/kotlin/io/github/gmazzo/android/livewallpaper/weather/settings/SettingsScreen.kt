package io.github.gmazzo.android.livewallpaper.weather.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.AppTheme
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditions
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            latitude = 37f,
            longitude = -2f,
            weatherType = WeatherType.RAIN
        )
    ),
    onLocationOnChange: (Boolean) -> Boolean = { it }
) {
    val weather by weatherConditions.collectAsState()

    AppTheme {
        ProvidePreferenceLocals(preferences) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text(stringResource(id = R.string.app_name)) })
                },
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    preferenceCategory(
                        key = "dynamic_scenes",
                        title = {
                            Column {
                                Text(text = stringResource(id = R.string.settings_dynamic_scenes))
                                Text(
                                    text = stringResource(id = R.string.settings_dynamic_scenes_summary),
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    )
                    switchPreference(
                        key = SETTING_LOCATION_ON,
                        defaultValue = false,
                        title = { Text(text = stringResource(id = R.string.settings_use_location)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null
                            )
                        },
                        summary = {
                            Text(text = stringResource(id = R.string.settings_use_location_summary))
                            if (weather.latitude.isFinite() && weather.longitude.isFinite()) {
                                SuggestionChip(
                                    label = {
                                        Text(
                                            text = stringResource(
                                                R.string.settings_use_location_current,
                                                weather.latitude,
                                                weather.longitude
                                            )
                                        )
                                    },
                                    onClick = {}
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

