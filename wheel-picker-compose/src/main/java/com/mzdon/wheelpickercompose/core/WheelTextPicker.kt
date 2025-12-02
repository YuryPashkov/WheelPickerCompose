package com.mzdon.wheelpickercompose.core

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun WheelTextPicker(
    modifier: Modifier = Modifier,
    focusedIndex: Int = 0,
    size: DpSize = DpSize(128.dp, 128.dp),
    texts: List<String>,
    rowCount: Int,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onScrollFinished: (snappedIndex: Int) -> Int? = { null },
) {
    WheelPicker(
        modifier = modifier,
        focusedIndex = focusedIndex,
        size = size,
        count = texts.size,
        rowCount = rowCount,
        selectorProperties = selectorProperties,
        onScrollFinished = onScrollFinished
    ) { index ->
        Text(
            text = texts[index],
            style = style,
            color = if (index == focusedIndex) {
                color
            } else {
                color.copy(alpha = 0.5f)
            },
            maxLines = 1
        )
    }
}
