package com.mentality.gamescope.ui.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.mentality.gamescope.data.model.CrosshairConfig
import com.mentality.gamescope.data.model.CrosshairStyle
import kotlin.math.min

/**
 * Custom View для отрисовки прицела поверх других приложений
 * Поддерживает три стиля: точка (DOT), крестик (CROSSHAIR), круг (CIRCLE)
 */
class CrosshairView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var config: CrosshairConfig = CrosshairConfig.getDefault()

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    /**
     * Установить конфигурацию прицела и перерисовать
     */
    fun setConfig(newConfig: CrosshairConfig) {
        config = newConfig
        invalidate()
    }

    /**
     * Получить текущую конфигурацию
     */
    fun getConfig(): CrosshairConfig = config

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Обновить краску в зависимости от конфига
        updatePaint()

        // Получить размеры Canvas и центр
        val centerX = width / 2f
        val centerY = height / 2f

        // Рисовать прицел в зависимости от стиля
        when (config.style) {
            CrosshairStyle.DOT -> drawDot(canvas, centerX, centerY)
            CrosshairStyle.CROSSHAIR -> drawCrosshair(canvas, centerX, centerY)
            CrosshairStyle.CIRCLE -> drawCircle(canvas, centerX, centerY)
        }
    }

    /**
     * Обновить параметры краски
     */
    private fun updatePaint() {
        // Парсить цвет из hex строки
        paint.color = try {
            android.graphics.Color.parseColor(config.color)
        } catch (e: Exception) {
            android.graphics.Color.WHITE
        }

        // Установить толщину линии
        paint.strokeWidth = config.thickness

        // Установить альфу (прозрачность)
        paint.alpha = (config.alpha * 255).toInt()
    }

    /**
     * Нарисовать прицел в форме точки
     */
    private fun drawDot(canvas: Canvas, centerX: Float, centerY: Float) {
        // Dot should be much smaller than the crosshair/circle, based on thickness
        val radius = (config.thickness * config.size * context.resources.displayMetrics.density) / 2

        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    /**
     * Нарисовать прицел в форме крестика
     */
    private fun drawCrosshair(canvas: Canvas, centerX: Float, centerY: Float) {
        val baseSize = getScaledSize()
        val armEnd = (baseSize / 2) * config.lineLength
        val gap = baseSize * config.gapSize

        paint.style = Paint.Style.STROKE

        // Вертикальные линии
        canvas.drawLine(centerX, centerY - armEnd, centerX, centerY - gap, paint)
        canvas.drawLine(centerX, centerY + gap, centerX, centerY + armEnd, paint)

        // Горизонтальные линии
        canvas.drawLine(centerX - armEnd, centerY, centerX - gap, centerY, paint)
        canvas.drawLine(centerX + gap, centerY, centerX + armEnd, centerY, paint)

        // Центральная точка
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, paint.strokeWidth * 0.5f, paint)
    }

    /**
     * Нарисовать прицел в форме круга
     */
    private fun drawCircle(canvas: Canvas, centerX: Float, centerY: Float) {
        val radius = getScaledSize() / 2

        paint.style = Paint.Style.STROKE
        canvas.drawCircle(centerX, centerY, radius, paint)

        if (config.showCenterCross) {
            val crossSize = radius * config.centerCrossSize
            canvas.drawLine(centerX - crossSize, centerY, centerX + crossSize, centerY, paint)
            canvas.drawLine(centerX, centerY - crossSize, centerX, centerY + crossSize, paint)
        }
    }

    /**
     * Получить масштабированный размер прицела на основе конфига
     */
    private fun getScaledSize(): Float {
        // Базовый размер ~ 40dp, масштабируется в зависимости от конфига
        val baseSizeInDp = 40f
        val density = context.resources.displayMetrics.density
        val baseSizeInPx = baseSizeInDp * density
        return baseSizeInPx * config.size
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = (200 * resources.displayMetrics.density).toInt()
        setMeasuredDimension(size, size)
    }

    /**
     * Установить размер View явно (для WindowManager)
     */
    fun setCustomSize(width: Int, height: Int) {
        layoutParams = layoutParams.apply {
            this.width = width
            this.height = height
        } ?: android.view.ViewGroup.LayoutParams(width, height)
    }
}
