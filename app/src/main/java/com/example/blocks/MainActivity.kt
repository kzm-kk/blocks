package com.example.blocks


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        message.setText("どちらで遊びますか？　")
        player.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                var i = Intent(this@MainActivity, VersusActivity::class.java)
                startActivity(i)
            }
        })
        CPU.setOnClickListener {
            var i = Intent(this@MainActivity, CpuActivity::class.java)
            startActivity(i)

        }
    }
}
