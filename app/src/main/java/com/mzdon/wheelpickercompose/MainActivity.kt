package com.mzdon.wheelpickercompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.mzdon.wheelpickercompose.ui.theme.WheelPickerComposeTheme
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WheelPickerComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val scrollState = rememberScrollState()
                    val nestedScrollConnection = remember {
                        object : NestedScrollConnection {
                            override fun onPostScroll(
                                consumed: Offset,
                                available: Offset,
                                source: NestedScrollSource
                            ): Offset {
                                Log.d("NestedScrollConnection", "onPostScroll consumed = $consumed available = $available source = $source")
                                return available
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.verticalScroll(scrollState)
                            .nestedScroll(nestedScrollConnection),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        var startTime by rememberSaveable {
                            mutableStateOf(LocalTime.of(12, 0))
                        }
                        WheelTimePicker(
                            startTime = startTime,
                            onSnappedTime = { snappedTime ->
                                Log.d("snappedDate", "$snappedTime")
                                startTime = snappedTime
                            }
                        ) { snappedTime ->
                            println(snappedTime)
                        }
                        Text("Time Selected: $startTime")
                        Button(onClick = {
                            startTime = LocalTime.of(19, 23)
                        }) {
                            Text(text = "Change startTime to 19:23")
                        }
                        var startDate by rememberSaveable {
                            mutableStateOf(LocalDate.of(1993, 1, 12))
                        }
                        WheelDatePicker(
                            startDate = startDate,
                            minDate = LocalDate.of(1900, 1, 1),
                            maxDate = LocalDate.now(),
                        ) { snappedDate ->
                            Log.d("snappedDate", "$snappedDate")
                            startDate = snappedDate
                        }

                        Text("Date Selected: $startDate")

                        Button(onClick = {
                            startDate = LocalDate.of(1999, 11, 7)
                        }) {
                            Text(text = "Change startDate to 07.11.1999")
                        }

//                        WheelDatePicker { snappedDate ->
//                            println(snappedDate)
//                        }
//                        WheelDateTimePicker { snappedDateTime ->
//                            println(snappedDateTime)
//                        }
//                        WheelDateTimePicker(
//                            startDateTime = LocalDateTime.of(
//                                2025, 10, 20, 5, 30
//                            ),
//                            minDateTime = LocalDateTime.now(),
//                            maxDateTime = LocalDateTime.of(
//                                2025, 10, 20, 5, 30
//                            ),
//                            size = DpSize(200.dp, 100.dp),
//                            rowCount = 5,
//                            textStyle = MaterialTheme.typography.titleSmall,
//                            textColor = Color(0xFFffc300),
//                            selectorProperties = WheelPickerDefaults.selectorProperties(
//                                enabled = true,
//                                shape = RoundedCornerShape(0.dp),
//                                color = Color(0xFFf1faee).copy(alpha = 0.2f),
//                                border = BorderStroke(2.dp, Color(0xFFf1faee))
//                            )
//                        ) { snappedDateTime ->
//                            println(snappedDateTime)
//                        }
//                        WheelTimePicker(
//                            size = DpSize(200.dp, 100.dp),
//                            rowCount = 5,
//                            textStyle = MaterialTheme.typography.titleSmall,
//                            textColor = Color(0xFFffc300),
//                            selectorProperties = WheelPickerDefaults.selectorProperties(
//                                enabled = true,
//                                shape = RoundedCornerShape(0.dp),
//                                color = Color(0xFFf1faee).copy(alpha = 0.2f),
//                                border = BorderStroke(2.dp, Color(0xFFf1faee))
//                            )
//                        ) { snappedTime ->
//                            println(snappedTime)
//                        }
//                        WheelDurationPicker(
//                            startDuration = 15.toDuration(DurationUnit.MINUTES),
//                            minDuration = 1.toDuration(DurationUnit.MINUTES),
//                            maxDuration = 30.toDuration(DurationUnit.MINUTES),
//                            durationFormat = DurationFormat.MINUTES_SECONDS,
//                            size = DpSize(200.dp, 100.dp),
//                            rowCount = 5,
//                            textStyle = MaterialTheme.typography.titleSmall,
//                            textColor = Color(0xFFffc300),
//                            selectorProperties = WheelPickerDefaults.selectorProperties(
//                                enabled = true,
//                                shape = RoundedCornerShape(0.dp),
//                                color = Color(0xFFf1faee).copy(alpha = 0.2f),
//                                border = BorderStroke(2.dp, Color(0xFFf1faee))
//                            )
//                        ) { snappedTime ->
//                            println(snappedTime)
//                        }
                    }
                }
            }
        }
    }
}