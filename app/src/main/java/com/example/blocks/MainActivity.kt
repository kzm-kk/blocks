package com.example.blocks


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        player.setOnClickListener{
            var i = Intent(applicationContext, VersusActivity::class.java)
            startActivity(i)
        }
        CPU.setOnClickListener {
            /*var i = Intent(applicationContext, CpuActivity::class.java)
            startActivity(i)
            */
        }
    }
}
