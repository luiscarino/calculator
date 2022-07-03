package dev.luiscarino.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Clear -> clear()
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.Operation -> enterOperation(action.calculatorOperation)
        }
    }

    private fun performCalculation() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        if (number1 != null && number2 != null) {
            val result = when (state.operation) {
                is CalculatorOperation.Add -> number1 + number2
                CalculatorOperation.Divide -> number1 / number2
                CalculatorOperation.Multiply -> number1 * number2
                CalculatorOperation.Percentage -> (number1 / number2) * 100
                CalculatorOperation.Subtract -> number1 - number2
                null -> return
            }
            state = state.copy(number1 = result.toString().take(15), number2 = "", operation = null)
        }
    }

    private fun enterDecimal() {
        // decimal only if we are currently adding number one or two and they have any numbers and numbers dont contain other decimal
        if (state.operation == null && !state.number1.contains(".") && state.number1.isNotBlank()) {
            state = state.copy(number1 = state.number1.plus("."))
            return
        }
        // check for number 2
        if (!state.number2.contains(".") && state.number2.isNotBlank()) {
            state = state.copy(number2 = state.number2.plus("."))
            return
        }
    }

    private fun delete() {
        when {
            state.number2.isNotBlank() -> state = state.copy(number2 = state.number2.dropLast(1))
            state.operation != null -> state = state.copy(operation = null)
            state.number1.isNotBlank() -> state = state.copy(number1 = state.number1.dropLast(1))
        }
    }

    private fun clear() {
        state = CalculatorState()
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (state.number1.isNotBlank()) {
            state = state.copy(operation = operation)
        }
    }

    private fun enterNumber(number: Int) {
        // use operation to check if its number one or two
        if (state.operation == null) {
            if (state.number1.length >= MAX_NUMBER_LENGTH) {
                return
            }
            state = state.copy(number1 = state.number1.plus(number))
            return
        } else {
            if (state.number2.length >= MAX_NUMBER_LENGTH) {
                return
            }
            state = state.copy(number2 = state.number2.plus(number))
        }
    }

    companion object {
        const val MAX_NUMBER_LENGTH = 8
    }
}