package com.example.customcalculatorapp

import android.os.Bundle
import android.os.DeadObjectException
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.customcalculatorapp.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {

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
            binding.btnPercent to "%",
            binding.btnPointer to ",",
            binding.btnBrackets to "()"
        )



        fun handleButtonClick(value: Any?) {

            val currentStringCalculate = binding.tvCalculate.text.toString()

            when(value) {
                is Int -> {
                    binding.tvCalculate.text = (currentStringCalculate + value).toString()
                }

                is String -> {
                    when(value) {
                        "+" -> { binding.tvCalculate.text = (currentStringCalculate + value) }
                        "-" -> { binding.tvCalculate.text = (currentStringCalculate + value) }
                        "=" -> { val result = parseAndCountResult(binding.tvCalculate.text.toString())
                            if (result.rem(1) == 0.0) binding.tvResult.text = result.toInt().toString()
                             else binding.tvResult.text = result.toString()
                            }
                        "x" -> { binding.tvCalculate.text = (currentStringCalculate + value) }
                        "/" -> { binding.tvCalculate.text = (currentStringCalculate + value)  }
                        "," -> {}
                        "clear" -> binding.tvCalculate.text = ""
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

fun performOperation(result: Double, number: Double, operator: Char?): Double {
    // Выполним операцию в соответствии с оператором
    return when (operator) {
        '+' -> result + number
        '-' -> result - number
        'x' -> result * number
        '/' -> result / number
        else -> number // Если оператор не определен, вернем число без изменений
    }
}

fun parseAndCountResult(calculateString: String): Double {

    Log.d("MyCalculate", "What we want to calculate: $calculateString")

    var currentNumber = ""
    var currentOperator: Char? = null
    var result = 0.0

    for (char in calculateString) {
        when {
            char.isDigit() -> currentNumber += char
            char.isWhitespace() -> continue
            else -> {
                // Если мы дошли до символа, который не является цифрой и не пробелом, это оператор
                // Преобразуем текущее число и оператор в результат, если они есть
                if (currentNumber.isNotEmpty()) {
                    val number = currentNumber.toDouble()
                    result = performOperation(result, number, currentOperator)
                    currentNumber = ""
                }
                currentOperator = char
            }
        }
    }

    // Обработаем оставшееся число и оператор, если они есть
    if (currentNumber.isNotEmpty() && currentOperator != null) {
        val number = currentNumber.toDouble()
        result = performOperation(result, number, currentOperator)
    }

    Log.d("MyCalculateResult", "What we calculated: $result")
    return result
}
