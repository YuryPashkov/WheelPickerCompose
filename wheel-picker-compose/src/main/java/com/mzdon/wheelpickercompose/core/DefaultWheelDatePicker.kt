package com.mzdon.wheelpickercompose.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mzdon.wheelpickercompose.core.PickDateField.*
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

@Composable
internal fun DefaultWheelDatePicker(
    modifier: Modifier = Modifier,
    startDate: LocalDate,
    dateLocale: Locale = Locale.getDefault(),
    minDate: LocalDate = LocalDate.MIN,
    maxDate: LocalDate = LocalDate.MAX,
    yearsRange: IntRange? = IntRange(1922, 2122),
    size: DpSize = DpSize(256.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedDate: (snappedDate: SnappedDate) -> Int? = { _ -> null }
) {
    val month31day = remember {
        (1..31).map {
            DayOfMonth(
                text = it.toString(),
                value = it,
                index = it - 1
            )
        }
    }
    val month30day = remember {
        (1..30).map {
            DayOfMonth(
                text = it.toString(),
                value = it,
                index = it - 1
            )
        }
    }
    val month29day = remember {
        (1..29).map {
            DayOfMonth(
                text = it.toString(),
                value = it,
                index = it - 1
            )
        }
    }
    val month28day = remember {
        (1..28).map {
            DayOfMonth(
                text = it.toString(),
                value = it,
                index = it - 1
            )
        }
    }

    var dayOfMonths = calculateDayOfMonths(
        month = startDate.month.value,
        year = startDate.year,
        month31day = month31day,
        month30day = month30day,
        month29day = month29day,
        month28day = month28day,
    )

    val months = remember {
        (1..12).map {
            Month(
                text = if (size.width / 3 < 55.dp) {
                    DateFormatSymbols(dateLocale).shortMonths[it - 1]
                } else DateFormatSymbols(dateLocale).months[it - 1],
                value = it,
                index = it - 1
            )
        }
    }

    val years = remember {
        yearsRange?.map {
            Year(
                text = it.toString(),
                value = it,
                index = yearsRange.indexOf(it)
            )
        }
    }

    var pickDateOrder by remember {
        mutableStateOf(
            getDateFormatOrder()
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    pickDateOrder = getDateFormatOrder()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (selectorProperties.enabled().value) {
            Surface(
                modifier = Modifier
                    .size(size.width, size.height / rowCount),
                shape = selectorProperties.shape().value,
                color = selectorProperties.color().value,
                border = selectorProperties.border().value
            ) {}
        }
        Row {
            pickDateOrder.order.forEach {  order ->
                when (order) {
                    DAY -> {
                        //Day of Month
                        WheelTextPicker(
                            size = DpSize(
                                width = if (yearsRange == null) size.width / 2 else size.width / 3,
                                height = size.height
                            ),
                            texts = dayOfMonths.map { it.text },
                            rowCount = rowCount,
                            style = textStyle,
                            color = textColor,
                            selectorProperties = WheelPickerDefaults.selectorProperties(
                                enabled = false
                            ),
                            focusedIndex = dayOfMonths.find { it.value == startDate.dayOfMonth }?.index ?: 0,
                            onScrollFinished = { snappedIndex ->
                                val newDayOfMonth = if (snappedIndex >= dayOfMonths.size) {
                                    dayOfMonths.last().value
                                } else {
                                    dayOfMonths.find { it.index == snappedIndex }?.value
                                }

                                if (newDayOfMonth == null) {
                                    return@WheelTextPicker null
                                } else {
                                    val newDate = startDate.withDayOfMonth(newDayOfMonth)

                                    val snappedDate = if (newDate.isBefore(minDate)) {
                                        minDate
                                    } else if (newDate.isAfter(maxDate)) {
                                        maxDate
                                    } else {
                                        newDate
                                    }

                                    val newIndex =
                                        dayOfMonths.find { it.value == snappedDate.dayOfMonth }?.index
                                    if (newIndex == null) {
                                        return@WheelTextPicker null
                                    } else {
                                        return@WheelTextPicker onSnappedDate(
                                            SnappedDate.DayOfMonth(
                                                localDate = snappedDate,
                                                index = newIndex
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    }

                    MONTH -> {
                        //Month
                        WheelTextPicker(
                            size = DpSize(
                                width = if (yearsRange == null) size.width / 2 else size.width / 3,
                                height = size.height
                            ),
                            texts = months.map { it.text },
                            rowCount = rowCount,
                            style = textStyle,
                            color = textColor,
                            selectorProperties = WheelPickerDefaults.selectorProperties(
                                enabled = false
                            ),
                            focusedIndex = months.find { it.value == startDate.monthValue }?.index ?: 0,
                            onScrollFinished = { snappedIndex ->
                                val newMonth = if (snappedIndex >= months.size) {
                                    months.last().value
                                } else {
                                    months.find { it.index == snappedIndex }?.value
                                }

                                if (newMonth == null) {
                                    return@WheelTextPicker null
                                } else {
                                    val newDate = startDate.withMonth(newMonth)
                                    val snappedDate = if (newDate.isBefore(minDate)) {
                                        minDate
                                    } else if (newDate.isAfter(maxDate)) {
                                        maxDate
                                    } else {
                                        newDate
                                    }

                                    dayOfMonths =
                                        calculateDayOfMonths(
                                            month = snappedDate.month.value,
                                            year = snappedDate.year,
                                            month31day = month31day,
                                            month30day = month30day,
                                            month29day = month29day,
                                            month28day = month28day,
                                        )

                                    val newIndex = months.find { it.value == snappedDate.monthValue }?.index

                                    if (newIndex == null) {
                                        return@WheelTextPicker null
                                    } else {
                                        return@WheelTextPicker onSnappedDate(
                                            SnappedDate.Month(
                                                localDate = snappedDate,
                                                index = newIndex
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    }

                    YEAR -> {
                        //Year
                        years?.let { years ->
                            WheelTextPicker(
                                size = DpSize(
                                    width = size.width / 3,
                                    height = size.height
                                ),
                                texts = years.map { it.text },
                                rowCount = rowCount,
                                style = textStyle,
                                color = textColor,
                                selectorProperties = WheelPickerDefaults.selectorProperties(
                                    enabled = false
                                ),
                                focusedIndex = years.find { it.value == startDate.year }?.index ?: 0,
                                onScrollFinished = { snappedIndex ->
                                    val newYear = if (snappedIndex >= years.size) {
                                        years.last().value
                                    } else {
                                        years.find { it.index == snappedIndex }?.value
                                    }

                                    if (newYear == null) {
                                        return@WheelTextPicker null
                                    } else {
                                        val newDate = startDate.withYear(newYear)

                                        val snappedDate = if (newDate.isBefore(minDate)) {
                                            minDate
                                        } else if (newDate.isAfter(maxDate)) {
                                            maxDate
                                        } else {
                                            newDate
                                        }

                                        dayOfMonths =
                                            calculateDayOfMonths(
                                                month = snappedDate.month.value,
                                                year = snappedDate.year,
                                                month31day = month31day,
                                                month30day = month30day,
                                                month29day = month29day,
                                                month28day = month28day,
                                            )

                                        val newIndex = years.find { it.value == snappedDate.year }?.index

                                        if (newIndex == null) {
                                            return@WheelTextPicker null
                                        } else {
                                            return@WheelTextPicker onSnappedDate(
                                                SnappedDate.Year(
                                                    localDate = snappedDate,
                                                    index = newIndex
                                                )
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

internal data class DayOfMonth(
    val text: String,
    val value: Int,
    val index: Int
)

private data class Month(
    val text: String,
    val value: Int,
    val index: Int
)

private data class Year(
    val text: String,
    val value: Int,
    val index: Int
)

internal fun calculateDayOfMonths(
    month: Int,
    year: Int,
    month31day: List<DayOfMonth>,
    month30day: List<DayOfMonth>,
    month29day: List<DayOfMonth>,
    month28day: List<DayOfMonth>,
): List<DayOfMonth> {
    val isLeapYear = LocalDate.of(year, month, 1).isLeapYear
    return when (month) {
        1 -> {
            month31day
        }

        2 -> {
            if (isLeapYear) month29day else month28day
        }

        3 -> {
            month31day
        }

        4 -> {
            month30day
        }

        5 -> {
            month31day
        }

        6 -> {
            month30day
        }

        7 -> {
            month31day
        }

        8 -> {
            month31day
        }

        9 -> {
            month30day
        }

        10 -> {
            month31day
        }

        11 -> {
            month30day
        }

        12 -> {
            month31day
        }

        else -> {
            emptyList()
        }
    }
}

fun getDateFormatOrder(): PickDateOrder {
    val formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
    val pattern = (formatter as? SimpleDateFormat?)?.toPattern()?.uppercase()
    return if (pattern == null) {
        PickDateOrder.MDY
    } else {
        when {
            pattern.indexOf('Y') < pattern.indexOf('M') && pattern.indexOf('M') < pattern.indexOf('D') -> PickDateOrder.YMD
            pattern.indexOf('M') < pattern.indexOf('D') && pattern.indexOf('D') < pattern.indexOf('Y') -> PickDateOrder.MDY
            pattern.indexOf('D') < pattern.indexOf('M') && pattern.indexOf('M') < pattern.indexOf('Y') -> PickDateOrder.DMY
            pattern.indexOf('Y') < pattern.indexOf('D') && pattern.indexOf('D') < pattern.indexOf('M') -> PickDateOrder.YDM
            pattern.indexOf('M') < pattern.indexOf('Y') && pattern.indexOf('Y') < pattern.indexOf('D') -> PickDateOrder.MYD
            pattern.indexOf('D') < pattern.indexOf('Y') && pattern.indexOf('Y') < pattern.indexOf('M') -> PickDateOrder.DYM
            else -> PickDateOrder.MDY
        }
    }
}
