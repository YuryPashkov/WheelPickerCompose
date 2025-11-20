package com.mzdon.wheelpickercompose

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.mzdon.wheelpickercompose.core.DefaultWheelTimePicker
import com.mzdon.wheelpickercompose.core.SelectorProperties
import com.mzdon.wheelpickercompose.core.WheelPickerDefaults
import java.time.LocalTime
import java.util.Locale

@Composable
fun WheelTimePicker(
    modifier: Modifier = Modifier,
    startTime: LocalTime = LocalTime.now(),
    dateLocale: Locale = Locale.getDefault(),
    minTime: LocalTime = LocalTime.MIN,
    maxTime: LocalTime = LocalTime.MAX,
    size: DpSize = DpSize(128.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedTime: (snappedTime: LocalTime) -> Unit = {},
    onLog: (log: String) -> Unit = {}
) {
    DefaultWheelTimePicker(
        modifier,
        startTime,
        dateLocale,
        minTime,
        maxTime,
        size,
        rowCount,
        textStyle,
        textColor,
        selectorProperties,
        onSnappedTime = { snappedTime, _ ->
            onSnappedTime(snappedTime.snappedLocalTime)
            snappedTime.snappedIndex
        },
        onLog
    )
}