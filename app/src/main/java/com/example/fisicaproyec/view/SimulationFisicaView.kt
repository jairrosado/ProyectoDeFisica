package com.example.fisicaproyec.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.example.fisicaproyec.model.FisicaModel

class SimulationFisicaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var physicsModel: FisicaModel? = null
    private var animationController: BaseAnimationController = HorizontalPlaneAnimationController()
    private val simulationRenderer = SimulationRenderer()

    fun setPhysicsModel(model: FisicaModel) {
        this.physicsModel = model
        // Cambiar el controlador de animación según el tipo de plano
        animationController = if (model.angle > 0) {
            InclinedPlaneAnimationController()
        } else {
            HorizontalPlaneAnimationController()
        }
        invalidate()
    }

    fun startAnimation() {
        animationController.startAnimation()
        invalidate()
    }

    fun stopAnimation() {
        animationController.stopAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val model = physicsModel ?: return

        val width = width.toFloat()
        val height = height.toFloat()

        // Actualizar la animación
        val progress = animationController.updateAnimation(model)

        // Dibujar la simulación
        simulationRenderer.drawSimulation(
            canvas,
            model,
            width,
            height,
            progress,
            animationController.velocity
        )

        // Si la animación está activa, continuar actualizando
        if (animationController.isAnimating) {
            invalidate()
        }
    }
} 