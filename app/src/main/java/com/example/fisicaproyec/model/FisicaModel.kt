package com.example.fisicaproyec.model

data class FisicaModel(
    val mass1: Double = 1.0,
    val mass2: Double = 1.0,
    val angle: Double = 30.0,
    val friction: Double = 0.0
) {
    companion object {
        const val GRAVEDAD = 9.8
    }

    val acceleration: Double
        get() {
            val angleRad = Math.toRadians(angle)
            val m1 = mass1
            val m2 = mass2
            val mu = friction
            
            // Componente del peso de m1 paralela al plano
            val m1g_parallel = m1 * GRAVEDAD * Math.sin(angleRad)
            // Componente del peso de m1 perpendicular al plano
            val m1g_perpendicular = m1 * GRAVEDAD * Math.cos(angleRad)
            // Fuerza de fricción
            val frictionForce = mu * m1g_perpendicular
            
            // Fuerza neta
            val netForce = m2 * GRAVEDAD - m1g_parallel - frictionForce
            
            // Aceleración del sistema
            return if (netForce > 0) {
                netForce / (m1 + m2)
            } else {
                0.0
            }
        }

    val tension: Double
        get() {
            val angleRad = Math.toRadians(angle)
            val m1 = mass1
            val m2 = mass2
            val mu = friction
            
            // Componente del peso de m1 paralela al plano
            val m1g_parallel = m1 * GRAVEDAD * Math.sin(angleRad)
            // Componente del peso de m1 perpendicular al plano
            val m1g_perpendicular = m1 * GRAVEDAD * Math.cos(angleRad)
            // Fuerza de fricción
            val frictionForce = mu * m1g_perpendicular
            
            // Tensión en la cuerda
            return if (acceleration > 0) {
                m2 * (GRAVEDAD - acceleration)
            } else {
                m2 * GRAVEDAD
            }
        }
} 