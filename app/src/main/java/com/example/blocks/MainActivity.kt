package com.example.blocks

import android.graphics.Color
import android.graphics.Path
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var myView: boardview
    var turn = 2
    var Opened = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myView = findViewById(R.id.view1)
        button.setOnClickListener{
            /*var hogeFragment = getSupportFragmentManager().findFragmentByTag("tag")
            if(hogeFragment != null)finish_fragment(hogeFragment)*/

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        textView.setTextColor(Color.WHITE)
        val pointX = event!!.getX()
        val pointY = event!!.getY()
        if(pointY > 1130) {
            if(!Opened) {
                Opened = true
                partsExpansion()
            }
        } else {
            if(Opened && parts[turn - 2][presentParts.kinds].getUsable()){
                if(myView.check_dup_cyan(pointX, pointY, turn - 2)){
                    Opened = false
                    presentParts.setUsable(false)
                    if(turn == 2)turn = 3
                    else turn = 2
                }
            }
        }
        textView.setText("red:" + myView.count_block(2).toString() + "blue:" + myView.count_block(3).toString())
        return super.onTouchEvent(event)
    }

    fun partsExpansion(){
        val fragment01 = partsfragment().newInstance(turn.toString())
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        //container.setOnTouchListener(fragment01)
        fragmentTransaction.replace(R.id.container, fragment01, "tag")
        fragmentTransaction.commit()
    }

    fun finish_fragment(flag:Fragment){
        getSupportFragmentManager().beginTransaction().remove(flag).commit()
    }

}
