package com.mzdon.wheelpickercompose.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
internal fun DefaultWheelTimePicker(
    modifier: Modifier = Modifier,
    startTime: LocalTime = LocalTime.now(),
    dateLocale: Locale = Locale.getDefault(),
    minTime: LocalTime = LocalTime.MIN,
    maxTime: LocalTime = LocalTime.MAX,
    timeFormat: TimeFormat = TimeFormat.HOUR_24,
    size: DpSize = DpSize(128.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedTime: (snappedTime: SnappedTime, timeFormat: TimeFormat) -> Int? = { _, _ -> null },
    onLog: (log: String) -> Unit = {}
) {
    onLog("Recomposing - startTime: $startTime")

    var snappedTime by remember { mutableStateOf(startTime.truncatedTo(ChronoUnit.MINUTES)) }

    val hours = (0..23).map {
        Hour(
            text = it.toString().padStart(2, '0'),
            value = it,
            index = it
        )
    }
    val amPmHours = (1..12).map {
        AmPmHour(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }

    val minutes = (0..59).map {
        Minute(
            text = it.toString().padStart(2, '0'),
            value = it,
            index = it
        )
    }

    val amPms = listOf(
        AmPm(
            text = amPmStringByLocale(AmPmValue.AM, dateLocale),
            value = AmPmValue.AM,
            index = 0
        ),
        AmPm(
            text = amPmStringByLocale(AmPmValue.PM, dateLocale),
            value = AmPmValue.PM,
            index = 1
        )
    )

    var snappedAmPm by remember {
        mutableStateOf(
            amPms.find { it.value == amPmValueFromTime(startTime) } ?: amPms[0]
        )
    }

    onLog("snappedTime: $snappedTime, snappedAmPm: $snappedAmPm")

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (selectorProperties.enabled().value) {
            Surface(
                modifier = Modifier.size(size.width, size.height / rowCount),
                shape = selectorProperties.shape().value,
                color = selectorProperties.color().value,
                border = selectorProperties.border().value
            ) {}
        }
        Row {
            //Hour
            WheelTextPicker(
                size = DpSize(
                    width = size.width / if (timeFormat == TimeFormat.HOUR_24) 2 else 3,
                    height = size.height
                ),
                texts = if (timeFormat == TimeFormat.HOUR_24) hours.map { it.text } else amPmHours.map { it.text },
                rowCount = rowCount,
                style = textStyle,
                color = textColor,
                focusedIndex = if (timeFormat == TimeFormat.HOUR_24) {
                    hours.find { it.value == startTime.hour }?.index ?: 0
                } else amPmHours.find { it.value == localTimeToAmPmHour(startTime) }?.index ?: 0,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = false
                ),
                onScrollFinished = { snappedIndex ->
                    onLog("Hour onScollFinished - snappedIndex: $snappedIndex")

                    val newHour = if (timeFormat == TimeFormat.HOUR_24) {
                        hours.find { it.index == snappedIndex }?.value
                    } else {
                        amPmHourToHour24(
                            amPmHours.find { it.index == snappedIndex }?.value ?: 0,
                            snappedTime.minute,
                            snappedAmPm.value
                        )
                    }

                    var newIndex = if (timeFormat == TimeFormat.HOUR_24) {
                        hours.find { it.value == snappedTime.hour }?.index
                    } else {
                        amPmHours.find { it.value == localTimeToAmPmHour(snappedTime) }?.index
                    }

                    newHour?.let {

                        val newTime = snappedTime.withHour(newHour)

                        if (!newTime.isBefore(minTime) && !newTime.isAfter(maxTime)) {
                            snappedTime = newTime
                        }

                        newIndex = if (timeFormat == TimeFormat.HOUR_24) {
                            hours.find { it.value == snappedTime.hour }?.index
                        } else {
                            amPmHours.find { it.value == localTimeToAmPmHour(snappedTime) }?.index
                        }

                        newIndex?.let { idx ->
                            onLog("onSnappedTime from Hour picker - newIndex: $idx, snappedTime: $snappedTime")
                            onSnappedTime(
                                SnappedTime.Hour(
                                    localTime = snappedTime,
                                    index = idx
                                ),
                                timeFormat
                            )?.let { return@WheelTextPicker it }
                        }
                    }

                    onLog("No newTime from Hour picker - snappedTime: $snappedTime")
                    return@WheelTextPicker newIndex
                }
            )
            //Minute
            WheelTextPicker(
                size = DpSize(
                    width = size.width / if (timeFormat == TimeFormat.HOUR_24) 2 else 3,
                    height = size.height
                ),
                texts = minutes.map { it.text },
                rowCount = rowCount,
                style = textStyle,
                color = textColor,
                focusedIndex = minutes.find { it.value == startTime.minute }?.index ?: 0,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = false
                ),
                onScrollFinished = { snappedIndex ->
                    onLog("Minute onScollFinished - snappedIndex: $snappedIndex")

                    val newMinute = minutes.find { it.index == snappedIndex }?.value

                    val newHour = if (timeFormat == TimeFormat.HOUR_24) {
                        hours.find { it.value == snappedTime.hour }?.value
                    } else {
                        amPmHourToHour24(
                            amPmHours.find { it.value == localTimeToAmPmHour(snappedTime) }?.value
                                ?: 0,
                            snappedTime.minute,
                            snappedAmPm.value
                        )
                    }

                    newMinute?.let {
                        newHour?.let {
                            val newTime = snappedTime.withMinute(newMinute).withHour(newHour)

                            if (!newTime.isBefore(minTime) && !newTime.isAfter(maxTime)) {
                                snappedTime = newTime
                            }

                            val newIndex = minutes.find { it.value == snappedTime.minute }?.index

                            newIndex?.let {
                                onLog("onSnappedTime from Minute picker - newIndex: $newIndex, snappedTime: $snappedTime")
                                onSnappedTime(
                                    SnappedTime.Minute(
                                        localTime = snappedTime,
                                        index = newIndex
                                    ),
                                    timeFormat
                                )?.let { return@WheelTextPicker it }
                            }
                        }
                    }

                    onLog("No newTime from Minute picker - snappedTime: $snappedTime")
                    return@WheelTextPicker minutes.find { it.value == snappedTime.minute }?.index
                }
            )
            //AM_PM
            if (timeFormat == TimeFormat.AM_PM) {
                WheelTextPicker(
                    size = DpSize(
                        width = size.width / 3,
                        height = size.height
                    ),
                    texts = amPms.map { it.text },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    focusedIndex = amPms.find { it.value == amPmValueFromTime(startTime) }?.index
                        ?: 0,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    onScrollFinished = { snappedIndex ->
                        onLog("AmPm onScollFinished - snappedIndex: $snappedIndex")

                        val maxAmPmIndex = amPms.maxBy { it.index }.index
                        val normalizedSnappedIndex = when (snappedIndex > maxAmPmIndex) {
                            true -> maxAmPmIndex
                            false -> snappedIndex
                        }.coerceAtLeast(0)
                        val newAmPm = amPms.find {
                            it.index == normalizedSnappedIndex
                        }

                        newAmPm?.let {
                            snappedAmPm = newAmPm
                        }

                        val newMinute = minutes.find { it.value == snappedTime.minute }?.value

                        val newHour = amPmHourToHour24(
                            amPmHours.find { it.value == localTimeToAmPmHour(snappedTime) }?.value
                                ?: 0,
                            snappedTime.minute,
                            snappedAmPm.value
                        )

                        newMinute?.let {
                            val newTime = snappedTime.withMinute(newMinute).withHour(newHour)

                            if (!newTime.isBefore(minTime) && !newTime.isAfter(maxTime)) {
                                snappedTime = newTime
                            }

                            onLog("onSnappedTime from AmPm picker - newIndex: $normalizedSnappedIndex, snappedTime: $snappedTime")
                            onSnappedTime(
                                SnappedTime.AmPm(
                                    localTime = snappedTime,
                                    index = normalizedSnappedIndex
                                ),
                                timeFormat
                            )?.let { return@WheelTextPicker it }
                        }

                        onLog("No newTime from AmPm picker - newIndex: $normalizedSnappedIndex, snappedTime: $snappedTime")
                        return@WheelTextPicker normalizedSnappedIndex
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .size(
                    width = if (timeFormat == TimeFormat.HOUR_24) {
                        size.width
                    } else size.width * 2 / 3,
                    height = size.height / 3
                )
                .align(
                    alignment = if (timeFormat == TimeFormat.HOUR_24) {
                        Alignment.Center
                    } else Alignment.CenterStart
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ":",
                style = textStyle,
                color = textColor
            )
        }
    }
}

enum class TimeFormat {
    HOUR_24, AM_PM
}

private data class Hour(
    val text: String,
    val value: Int,
    val index: Int
)

private data class AmPmHour(
    val text: String,
    val value: Int,
    val index: Int
)

internal fun localTimeToAmPmHour(localTime: LocalTime): Int {

    if (
        isBetween(
            localTime,
            LocalTime.of(0, 0),
            LocalTime.of(0, 59)
        )
    ) {
        return localTime.hour + 12
    }

    if (
        isBetween(
            localTime,
            LocalTime.of(1, 0),
            LocalTime.of(11, 59)
        )
    ) {
        return localTime.hour
    }

    if (
        isBetween(
            localTime,
            LocalTime.of(12, 0),
            LocalTime.of(12, 59)
        )
    ) {
        return localTime.hour
    }

    if (
        isBetween(
            localTime,
            LocalTime.of(13, 0),
            LocalTime.of(23, 59)
        )
    ) {
        return localTime.hour - 12
    }

    return localTime.hour
}

private fun isBetween(localTime: LocalTime, startTime: LocalTime, endTime: LocalTime): Boolean {
    return localTime in startTime..endTime
}

private fun amPmHourToHour24(amPmHour: Int, amPmMinute: Int, amPmValue: AmPmValue): Int {

    return when (amPmValue) {
        AmPmValue.AM -> {
            if (amPmHour == 12 && amPmMinute <= 59) {
                0
            } else {
                amPmHour
            }
        }

        AmPmValue.PM -> {
            if (amPmHour == 12 && amPmMinute <= 59) {
                amPmHour
            } else {
                amPmHour + 12
            }
        }
    }
}

private data class Minute(
    val text: String,
    val value: Int,
    val index: Int
)

private data class AmPm(
    val text: String,
    val value: AmPmValue,
    val index: Int
)

internal enum class AmPmValue {
    AM, PM
}

private fun amPmValueFromTime(time: LocalTime): AmPmValue {
    return if (time.hour > 11) AmPmValue.PM else AmPmValue.AM
}

internal fun amPmIndexFromTime(time: LocalTime): Int {
    return amPmValueFromTime(time).ordinal
}

private fun amPmStringByLocale(amPmValue: AmPmValue, locale: Locale): String {
    val amPmTime = with(LocalDate.now()) {
        if (amPmValue == AmPmValue.AM) {
            atTime(0, 0)
        } else {
            atTime(12, 0)
        }
    }
    val timeFormat = DateTimeFormatter.ofPattern("a", locale)

    return amPmTime.format(timeFormat)
}