package io.github.gmazzo.android.livewallpaper.weather.settings

import android.graphics.Typeface
import android.os.Build
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.compose.AppTheme
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherState
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.minutesSinceMidnight
import io.github.gmazzo.android.livewallpaper.weather.theme.WeatherIcons
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.ZonedDateTime
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

private const val opacity = .6f
private val margin = 8.dp
private val sampleConditions = MutableStateFlow(WeatherState())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SettingsScreen(
    now: ZonedDateTime = ZonedDateTime.now(),
    updateLocationEnabled: Boolean = true,
    weatherState: WeatherState = sampleConditions.value,
    missingLocationPermission: Boolean = true,
    updateLocationEnabledChange: (Boolean) -> Unit = {},
    onSceneSelected: (SceneMode) -> Unit = {},
    onRequestLocationPermission: () -> Unit = {},
    onSetAsWallpaper: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    surfaceView: @Composable () -> Unit = {
        Surface(
            color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.fillMaxSize()
        ) {}
    },
) {
    AppTheme {
        Box(Modifier.fillMaxSize()) { surfaceView() }
        Scaffold(containerColor = Color.Transparent, topBar = {
            Column {
                CenterAlignedTopAppBar(title = { Text(text = stringResource(id = weatherState.weatherType.scene.textId)) },
                    colors = TopAppBarDefaults.topAppBarColors()
                        .copy(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        FilledIconButton(onClick = onSetAsWallpaper) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(id = R.string.settings_set_as_wallpaper),
                            )
                        }
                    })
                DayTimeProgression(now.minutesSinceMidnight)
            }
        }) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(margin)
                    .fillMaxSize()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(margin, Alignment.Bottom),
            ) {
                SettingsItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
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
                WeathersGallery(weatherState, onSceneSelected)
            }
        }
    }
}

@Composable
private fun DayTimeProgression(
    minutes: Duration,
) = ConstraintLayout(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = margin),
) {
    val color = MaterialTheme.colorScheme.primary.copy(alpha = opacity)
    val track = Modifier
        .height(2.dp)
        .padding(horizontal = margin)
        .background(color, MaterialTheme.shapes.large)

    val progress = (1 - minutes / 1.days).toFloat()
    val night = progress >= .5f
    val startFraction = min(progress, (.5f + progress) % 1)
    val middleFraction = startFraction + .5f
    val endFraction = min(startFraction + 1f, 1f)

    val guidelineStart = createGuidelineFromStart(startFraction)
    val guidelineMiddle = createGuidelineFromStart(middleFraction)
    val guidelineEnd = createGuidelineFromStart(endFraction)

    fun Float.asAlpha() = when {
        this <= .1f -> this / .1f
        this >= .9f -> (1f - this) / .1f
        else -> 1f
    }
    val startAlpha = startFraction.asAlpha()
    val middleAlpha = middleFraction.asAlpha()
    val endAlpha = endFraction.asAlpha()

    val (startIcon, middleIcon, endIcon) = createRefs()

    Spacer(modifier = track.constrainAs(createRef()) {
        start.linkTo(parent.start)
        end.linkTo(startIcon.start)
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
    })
    Icon(
        modifier = Modifier
            .constrainAs(startIcon) {
                start.linkTo(guidelineStart)
                end.linkTo(guidelineStart)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .alpha(startAlpha),
        tint = color,
        imageVector = if (night) WeatherIcons.night else WeatherIcons.day,
        contentDescription = null
    )
    Spacer(modifier = track.constrainAs(createRef()) {
        start.linkTo(startIcon.end)
        end.linkTo(middleIcon.start)
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
    })
    Icon(
        modifier = Modifier
            .constrainAs(middleIcon) {
                start.linkTo(guidelineMiddle)
                end.linkTo(guidelineMiddle)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .alpha(middleAlpha),
        tint = color,
        imageVector =if (night) WeatherIcons.day else WeatherIcons.night,
        contentDescription = null
    )
    Spacer(modifier = track.alpha(middleAlpha).constrainAs(createRef()) {
        start.linkTo(middleIcon.end)
        end.linkTo(endIcon.start)
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
    })
    Icon(
        modifier = Modifier
            .constrainAs(endIcon) {
                start.linkTo(guidelineEnd)
                end.linkTo(guidelineEnd)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .alpha(endAlpha),
        tint = color,
        imageVector = if (night) WeatherIcons.night else WeatherIcons.day,
        contentDescription = null
    )
}

@Composable
private fun SettingsItem(
    containerColor: Color? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit = {},
    summary: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    widget: @Composable (() -> Unit)? = null,
) = Card(
    colors = CardDefaults.cardColors().let {
        it.copy(containerColor = (containerColor ?: it.containerColor).copy(alpha = .6f))
    }, onClick = onClick
) {
    Row(
        modifier = Modifier.padding(margin),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (icon != null) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                content = icon
            )
            Spacer(modifier = Modifier.width(margin))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = (if (widget != null) margin else 0.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeathersGallery(
    weatherState: WeatherState,
    onSceneSelected: (SceneMode) -> Unit,
) {
    val noSize = CornerSize(0.dp)
    val shape = MaterialTheme.shapes.extraLarge
    val shapeStart = shape.copy(topEnd = noSize, bottomEnd = noSize)
    val shapeMiddle = shape.copy(all = noSize)
    val shapeEnd = shape.copy(topStart = noSize, bottomStart = noSize)
    val colors = SegmentedButtonDefaults.colors().let {
        it.copy(
            activeContainerColor = it.activeContainerColor.copy(alpha = opacity),
            inactiveContainerColor = it.inactiveContainerColor.copy(alpha = opacity),
        )
    }

    SingleChoiceSegmentedButtonRow {
        SceneMode.entries.forEachIndexed { index, scene ->
            SegmentedButton(
                selected = scene == weatherState.weatherType.scene,
                shape = when (index) {
                    0 -> shapeStart
                    SceneMode.entries.size - 1 -> shapeEnd
                    else -> shapeMiddle
                },
                colors = colors,
                icon = {},
                onClick = { onSceneSelected(scene) },
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
private fun MissingLocationPermissionPanel(onRequestLocationPermission: () -> Unit) = SettingsItem(
    containerColor = MaterialTheme.colorScheme.errorContainer,
    icon = { Icon(imageVector = Icons.Outlined.Warning, contentDescription = null) },
    title = { Text(textResource(R.string.settings_missing_location_permission_title)) },
    summary = { Text(missingLocationPermissionExplanation()) },
) {
    Button(onClick = onRequestLocationPermission) {
        Text(text = stringResource(R.string.settings_missing_location_permission_grant))
    }
}

@Composable
@ReadOnlyComposable
fun missingLocationPermissionExplanation() = buildAnnotatedString {
    append(textResource(R.string.settings_missing_location_permission_summary))

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
            appendLine()
            append(stringResource(R.string.settings_missing_location_permission_summary_instructions))
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(LocalContext.current.packageManager.backgroundPermissionOptionLabel)
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun textResource(@StringRes id: Int) = buildAnnotatedString {
    val text = LocalContext.current.resources.getText(id)
    append(text)
    (text as? Spanned)?.getSpans(0, text.length, StyleSpan::class.java)?.forEach {
        addStyle(
            start = text.getSpanStart(it), end = text.getSpanEnd(it), style = when (it.style) {
                Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                Typeface.BOLD_ITALIC -> SpanStyle(
                    fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic
                )

                else -> SpanStyle()
            }
        )
    }
}
