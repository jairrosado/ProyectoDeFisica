package com.example.fisicaproyec.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fisicaproyec.model.FisicaModel

class FisicaViewModel : ViewModel() {
    private val _fisicaModel = MutableLiveData(FisicaModel())
    val physicsModel: LiveData<FisicaModel> = _fisicaModel

    private val _isSimulating = MutableLiveData(false)
    val isSimulating: LiveData<Boolean> = _isSimulating

    fun updateMass1(mass: Double) {
        _fisicaModel.value = _fisicaModel.value?.copy(mass1 = mass)
    }

    fun updateMass2(mass: Double) {
        _fisicaModel.value = _fisicaModel.value?.copy(mass2 = mass)
    }

    fun updateAngle(angle: Double) {
        _fisicaModel.value = _fisicaModel.value?.copy(angle = angle)
    }

    fun updateFriction(friction: Double) {
        _fisicaModel.value = _fisicaModel.value?.copy(friction = friction)
    }

    fun startSimulation() {
        _isSimulating.value = true
    }

    fun stopSimulation() {
        _isSimulating.value = false
    }
} 