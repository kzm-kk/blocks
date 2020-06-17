package com.example.blocks

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var myView: boardview
    var turn = 2
    var Opened = false
    lateinit var alertDialog:AlertDialog
    var play1_playable = true
    var play2_playable = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myView = findViewById(R.id.view1)
        button.setOnClickListener{
            /*var hogeFragment = getSupportFragmentManager().findFragmentByTag("tag")
            if(hogeFragment != null)finish_fragment(hogeFragment)*/
            alertDialog = AlertDialog.Builder(this@MainActivity)
                .setTitle("自分の番をスキップしますか？")
                .setPositiveButton(
                    "はい",
                    DialogInterface.OnClickListener { dialog, which -> skip_player(turn) })
                .setNegativeButton(
                    "いいえ",
                    DialogInterface.OnClickListener { dialog, which -> alertDialog.dismiss() })
                .show()

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
                if(myView.recheck_able_set(pointX, pointY, turn)){//myView.check_dup_cyan(pointX, pointY, turn - 2)){
                    //myView.setblock(pointX, pointY, turn - 2)
                    myView.setblock2(turn)
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

    fun skip_player(turn:Int){
        if(turn == 2) {
            play1_playable = false
            //turn = 3
        } else {
            play2_playable = false
            //turn = 2
        }
    }

    fun finish_fragment(flag:Fragment){
        getSupportFragmentManager().beginTransaction().remove(flag).commit()
    }

}
