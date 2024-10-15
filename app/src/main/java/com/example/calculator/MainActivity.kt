package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                CalculatorApp() // Set the content to use the CalculatorApp composable
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    // State variables to hold the values displayed and used in calculations
    var displayValue by remember { mutableStateOf("0") } // Displayed value on the screen
    var currentNumber by remember { mutableStateOf("0") } // Current number being entered
    var operator by remember { mutableStateOf<String?>(null) } // Current operator selected
    var previousNumber by remember { mutableStateOf<String?>(null) } // Previous number before an operator
    var expression by remember { mutableStateOf("") } // Expression being calculated (for UI display)
    // Layout of the calculator app
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display area showing the current value
        Text(
            text = expression,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        )
        // Display area showing the current value
        Text(
            text = displayValue,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp)) // Spacer to add vertical space

        // Button layout for the calculator in a grid
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorGrid(
                listOf(
                    listOf("CE", "C", "BS", "/"),
                    listOf("7", "8", "9", "x"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("+/-", "0", ".", "=")
                ),
                onClick = { handleInput(it, displayValue, currentNumber, previousNumber, operator,
                    onUpdate = { newDisplayValue, newCurrentNumber, newPreviousNumber, newOperator ->
                        displayValue = newDisplayValue
                        currentNumber = newCurrentNumber
                        previousNumber = newPreviousNumber
                        operator = newOperator

                        // Cập nhật biểu thức để hiển thị
                        expression = if (newOperator != null) {
                            "$newPreviousNumber $newOperator $newCurrentNumber"
                        } else {
                            newCurrentNumber
                        }
                    })
                }
            )
        }
    }
}

@Composable
fun CalculatorGrid(buttonRows: List<List<String>>, onClick: (String) -> Unit) {
    // Layout for a grid of buttons
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        buttonRows.forEach { buttons ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                buttons.forEach { label ->
                    Button(
                        onClick = { onClick(label) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp),
                        shape = RectangleShape // Set shape to rectangle
                    ) {
                        Text(text = label, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

private fun handleInput(input: String, displayValue: String, currentNumber: String, previousNumber: String?, operator: String?, onUpdate: (String, String, String?, String?) -> Unit) {
    var newDisplayValue = displayValue
    var newCurrentNumber = currentNumber
    var newPreviousNumber = previousNumber
    var newOperator = operator

    when (input) {
        in "0".."9" -> {
            newCurrentNumber = if (newCurrentNumber == "0" || (newOperator == null && newPreviousNumber == displayValue)) {
                input
            } else {
                newCurrentNumber + input
            }
            newDisplayValue = newCurrentNumber
        }
        "." -> {
            if (!newCurrentNumber.contains(".")) {
                newCurrentNumber += "."
            }
            newDisplayValue = newCurrentNumber
        }
        "+/-" -> {
            if (newCurrentNumber.isNotEmpty()) {
                newCurrentNumber = if (newCurrentNumber.startsWith("-")) {
                    newCurrentNumber.drop(1)
                } else {
                    "-$newCurrentNumber"
                }
                newDisplayValue = newCurrentNumber
            }
        }
        "=" -> {
            if (newOperator != null && newPreviousNumber != null && newCurrentNumber.isNotEmpty()) {
                val result = when (newOperator) {
                    "+" -> newPreviousNumber.toDouble() + newCurrentNumber.toDouble()
                    "-" -> newPreviousNumber.toDouble() - newCurrentNumber.toDouble()
                    "x" -> newPreviousNumber.toDouble() * newCurrentNumber.toDouble()
                    "/" -> newPreviousNumber.toDouble() / newCurrentNumber.toDouble()
                    else -> 0.0
                }
                newDisplayValue = result.toString()
                newPreviousNumber = result.toString()
                newCurrentNumber = "0"
                newOperator = null
            }
        }
        "+", "-", "x", "/" -> {
            if (newOperator != null && newPreviousNumber != null && newCurrentNumber.isNotEmpty()) {
                val result = when (newOperator) {
                    "+" -> newPreviousNumber.toDouble() + newCurrentNumber.toDouble()
                    "-" -> newPreviousNumber.toDouble() - newCurrentNumber.toDouble()
                    "x" -> newPreviousNumber.toDouble() * newCurrentNumber.toDouble()
                    "/" -> newPreviousNumber.toDouble() / newCurrentNumber.toDouble()
                    else -> 0.0
                }
                newPreviousNumber = result.toString()
                newCurrentNumber = "0"
                newDisplayValue = newPreviousNumber
            } else if (newPreviousNumber == null) {
                newPreviousNumber = newCurrentNumber
                newCurrentNumber = "0"
            }
            newOperator = input
        }
        "CE" -> {
            newCurrentNumber = "0"
            newDisplayValue = "0"
        }
        "C" -> {
            newCurrentNumber = "0"
            newPreviousNumber = null
            newOperator = null
            newDisplayValue = "0"
        }
        "BS" -> {
            newCurrentNumber = if (newCurrentNumber.length > 1) {
                newCurrentNumber.dropLast(1)
            } else {
                "0"
            }
            newDisplayValue = newCurrentNumber
        }
    }

    // Cập nhật giao diện với các giá trị mới
    onUpdate(newDisplayValue, newCurrentNumber, newPreviousNumber, newOperator)
}


