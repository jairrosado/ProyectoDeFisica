package com.example.fisicaproyec.view

import android.graphics.*
import com.example.fisicaproyec.model.FisicaModel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.tan

class SimulationRenderer {
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        textSize = 40f
        color = Color.BLACK
    }

    private val shadowPaint = Paint().apply {
        isAntiAlias = true
        color = Color.argb(50, 0, 0, 0)
        maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
    }

    fun drawSimulation(
        canvas: Canvas,
        model: FisicaModel,
        width: Float,
        height: Float,
        animationProgress: Float,
        velocity: Float
    ) {
        val centerX = width / 2
        val centerY = height / 2

        // Calcular los pesos de las masas
        val mass1Weight = model.mass1 * FisicaModel.GRAVEDAD
        val mass2Weight = model.mass2 * FisicaModel.GRAVEDAD

        if (model.angle > 0) {
            drawInclinedPlane(canvas, model, centerX, centerY, width, animationProgress, mass1Weight, mass2Weight)
        } else {
            drawHorizontalPlane(canvas, model, centerX, centerY, width, animationProgress)
        }

        // Dibujar etiquetas
        drawLabels(canvas, model, width, height, velocity)
    }

    private fun drawInclinedPlane(
        canvas: Canvas,
        model: FisicaModel,
        centerX: Float,
        centerY: Float,
        width: Float,
        animationProgress: Float,
        mass1Weight: Double,
        mass2Weight: Double
    ) {
        val base = width * 0.4f
        val angleRad = (model.angle * PI / 180.0).toFloat()
        val heightTri = base * tan(angleRad)
        val triLeftX = centerX - base / 2
        val triLeftY = centerY + heightTri / 2
        val triRightX = centerX + base / 2
        val triRightY = centerY + heightTri / 2
        val triTopX = triRightX
        val triTopY = triRightY - heightTri

        // Dibujar triángulo
        val triPath = Path()
        triPath.moveTo(triLeftX, triLeftY)
        triPath.lineTo(triRightX, triRightY)
        triPath.lineTo(triTopX, triTopY)
        triPath.close()
        paint.color = Color.YELLOW
        paint.style = Paint.Style.FILL
        canvas.drawPath(triPath, paint)
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        canvas.drawPath(triPath, paint)

        // Dibujar polea
        val pulleyRadius = 20f
        val pulleyX = triTopX
        val pulleyY = triTopY
        paint.color = Color.GRAY
        paint.style = Paint.Style.FILL
        canvas.drawCircle(pulleyX, pulleyY, pulleyRadius, paint)
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(pulleyX, pulleyY, pulleyRadius, paint)

        // Dibujar masas y cuerdas
        drawMassesAndRopes(canvas, model, triLeftX, triLeftY, triTopX, triTopY, pulleyX, pulleyY, animationProgress, mass1Weight, mass2Weight)
    }

    private fun drawHorizontalPlane(
        canvas: Canvas,
        model: FisicaModel,
        centerX: Float,
        centerY: Float,
        width: Float,
        animationProgress: Float
    ) {
        val planeLength = width * 0.4f
        paint.color = Color.YELLOW
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            centerX - planeLength/2,
            centerY - planeLength/2,
            centerX + planeLength/2,
            centerY + planeLength/2,
            paint
        )
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        canvas.drawRect(
            centerX - planeLength/2,
            centerY - planeLength/2,
            centerX + planeLength/2,
            centerY + planeLength/2,
            paint
        )

        // Polea en la esquina superior derecha
        val pulleyRadius = 20f
        val pulleyX = centerX + planeLength/2
        val pulleyY = centerY - planeLength/2
        paint.color = Color.GRAY
        paint.style = Paint.Style.FILL
        canvas.drawCircle(pulleyX, pulleyY, pulleyRadius, paint)
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(pulleyX, pulleyY, pulleyRadius, paint)

        // Dibujar masas y cuerdas
        val massSize = 60f
        val m1X = centerX - planeLength/2 + (planeLength) * animationProgress
        val m1Y = centerY - planeLength/2
        val m2Y = pulleyY + 100f + 200f * animationProgress

        // Masa m1
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            m1X - massSize/2,
            m1Y - massSize/2,
            m1X + massSize/2,
            m1Y + massSize/2,
            paint
        )

        // Masa m2
        canvas.drawRect(
            pulleyX - massSize/2,
            m2Y - massSize/2,
            pulleyX + massSize/2,
            m2Y + massSize/2,
            paint
        )

        // Cuerdas
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawLine(m1X, m1Y, pulleyX, pulleyY, paint)
        canvas.drawLine(pulleyX, pulleyY, pulleyX, m2Y, paint)

        // Dibujar vectores de fuerza
        drawForceVectors(canvas, model, m1X, m1Y, pulleyX, m2Y)
    }

    private fun drawMassesAndRopes(
        canvas: Canvas,
        model: FisicaModel,
        triLeftX: Float,
        triLeftY: Float,
        triTopX: Float,
        triTopY: Float,
        pulleyX: Float,
        pulleyY: Float,
        animationProgress: Float,
        mass1Weight: Double,
        mass2Weight: Double
    ) {
        val massSize = 60f
        val m1Progress = animationProgress
        val m2Progress = animationProgress

        // Longitud del plano inclinado
        val inclinedLength = Math.sqrt(Math.pow((triTopX - triLeftX).toDouble(), 2.0) + Math.pow((triTopY - triLeftY).toDouble(), 2.0)).toFloat()
        // Punto medio en el plano inclinado
        val midInclinedX = triLeftX + (triTopX - triLeftX) * 0.5f
        val midInclinedY = triLeftY + (triTopY - triLeftY) * 0.5f

        // Ajustar el progreso para que el centro sea el inicio (progreso 0)
        // Recorrido total en el plano inclinado (ajustable si solo quieres una parte del plano)
        val totalInclinedTravel = inclinedLength * 0.8f // Usar el 80% del plano por ejemplo
        // Punto de inicio ajustado para que el centro del recorrido sea el centro del plano
        val startInclinedX = midInclinedX - (triTopX - triLeftX) * (totalInclinedTravel / 2f / inclinedLength)
        val startInclinedY = midInclinedY - (triTopY - triLeftY) * (totalInclinedTravel / 2f / inclinedLength)

        // Calcular la posición actual en el plano inclinado
        val m1X = startInclinedX + (triTopX - triLeftX) * (totalInclinedTravel / inclinedLength) * m1Progress
        val m1Y = startInclinedY + (triTopY - triLeftY) * (totalInclinedTravel / inclinedLength) * m1Progress

        // Posición vertical de la masa 2
        val totalVerticalTravel = 200f // El rango de 100 a 300, total 200f
        val startVerticalY = pulleyY + 100f // Inicio anterior
        // Punto de inicio ajustado para que el centro del recorrido vertical sea el inicio (progreso 0)
        val adjustedStartVerticalY = startVerticalY + totalVerticalTravel / 2f // pulleyY + 100 + 100 = pulleyY + 200

        // Calcular la posición actual vertical de la masa 2
        val m2Y = (pulleyY + 100f) + totalVerticalTravel * m2Progress


        // Masa m1
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            m1X - massSize/2,
            m1Y - massSize/2,
            m1X + massSize/2,
            m1Y + massSize/2,
            paint
        )

        // Masa m2
        canvas.drawRect(
            pulleyX - massSize/2,
            m2Y - massSize/2,
            pulleyX + massSize/2,
            m2Y + massSize/2,
            paint
        )

        // Cuerdas
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawLine(m1X, m1Y, pulleyX, pulleyY, paint)
        canvas.drawLine(pulleyX, pulleyY, pulleyX, m2Y, paint)

        // Dibujar vectores de fuerza
        drawForceVectors(canvas, model, m1X, m1Y, pulleyX, m2Y)
    }

    private fun drawForceVectors(
        canvas: Canvas,
        model: FisicaModel,
        m1X: Float,
        m1Y: Float,
        pulleyX: Float,
        m2Y: Float
    ) {
        val forceScale = 0.1f
        val arrowSize = 20f

        // Vectores de peso
        paint.color = Color.RED
        paint.strokeWidth = 3f
        drawArrow(
            canvas,
            m1X,
            m1Y,
            m1X,
            m1Y + (model.mass1 * FisicaModel.GRAVEDAD * forceScale).toFloat(),
            arrowSize
        )
        drawArrow(
            canvas,
            pulleyX,
            m2Y,
            pulleyX,
            m2Y + (model.mass2 * FisicaModel.GRAVEDAD * forceScale).toFloat(),
            arrowSize
        )

        // Vectores de tensión
        paint.color = Color.GREEN
        val tensionLength = (model.tension * forceScale).toFloat()
        drawArrow(
            canvas,
            m1X,
            m1Y,
            m1X + tensionLength,
            m1Y,
            arrowSize
        )
        drawArrow(
            canvas,
            pulleyX,
            m2Y,
            pulleyX,
            m2Y - tensionLength,
            arrowSize
        )

        // Vector de fuerza normal
        paint.color = Color.BLUE
        val normalLength = (model.mass1 * FisicaModel.GRAVEDAD * forceScale).toFloat()
        drawArrow(
            canvas,
            m1X,
            m1Y,
            m1X,
            m1Y - normalLength,
            arrowSize
        )

        // Vector de fuerza de fricción
        if (model.friction > 0) {
            paint.color = Color.MAGENTA
            val frictionLength = (model.friction * model.mass1 * FisicaModel.GRAVEDAD * forceScale).toFloat()
            drawArrow(
                canvas,
                m1X,
                m1Y,
                m1X - frictionLength,
                m1Y,
                arrowSize
            )
        }
    }

    private fun drawLabels(
        canvas: Canvas,
        model: FisicaModel,
        width: Float,
        height: Float,
        velocity: Float
    ) {
        textPaint.textSize = 30f
        canvas.drawText("m1 = ${String.format("%.1f", model.mass1)} kg", 50f, 50f, textPaint)
        canvas.drawText("m2 = ${String.format("%.1f", model.mass2)} kg", 50f, 100f, textPaint)
        canvas.drawText("θ = ${String.format("%.0f", model.angle)}°", 50f, 150f, textPaint)
        canvas.drawText("T = ${String.format("%.1f", model.tension)} N", width - 200f, 50f, textPaint)
        canvas.drawText("a = ${String.format("%.2f", model.acceleration)} m/s²", width - 200f, 100f, textPaint)
        canvas.drawText("v = ${String.format("%.2f", velocity)} m/s", width - 200f, 150f, textPaint)
        canvas.drawText("F = ${String.format("%.2f", model.friction)}", width - 200f, 200f, textPaint)
    }

    private fun drawArrow(canvas: Canvas, startX: Float, startY: Float, endX: Float, endY: Float, arrowSize: Float) {
        canvas.drawLine(startX, startY, endX, endY, paint)

        val angle = Math.atan2((endY - startY).toDouble(), (endX - startX).toDouble())

        val arrowX1 = endX - arrowSize * cos(angle - Math.PI / 6).toFloat()
        val arrowY1 = endY - arrowSize * sin(angle - Math.PI / 6).toFloat()
        val arrowX2 = endX - arrowSize * cos(angle + Math.PI / 6).toFloat()
        val arrowY2 = endY - arrowSize * sin(angle + Math.PI / 6).toFloat()

        canvas.drawLine(endX, endY, arrowX1, arrowY1, paint)
        canvas.drawLine(endX, endY, arrowX2, arrowY2, paint)
    }
} 