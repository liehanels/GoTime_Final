package com.example.gotime_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

class ViewProjectStats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_project_stats)
        //back button
        var btnBack = findViewById<Button>(R.id.btnBackFromStats)
        btnBack.setOnClickListener() {
            val intent = Intent(this, ViewProjects::class.java)
            startActivity(intent)
        }
    }
}