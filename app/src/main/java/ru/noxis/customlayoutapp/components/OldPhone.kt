package ru.noxis.customlayoutapp.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import ru.noxis.customlayoutapp.ui.theme.baseColor
import ru.noxis.customlayoutapp.ui.theme.centerAndFrameColor
import ru.noxis.customlayoutapp.ui.theme.coverColor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")


@Composable
fun OldPhone(
    modifier: Modifier = Modifier,
) {

    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()
    val typography = MaterialTheme.typography.headlineLarge

    var activeDigitIndex by remember { mutableIntStateOf(-1) }

    //список DigitBounds равный количеству цифр
    val digitBoundsList = remember { List(digits.size) { DigitBounds() } }

    //переменную, которая будет анимировать значение угла поворота диска
    val animatedRotationAngle by animateFloatAsState(
        targetValue = rotationAngle, label = "rotationAngle"
    )

    val texts = remember {
        digits.map { digit ->
            textMeasurer.measure(
                text = digit,
                style = typography,
            )
        }
    }

    Canvas(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    //определение нажатой цифры внутри обработчика касаний
                    val rotatedPoint = down.position.rotate(
                        center = size.center,
                        degrees = -rotationAngle,
                    )

                    val digitHit = digitBoundsList.indexOfFirst { it.contains(rotatedPoint) }

                    //сохраняем индекс цифры, если пользователь попал по ней
                    activeDigitIndex = digitHit

                    if (digitHit != -1) {
                        // Вычисляем максимальный угол поворота для выбранной цифры
                        val maxRotation = (digitHit + 1) * 27f // 270 / 10 = 27 градусов на цифру

                        var currentAngle = calculateAngle(down.position, size.center)
                        do {
                            val event = awaitPointerEvent()
                            val newAngle =
                                calculateAngle(event.changes.first().position, size.center)

                            //необходимо нормализовать deltaAngle
                            val deltaAngle = (newAngle - currentAngle).let { delta ->
                                when {
                                    delta > 180f -> delta - 360f
                                    delta < -180f -> delta + 360f
                                    else -> delta
                                }
                            }

                            val targetRotation = rotationAngle + deltaAngle

                            if (rotationAngle <= maxRotation) {
                                rotationAngle = targetRotation.coerceIn(0f, maxRotation)
                            }

                            currentAngle = newAngle
                            event.changes
                                .first()
                                .consume()
                        } while (event.changes.any { it.pressed })

                        activeDigitIndex = -1
                        //сбрасываем угол вращения диска в 0
                        rotationAngle = 0f
                    }

                }
            }

    ) {
        //шаг расположения цифр
        val angleStep = -270f / digits.size
        val radius = size.width / 2f - 40.dp.toPx()
        //цент круга
        val center = Offset(size.width / 2, size.height / 2)
        val digitSize = 56.dp.toPx()

        // Основа с цифрами
        drawCircle(
            color = baseColor,
        )

        //заполняем список digitBoundsList реальными данными уже в момент отрисоки
        digits.forEachIndexed { index, digit ->
            val angleInDegrees = index * angleStep - 45
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
            val x = center.x + radius * cos(angleInRadians).toFloat()
            val y = center.y + radius * sin(angleInRadians).toFloat()
            digitBoundsList[index].update(x, y, digitSize)
            val bounds = digitBoundsList[index]
            val xDigit = (bounds.left + bounds.right) / 2
            val yDigit = (bounds.top + bounds.bottom) / 2
            //рисуем циферблат
            val textLayoutResult: TextLayoutResult = texts[index]
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    xDigit - textLayoutResult.size.width / 2f,
                    yDigit - textLayoutResult.size.height / 2f,
                ),
            )
        }

        rotate(animatedRotationAngle) {

            // радиус внутреннего диска, который находится в центре
            val centerCircleRadius = size.width / 4f

            // Центральный круг и рамка
            drawCircle(
                color = centerAndFrameColor,
                radius = centerCircleRadius,
                center = center
            )

            // количество точек, которые будут расположены по кругу
            val dotsCount = 12

            // радиус каждой точки
            val dotRadius = 2.dp.toPx()

            // расстояние от центра диска до точек (немного меньше радиуса центрального диска)
            val dotDistance = centerCircleRadius - 10.dp.toPx()

            // отрисовываем точки по кругу
            repeat(dotsCount) { index ->

                // вычисляем угол для текущей точки (360 градусов делим на количество точек)
                val angle = (360f / dotsCount) * index

                // переводим угол в радианы для использования в тригонометрических функциях
                val angleRad = Math.toRadians(angle.toDouble())

                // вычисляем x координату точки используя косинус угла
                val dotX = center.x + dotDistance * cos(angleRad).toFloat()

                // вычисляем y координату точки используя синус угла
                val dotY = center.y + dotDistance * sin(angleRad).toFloat()

                // рисуем точку
                drawCircle(
                    color = Color.Gray,
                    radius = dotRadius,
                    center = Offset(dotX, dotY)
                )
            }

            val diskPath = Path().apply {
                // Создаем основной круг
                addOval(
                    Rect(
                        center = center,
                        radius = size.width / 2f
                    )
                )

                // Добавляем отверстия для цифр
                digits.forEachIndexed { index, _ ->
                    val angleInDegrees = index * angleStep - 45
                    val angleInRadians = Math.toRadians(angleInDegrees.toDouble())

                    val x = center.x + radius * cos(angleInRadians).toFloat()
                    val y = center.y + radius * sin(angleInRadians).toFloat()

                    addOval(
                        Rect(
                            center = Offset(x, y),
                            radius = digitSize / 2f
                        )
                    )
                }

                // Добавляем отверстие для центральной части
                addOval(
                    Rect(
                        center = center,
                        radius = centerCircleRadius
                    )
                )

                // Используем EvenOdd для создания отверстий
                fillType = PathFillType.EvenOdd
            }

            // Вращающаяся крышка с отверстиями
            drawPath(
                path = diskPath,
                color = coverColor,
            )
        }

        // рисуем внешнюю рамку телефонного диска
        drawCircle(
            color = centerAndFrameColor,
            radius = size.width / 2f,
            center = center,
            style = Stroke(
                width = 8.dp.toPx(),
            ),
        )

        // Добавляем стоппер в виде трапеции
        val stopperAngle = 0.0
        val stopperOuterDistance = size.width / 2f // внешний радиус, на границе диска
        val stopperInnerDistance = size.width / 2f - 48.dp.toPx() // увеличили глубину стоппера

        // Точки для внешней (широкой) части трапеции
        val outerLeftX = center.x + stopperOuterDistance * cos(
            Math.toRadians(stopperAngle - 5).toDouble()
        ).toFloat()
        val outerLeftY = center.y + stopperOuterDistance * sin(
            Math.toRadians(stopperAngle - 5).toDouble()
        ).toFloat()
        val outerRightX = center.x + stopperOuterDistance * cos(
            Math.toRadians(stopperAngle + 5).toDouble()
        ).toFloat()
        val outerRightY = center.y + stopperOuterDistance * sin(
            Math.toRadians(stopperAngle + 5).toDouble()
        ).toFloat()

        // Точки для внутренней (узкой) части трапеции
        val innerLeftX = center.x + stopperInnerDistance * cos(
            Math.toRadians(stopperAngle - 2.5).toDouble()
        ).toFloat()
        val innerLeftY = center.y + stopperInnerDistance * sin(
            Math.toRadians(stopperAngle - 2.5).toDouble()
        ).toFloat()
        val innerRightX = center.x + stopperInnerDistance * cos(
            Math.toRadians(stopperAngle + 2.5).toDouble()
        ).toFloat()
        val innerRightY = center.y + stopperInnerDistance * sin(
            Math.toRadians(stopperAngle + 2.5).toDouble()
        ).toFloat()

        drawPath(
            path = Path().apply {
                moveTo(outerLeftX, outerLeftY)
                lineTo(outerRightX, outerRightY)
                lineTo(innerRightX, innerRightY)
                lineTo(innerLeftX, innerLeftY)
                close()
            },
            color = centerAndFrameColor
        )
    }
}

//
private fun Offset.rotate(
    center: IntOffset,
    degrees: Float,
): Offset {
    val angle = Math.toRadians(degrees.toDouble())
    val cos = cos(angle).toFloat()
    val sin = sin(angle).toFloat()

    val x = this.x - center.x
    val y = this.y - center.y

    return Offset(
        x = center.x + (x * cos - y * sin),
        y = center.y + (x * sin + y * cos)
    )
}

//вычисляется угол поворота телефонного диска
private fun calculateAngle(position: Offset, center: IntOffset): Float {
    //При каждом движении пальца мы определяем разницу по осям X и Y:
    val dx = position.x - center.x
    val dy = position.y - center.y
    //Эти значения описывают вектор от центра круга к точке касания.
    //С этим вектором мы можем вычислить угол с помощью функции atan2
    val angleInRadians = atan2(dy.toDouble(), dx.toDouble())
//Теперь у нас есть угол в диапазоне от −180 до 180 градусов.
    return Math.toDegrees(angleInRadians).toFloat()
}

@Preview
@Composable
private fun OldPhonePreview() {
    OldPhone(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}