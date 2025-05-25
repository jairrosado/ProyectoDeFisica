package com.example.fisicaproyec.view

import com.example.fisicaproyec.model.FisicaModel

class InclinedPlaneAnimationController : BaseAnimationController() {
    override fun updateAnimation(model: FisicaModel): Float {
        if (!isAnimating) return progress

        val frictionDamping = 1.0f - (model.friction * 0.5f).toFloat()
        val effectiveDamping = baseDampingFactor * frictionDamping

        // Calcular la aceleración basada en la diferencia de masas y fricción
        val massDifference = model.mass2 - model.mass1

        val acceleration = if (massDifference == 0.0) {
            0f
        } else if (massDifference > 0) {
            (massDifference * FisicaModel.GRAVEDAD * 0.005 * (1.0 + model.friction)).toFloat()
        } else {
           -(massDifference * FisicaModel.GRAVEDAD * 0.005 * (1.0 - model.friction)).toFloat()
        }

        // Actualizar velocidad con efecto de fricción
        val newVelocity = velocity * effectiveDamping + acceleration
        updateVelocity(newVelocity)

        // Actualizar el progreso de la animación basado en la velocidad
        // Un factor pequeño para controlar la velocidad visual
        val newProgress = if (model.mass1 > model.mass2) {
            // If mass 1 is heavier, decrease progress to move mass 1 down and mass 2 up
            progress - velocity * 0.0005f // Note: velocity will be positive in this case
        } else {
            // Otherwise, increase progress for normal movement (mass 2 down, mass 1 up)
            progress + velocity * 0.0005f
        }

        updateProgress(newProgress)

        // Limitar el progreso entre 0 y 1
        if (progress >= 1f) {
            updateProgress(1f)
            updateVelocity(0f)
        } else if (progress <= 0f) {
            updateProgress(0f)
            updateVelocity(0f)
        }

        return progress
    }
}