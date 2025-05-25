package com.example.fisicaproyec.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fisicaproyec.R
import com.example.fisicaproyec.model.FisicaModel
import com.example.fisicaproyec.viewmodel.FisicaViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: FisicaViewModel
    private lateinit var simulationView: SimulationFisicaView
    private var isInclinedPlane = true

    // Views para resultados
    private lateinit var accelerationResult: TextView
    private lateinit var tensionResult: TextView
    private lateinit var weight1Result: TextView
    private lateinit var weight2Result: TextView
    private lateinit var frictionResult: TextView
    private lateinit var normalForceResult: TextView
    private lateinit var netForceResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener el tipo de plano seleccionado
        isInclinedPlane = intent.getBooleanExtra("isInclined", true)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[FisicaViewModel::class.java]

        // Inicializar vistas
        simulationView = findViewById(R.id.simulationView)
        val mass1EditText = findViewById<EditText>(R.id.mass1EditText)
        val mass2EditText = findViewById<EditText>(R.id.mass2EditText)
        val angleEditText = findViewById<EditText>(R.id.angleEditText)
        val frictionSwitch = findViewById<Switch>(R.id.frictionSwitch)
        val frictionEditText = findViewById<EditText>(R.id.frictionEditText)
        val frictionCoefficientContainer = findViewById<LinearLayout>(R.id.frictionCoefficientContainer)
        val angleContainer = findViewById<LinearLayout>(R.id.angleContainer)
        val startButton = findViewById<Button>(R.id.startButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        // Inicializar vistas de resultados
        accelerationResult = findViewById(R.id.accelerationResult)
        tensionResult = findViewById(R.id.tensionResult)
        weight1Result = findViewById(R.id.weight1Result)
        weight2Result = findViewById(R.id.weight2Result)
        frictionResult = findViewById(R.id.frictionResult)
        normalForceResult = findViewById(R.id.normalForceResult)
        netForceResult = findViewById(R.id.netForceResult)

        // Configurar la vista de simulación
        simulationView.setPhysicsModel(viewModel.physicsModel.value!!)

        // Observar cambios en el modelo
        viewModel.physicsModel.observe(this) { model ->
            simulationView.setPhysicsModel(model)
            updateResults(model)
        }

        // Configurar listeners para los campos de entrada
        setupTextWatcher(mass1EditText) { text ->
            text.toDoubleOrNull()?.let { viewModel.updateMass1(it) }
        }

        setupTextWatcher(mass2EditText) { text ->
            text.toDoubleOrNull()?.let { viewModel.updateMass2(it) }
            }

        setupTextWatcher(angleEditText) { text ->
            text.toDoubleOrNull()?.let { viewModel.updateAngle(it) }
    }

        setupTextWatcher(frictionEditText) { text ->
            text.toDoubleOrNull()?.let { viewModel.updateFriction(it) }
        }

        // Configurar el switch de fricción
        frictionSwitch.setOnCheckedChangeListener { _, isChecked ->
            frictionCoefficientContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                viewModel.updateFriction(0.0)
                frictionEditText.setText("0.0")
            }
        }

        // Configurar la vista según el tipo de plano
        if (!isInclinedPlane) {
            angleContainer.visibility = View.GONE
            viewModel.updateAngle(0.0)
        }

        startButton.setOnClickListener {
            viewModel.startSimulation()
            simulationView.startAnimation()
        }

        stopButton.setOnClickListener {
            viewModel.stopSimulation()
            simulationView.stopAnimation()
        }
    }

    private fun updateResults(model: FisicaModel) {
        val angleRad = (model.angle * PI / 180.0)
        
        // Calcular fuerzas
        val weight1 = model.mass1 * FisicaModel.GRAVEDAD
        val weight2 = model.mass2 * FisicaModel.GRAVEDAD
        val normalForce = weight1 * cos(angleRad)
        val frictionForce = model.friction * normalForce
        val netForce = weight2 - (weight1 * sin(angleRad) + frictionForce)

        // Actualizar textos
        accelerationResult.text = "Aceleración: ${String.format("%.2f", model.acceleration)} m/s²"
        tensionResult.text = "Tensión: ${String.format("%.2f", model.tension)} N"
        weight1Result.text = "Peso Masa 1: ${String.format("%.2f", weight1)} N"
        weight2Result.text = "Peso Masa 2: ${String.format("%.2f", weight2)} N"
        frictionResult.text = "Fuerza de Fricción: ${String.format("%.2f", frictionForce)} N"
        normalForceResult.text = "Fuerza Normal: ${String.format("%.2f", normalForce)} N"
        netForceResult.text = "Fuerza Neta: ${String.format("%.2f", netForce)} N"
    }

    private fun setupTextWatcher(editText: EditText, onTextChanged: (String) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { onTextChanged(it) }
            }
        })
    }
}