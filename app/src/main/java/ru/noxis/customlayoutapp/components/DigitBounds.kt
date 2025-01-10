package ru.noxis.customlayoutapp.components

import androidx.compose.ui.geometry.Offset

/**
 * класс для хранения границ каждой цифры.
 */
class DigitBounds(
    var left: Float = 0f,
    var top: Float = 0f,
    var right: Float = 0f,
    var bottom: Float = 0f
) {

    /**
     * Проверяет, находится ли точка внутри границ цифры
     * Используется для определения нажатой цифры
     */
    fun contains(point: Offset): Boolean {
        return point.x >= left && point.x <= right &&
                point.y >= top && point.y <= bottom
    }

    /**
     * Обновляет границы цифры при отрисовке, основываясь на её позиции (x, y) и размерах
     */
    fun update(x: Float, y: Float, size: Float) {
        left = x - size / 2
        top = y - size / 2
        right = x + size / 2
        bottom = y + size / 2
    }
}