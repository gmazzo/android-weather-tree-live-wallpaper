package io.github.gmazzo.android.livewallpaper.weather.settings

import android.graphics.Typeface
import android.os.Build
import android.text.Spanned
import android.text.format.DateUtils
import android.text.format.DateUtils.formatDateTime
import android.text.style.StyleSpan
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.ContentPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.minutesSinceMidnight
import io.github.gmazzo.android.livewallpaper.weather.theme.Speed
import io.github.gmazzo.android.livewallpaper.weather.theme.TimeOfDay
import java.time.ZonedDateTime
import kotlin.math.min
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

private const val opacity = .6f
private val margin = 8.dp
private val timeSpeeds = listOf(
    1f to R.string.settings_speed_realtime,
    (1.days / 15.seconds).toFloat() to R.string.settings_speed_day_in_15secs,
    (1.days / 3.seconds).toFloat() to R.string.settings_speed_day_in_3secs,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SettingsScreen(
    now: ZonedDateTime = ZonedDateTime.now(),
    timeSpeed: Float = 1f,
    updateLocationEnabled: Boolean = true,
    weather: WeatherType = WeatherType.SUNNY_DAY,
    missingLocationPermission: Boolean = true,
    updateLocationEnabledChange: (Boolean) -> Unit = {},
    onSpeedSelected: (Float) -> Unit = {},
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
                CenterAlignedTopAppBar(title = { Text(text = stringResource(id = weather.scene.textId)) },
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
                        TimeSpeedMenu(timeSpeed, onSpeedSelected)
                        FilledIconButton(onClick = onSetAsWallpaper) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(id = R.string.settings_set_as_wallpaper),
                            )
                        }
                    })
                DayTimeProgression(now)
            }
        }) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(margin)
                    .fillMaxSize()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
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
                WeathersGallery(weather.scene, onSceneSelected)
            }
        }
    }
}

@Composable
private fun TimeSpeedMenu(
    selectedSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    val selected = timeSpeeds.find { it.first == selectedSpeed }

    @Composable
    fun SpeedIcon() = Icon(
        imageVector = Icons.Speed,
        contentDescription = stringResource(R.string.settings_speed)
    )

    Box(modifier = Modifier.animateContentSize()) {
        if (showMenu) {
            Button(
                contentPadding = PaddingValues(ContentPadding.calculateTopPadding()),
                onClick = { showMenu = !showMenu },
            ) {
                SpeedIcon()
                if (selected != null) {
                    Text(
                        text = stringResource(selected.second),
                        modifier = Modifier.padding(start = margin)
                    )
                }
            }

        } else {
            OutlinedIconButton(onClick = { showMenu = !showMenu }) {
                SpeedIcon()
            }
        }
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        timeSpeeds.forEach {
            DropdownMenuItem(
                text = { Text(text = stringResource(it.second)) },
                trailingIcon = {
                    if (it == selected) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                        )
                    }
                },
                onClick = { onSpeedSelected(it.first) })
        }
    }
}

@Composable
private fun DayTimeProgression(
    now: ZonedDateTime,
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = margin),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    val iconSize = 24.dp
    val color = MaterialTheme.colorScheme.primary.copy(alpha = opacity)
    val progress = (1 - now.minutesSinceMidnight / 1.days).toFloat()
    val night = progress >= .5f
    val startFraction = min(progress, (.5f + progress) % 1)
    val middleFraction = startFraction + .5f
    val endFraction = min(startFraction + 1f, 1f)

    fun Float.asAlpha() = when {
        this <= .1f -> this / .1f
        this >= .9f -> (1f - this) / .1f
        else -> 1f
    }

    val startAlpha = startFraction.asAlpha()
    val middleAlpha = middleFraction.asAlpha()
    val endAlpha = endFraction.asAlpha()

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val track = Modifier
            .height(2.dp)
            .padding(horizontal = margin)
            .background(color, MaterialTheme.shapes.large)

        val guidelineStart = createGuidelineFromStart(startFraction)
        val guidelineMiddle = createGuidelineFromStart(middleFraction)
        val guidelineEnd = createGuidelineFromStart(endFraction)

        val (spacerStart, startIcon, spacerMiddle, middleIcon, spacerEnd, endIcon) = createRefs()

        Spacer(modifier = track.constrainAs(spacerStart) {
            start.linkTo(parent.start, margin = iconSize / 2)
            end.linkTo(startIcon.start)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        })
        Icon(
            modifier = Modifier
                .size(iconSize)
                .alpha(startAlpha)
                .constrainAs(startIcon) {
                    start.linkTo(guidelineStart)
                    end.linkTo(guidelineStart)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            tint = color,
            imageVector = if (night) Icons.TimeOfDay.Night else Icons.TimeOfDay.Day,
            contentDescription = null
        )
        Spacer(modifier = track.constrainAs(spacerMiddle) {
            start.linkTo(startIcon.end)
            end.linkTo(middleIcon.start)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        })
        Icon(
            modifier = Modifier
                .size(iconSize)
                .alpha(middleAlpha)
                .constrainAs(middleIcon) {
                    start.linkTo(guidelineMiddle)
                    end.linkTo(guidelineMiddle)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            tint = color,
            imageVector = if (night) Icons.TimeOfDay.Day else Icons.TimeOfDay.Night,
            contentDescription = null
        )
        Spacer(modifier = track
            .alpha(middleAlpha)
            .constrainAs(spacerEnd) {
                start.linkTo(middleIcon.end)
                end.linkTo(endIcon.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            })
        Icon(
            modifier = Modifier
                .size(iconSize)
                .alpha(endAlpha)
                .constrainAs(endIcon) {
                    start.linkTo(guidelineEnd)
                    end.linkTo(guidelineEnd)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            tint = color,
            imageVector = if (night) Icons.TimeOfDay.Night else Icons.TimeOfDay.Day,
            contentDescription = null
        )
    }
    Text(
        modifier = Modifier.padding(top = margin),
        color = color,
        style = MaterialTheme.typography.titleMedium,
        text = formatDateTime(
            LocalContext.current, now.toEpochSecond() * 1000,
            DateUtils.FORMAT_SHOW_DATE or
                    DateUtils.FORMAT_SHOW_TIME or
                    DateUtils.FORMAT_ABBREV_ALL or
                    DateUtils.FORMAT_NO_YEAR
        )
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

@Composable
private fun WeathersGallery(
    selected: SceneMode,
    onSceneSelected: (SceneMode) -> Unit,
) = Selector(
    entries = SceneMode.entries,
    selected = selected,
    onSelection = onSceneSelected,
) { scene ->
    Icon(
        modifier = Modifier.fillMaxSize(.6f),
        imageVector = scene.icon,
        contentDescription = scene.name,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <Value> Selector(
    entries: List<Value>,
    selected: Value,
    onSelection: (Value) -> Unit,
    label: @Composable (Value) -> Unit,
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
        entries.forEachIndexed { index, value ->
            SegmentedButton(
                selected = value == selected,
                shape = when (index) {
                    0 -> shapeStart
                    entries.size - 1 -> shapeEnd
                    else -> shapeMiddle
                },
                colors = colors,
                icon = {},
                onClick = { onSelection(value) },
            ) { label(value) }
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
