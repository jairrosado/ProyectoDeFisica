package com.example.fisicaproyec.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.fisicaproyec.R

class SelectionDePlanosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plane_selection)

        findViewById<Button>(R.id.btnInclinedPlane).setOnClickListener {
            startSimulation(true)
        }

        findViewById<Button>(R.id.btnHorizontalPlane).setOnClickListener {
            startSimulation(false)
        }
    }

    private fun startSimulation(isInclined: Boolean) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("isInclined", isInclined)
        }
        startActivity(intent)
    }
} 