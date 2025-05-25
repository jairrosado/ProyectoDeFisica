package com.example.fisicaproyec.view

import com.example.fisicaproyec.model.FisicaModel

abstract class BaseAnimationController {
    private var _animationProgress = 0f
    private var _isAnimating = false
    private var _velocity = 0f
    protected val baseDampingFactor = 0.995f
    protected val movementScale = 0.2f

    val velocity: Float get() = _velocity
    val isAnimating: Boolean get() = _isAnimating
    val progress: Float get() = _animationProgress

    fun startAnimation() {
        _isAnimating = true
        _animationProgress = 0f
        _velocity = 0f
    }

    fun stopAnimation() {
        _isAnimating = false
    }

    protected fun updateVelocity(newVelocity: Float) {
        _velocity = newVelocity
    }

    protected fun updateProgress(newProgress: Float) {
        _animationProgress = newProgress
    }

    abstract fun updateAnimation(model: FisicaModel): Float
} 