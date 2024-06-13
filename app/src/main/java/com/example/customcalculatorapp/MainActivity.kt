package com.example.customcalculatorapp

import android.os.Bundle
import android.os.DeadObjectException
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.customcalculatorapp.databinding.ActivityMainBinding

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
            binding.btnPlus to "+",
            binding.btnMinus to "-",
            binding.btnClear to "clear",
            binding.btnEquals to "=",
            binding.btnMultiplication to "x",
            binding.btnDivision to "/",
            binding.btnDel to "DeleteLastSymbol",
            binding.btnPercent to "%", //need implement
            binding.btnPointer to ",",
            binding.btnBrackets to "()" //need implement
        )


        fun handleButtonClick(value: Any?) {

            val currentStringCalculate = binding.tvCalculate.text.toString()

            when (value) {
                is Int -> {
                    binding.tvCalculate.text = (currentStringCalculate + value)
                }

                is String -> {
                    when (value) {
                        "+" -> {
                            binding.tvCalculate.text = (currentStringCalculate + value)
                        }

                        "-" -> {
                            binding.tvCalculate.text = (currentStringCalculate + value)
                        }

                        "=" -> {
                            value.replace(",", ".")
                            val result = parseAndCountResult(binding.tvCalculate.text.toString())
                            if (result.rem(1) == 0.0) binding.tvResult.text =
                                result.toInt().toString()
                            else binding.tvResult.text = result.toString()
                        }

                        "x" -> {
                            binding.tvCalculate.text = (currentStringCalculate + value)
                        }

                        "/" -> {
                            binding.tvCalculate.text = (currentStringCalculate + value)
                        }

                        "," -> {
                            var counterPointer = 0
                            if (binding.tvCalculate.text.toString().isNotEmpty() && counterPointer == 0) {
                                binding.tvCalculate.text = (currentStringCalculate + value)
                                counterPointer = 1
                            } else if (counterPointer == 0) {
                                binding.tvCalculate.text = (currentStringCalculate + value)
                            }
                        }
                        "clear" -> binding.tvCalculate.text = ""
                        "DeleteLastSymbol" -> {
                            val newExpression = delLastSymbol(binding.tvCalculate.text.toString())
                            binding.tvCalculate.text = newExpression
                        }
                        "()" -> {
                            val newExpression = checkAndAddBracket(binding.tvCalculate.text.toString())
                        }
                        "%" -> {
                            if (currentStringCalculate != "") binding.tvCalculate.text = (currentStringCalculate + value)
                        }
                    }
                }
            }
        }

        mapKeysInout.forEach { button, value ->
            button.setOnClickListener {
                handleButtonClick(value)
            }
        }
    }

}

private fun performOperation(result: Double, number: Double, operator: Char?): Double {
    // Выполним операцию в соответствии с оператором
    return when (operator) {
        '+' -> result + number
        '-' -> result - number
        'x' -> result * number
        '/' -> result / number
        '%' -> result % number
        else -> number
    // Если оператор не определен, вернем число без изменений
    }
}

private fun parseAndCountResult(calculateString: String): Double {

    val parseStringForCalculate = replacePointer(calculateString)

    Log.d("MyCalculate", "What we want to calculate: $parseStringForCalculate")

    var currentNumber = ""
    var currentOperator: Char? = null
    var result = 0.0

    for (char in parseStringForCalculate) {
        when {
            char.isDigit() || char == '.' -> currentNumber += char
            char.isWhitespace() -> continue
            else -> {
                if (currentNumber.isNotEmpty()) {
                    val number = currentNumber.toDouble()
                    result = if (currentOperator == null) {
                        number
                    } else {
                        performOperation(result, number, currentOperator)
                    }
                    currentNumber = ""
                }
                currentOperator = char
            }
        }
    }

    if (currentNumber.isNotEmpty()) {
        val number = currentNumber.toDouble()
        result = if (currentOperator == null) {
            number
        } else {
            performOperation(result, number, currentOperator)
        }
    }
    Log.d("MyCalculateResult", "What we calculated: $result")

    return formatResult(result)
}

private fun replacePointer(calculateString: String): String {
    return calculateString.replace(",", ".")
}

private fun formatResult(value: Double): Double {
    // Преобразуем число в строку с четырьмя знаками после запятой
    val formattedString = "%.4f".format(value)
    // Разделяем целую и дробную части
    val parts = formattedString.split(".")
    val fractionalPart = parts[1]

    // Проверяем первые два знака дробной части
    return if (fractionalPart.startsWith("00")) {
        "%.2f".format(value).toDouble()
    } else {
        formattedString.toDouble()
    }
}

private fun delLastSymbol(expression: String): String {
    return expression.dropLast(1)
}

private fun checkAndAddBracket(expression: String): Any {
    var resultExpression = ""
    if (!expression.last().equals("(")) resultExpression =  expression + "("
    if (!expression.last().equals(")")) resultExpression =  expression + ")"
    return resultExpression
}