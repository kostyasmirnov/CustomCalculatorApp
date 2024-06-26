package com.example.customcalculatorapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.customcalculatorapp.databinding.ActivityMainBinding
import java.util.Stack

const val TAG = "MyCalculator"
const val inputLog = "What we want calculate: "
const val outputLog = "What we calculate: "
const val lastSymbolNotNumberToast = "The last character is not a number, remove it or add a number"
const val warnMesDuplicateSymbolToast = "You cannot add 2 identical mathematical symbols"
const val warnMesFirstNumber = "The first character must be a number"
const val plus = "+"
const val minus = "-"
const val clear = "clear"
const val equals = "="
const val multiplication = "x"
const val division = "/"
const val del = "DeleteLastSymbol"
const val percent = "%"
const val pointer = ","
const val brackets = "()"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapKeysInout: Map<Button, *> = mapOf(
            binding.btn0 to 0,
            binding.btn1 to 1,
            binding.btn2 to 2,
            binding.btn3 to 3,
            binding.btn4 to 4,
            binding.btn5 to 5,
            binding.btn6 to 6,
            binding.btn7 to 7,
            binding.btn8 to 8,
            binding.btn9 to 9,
            binding.btnPlus to plus,
            binding.btnMinus to minus,
            binding.btnClear to clear,
            binding.btnEquals to equals,
            binding.btnMultiplication to multiplication,
            binding.btnDivision to division,
            binding.btnDel to del,
            binding.btnPercent to percent,
            binding.btnPointer to pointer,
            binding.btnBrackets to brackets
        )


        fun handleButtonClick(value: Any?) {

            val currentStringCalculate = binding.tvCalculate.text.toString()

            when (value) {
                is Int -> {
                    binding.tvCalculate.text = (currentStringCalculate + value)
                }

                is String -> {
                    when (value) {
                        plus -> binding.tvCalculate.text =
                            checkDuplicate(currentStringCalculate, value)

                        minus -> binding.tvCalculate.text =
                            checkDuplicate(currentStringCalculate, value)

                        equals -> {
                            value.replace(",", ".")
                            val result = parseAndCountResult(binding.tvCalculate.text.toString())
                            if (result.rem(1) == 0.0) binding.tvResult.text =
                                result.toInt().toString()
                            else binding.tvResult.text = result.toString()
                        }

                        multiplication -> binding.tvCalculate.text =
                            checkDuplicate(currentStringCalculate, value)

                        division -> binding.tvCalculate.text =
                            checkDuplicate(currentStringCalculate, value)

                        pointer -> binding.tvCalculate.text =
                            checkDuplicate(currentStringCalculate, value)

                        clear -> binding.tvCalculate.text = ""

                        del -> {
                            val newExpression = delLastSymbol(binding.tvCalculate.text.toString())
                            binding.tvCalculate.text = newExpression
                        }

                        brackets -> {
                            val newExpression = checkAndAddBracket(currentStringCalculate)
                            binding.tvCalculate.text = newExpression
                        }

                        percent -> binding.tvCalculate.text =
                            checkDuplicate(currentStringCalculate, value)
                    }
                }
            }
        }

        mapKeysInout.forEach { (button, value) ->
            button.setOnClickListener {
                handleButtonClick(value)
            }
        }
    }

    private fun parseAndCountResult(calculateString: String): Double {
        val lastSymbolNotNumber = listOf('-', '+', 'x', '/', '%', ',')
        for (symbol in lastSymbolNotNumber) {
            if (calculateString.last() == symbol)
                showShortToast(lastSymbolNotNumberToast)
        }
        val rpn = convertToRPN(calculateString)
        return evaluateRPN(rpn)
    }

    private fun checkDuplicate(calculateString: String, value: String): String {
        if (calculateString.isEmpty()) {
            showShortToast(warnMesFirstNumber)
            return ""
        }
        if (calculateString.isNotEmpty() && calculateString.last() != value.last()) return calculateString + value
        else {
            showShortToast(warnMesDuplicateSymbolToast)
            return calculateString
        }
    }

    private fun showShortToast(mes: String) {
        Toast.makeText(this, mes, Toast.LENGTH_SHORT).show()
    }

}

private fun performOperation(result: Double, number: Double, operator: Char?): Double {
    return when (operator) {
        '+' -> result + number
        '-' -> result - number
        'x' -> result * number
        '/' -> result / number
        '%' -> result * (number / 100)
        else -> number
    }
}

private fun convertToRPN(expression: String): List<String> {
    val output = mutableListOf<String>()
    val operators = Stack<Char>()
    var currentNumber = ""

    Log.d(TAG, inputLog + expression)

    fun addNumber() {
        if (currentNumber.isNotEmpty()) {
            output.add(currentNumber)
            currentNumber = ""
        }
    }

    for (char in expression) {
        when {
            char.isDigit() || char == '.' -> currentNumber += char
            char == '(' -> operators.push(char)
            char == ')' -> {
                addNumber()
                while (operators.isNotEmpty() && operators.peek() != '(') {
                    output.add(operators.pop().toString())
                }
                if (operators.isNotEmpty()) {
                    operators.pop()
                }
            }

            char in listOf('+', '-', 'x', '/', '%') -> {
                addNumber()
                while (operators.isNotEmpty() && precedence(char) <= precedence(operators.peek())) {
                    output.add(operators.pop().toString())
                }
                operators.push(char)
            }
        }
    }

    addNumber()
    while (operators.isNotEmpty()) {
        output.add(operators.pop().toString())
    }

    Log.d(TAG, outputLog)
    return output
}

private fun precedence(op: Char): Int {
    return when (op) {
        '+', '-' -> 1
        'x', '/' -> 2
        '%' -> 3
        else -> 0
    }
}

private fun evaluateRPN(rpn: List<String>): Double {
    val stack = Stack<Double>()

    for (token in rpn) {
        when {
            token.toDoubleOrNull() != null -> stack.push(token.toDouble())
            token.length == 1 && token[0] in listOf('+', '-', 'x', '/', '%') -> {
                val number2 = stack.pop()
                val number1 = stack.pop()
                val result = performOperation(number1, number2, token[0])
                stack.push(result)
            }
        }
    }

    return stack.pop()
}

//private fun replacePointer(calculateString: String): String {
//    return calculateString.replace(",", ".")
//}
//
//private fun formatResult(value: Double): Double {
//    val formattedString = "%.4f".format(value)
//    val parts = formattedString.split(".")
//    val fractionalPart = parts[1]
//
//    return if (fractionalPart.startsWith("00")) {
//        "%.2f".format(value).toDouble()
//    } else {
//        formattedString.toDouble()
//    }
//}

private fun delLastSymbol(expression: String): String {
    return expression.dropLast(1)
}

private fun checkAndAddBracket(expression: String): String {
    val openBracketCount: Int = expression.count { it == '(' }
    val closeBracketCount: Int = expression.count { it == ')' }

    return if (openBracketCount > closeBracketCount) {
        "$expression)"
    } else {
        "$expression("
    }
}