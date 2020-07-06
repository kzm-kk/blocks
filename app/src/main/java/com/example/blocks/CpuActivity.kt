package com.example.blocks

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_versus.*

class CpuActivity : AppCompatActivity() {
    lateinit var myView: boardview
    var turn = 2
    var Opened = true
    lateinit var alertDialog: AlertDialog
    var play1_playable = true
    var play2_playable = true
    var play1_preflag = true
    var play2_preflag = true
    private val handler = Handler()
    private var runnable: Runnable? = null
    var delaytime = 1500
    var timing = 1
    var red_block = 0
    var blue_block = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_versus)
        myView = findViewById(R.id.view1)
        button.setOnClickListener{
            //auto_play()
        }
        button3.setOnClickListener {
            alertDialog = AlertDialog.Builder(applicationContext)
                .setTitle("ゲームを最初からにしますか？")
                .setPositiveButton(
                    "はい",
                    DialogInterface.OnClickListener { dialog, which -> restart_game() })
                .setNegativeButton(
                    "いいえ",
                    DialogInterface.OnClickListener { dialog, which -> alertDialog.dismiss() })
                .show()
        }
        Handler().postDelayed({ turngo() }, delaytime.toLong())
        partsExpansion()
    }

    fun turngo(){
        runnable = object : Runnable {
            override fun run() {
                running()
            }
        }
        handler.post(runnable)
    }

    fun running(){
        while(timing == 1){
            timing = 0
            play1_playable = myView.set_state(2)
            play2_playable = myView.set_state(3)
            declaration_skip()
            result(play1_playable, play2_playable)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        textView.setTextColor(Color.WHITE)
        val pointX = event!!.getX()
        val pointY = event!!.getY()
        if (event.action == MotionEvent.ACTION_UP) {
            if (pointY < 1130) {
                if (parts[turn - 2][presentParts.kinds].getUsable()) {
                    if (Opened && myView.recheck_able_set(pointX, pointY, turn - 2)) {
                        Opened = false
                        myView.setblock3(turn)
                        presentParts.setUsable(false)
                        if (turn == 2 && play2_playable) turn = 3
                        else if (turn == 3 && play1_playable) turn = 2
                        timing = 1
                        Handler().postDelayed({ turngo() }, delaytime.toLong())
                        partsExpansion()
                    }
                }
            }
        }
        red_block = myView.count_block(2)
        blue_block = myView.count_block(3)
        textView.setText("red:" + red_block.toString() + "blue:" + blue_block.toString())
        return super.onTouchEvent(event)
    }

    fun partsExpansion(){
        Opened = true
        val fragment01 = partsfragment().newInstance(turn.toString())
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        //container.setOnTouchListener(fragment01)
        fragmentTransaction.replace(R.id.container, fragment01, "tag")
        fragmentTransaction.commit()
    }

    fun declaration_skip(){
        var play1_flag = (play1_playable != play1_preflag) && !play1_playable
        var play2_flag = (play2_playable != play2_preflag) && !play2_playable
        if(play1_flag && play2_flag)
            Toast.makeText(applicationContext, "全プレイヤーがもう置けません", Toast.LENGTH_SHORT).show()
        else {
            if (play1_flag) {
                Toast.makeText(applicationContext, "player1はもう置けません", Toast.LENGTH_SHORT).show()
                turn = 3
                partsExpansion()
            }

            if (play2_flag) {
                Toast.makeText(applicationContext, "player2はもう置けません", Toast.LENGTH_SHORT).show()
                turn = 2
                partsExpansion()
            }
        }
        play1_preflag = play1_playable
        play2_preflag = play2_playable
    }

    fun result(player1:Boolean, player2: Boolean){
        var minus = red_block - blue_block
        if(!player1 && !player2){
            var winner = ""
            if(minus < 0) winner = "player2"
            else if(minus > 0) winner = "player1"
            else winner = "none"
            alertDialog = AlertDialog.Builder(applicationContext)
                .setTitle(winner+" wins " +Math.abs(minus) + "point")
                .setCancelable(false)
                .setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, which -> alertDialog.dismiss() })
                .show()
        }
    }

    fun auto_play(){
        /*if (parts[turn - 2][presentParts.kinds].getUsable()) {
            if (Opened && myView.recheck_able_set(pointX, pointY, turn - 2)) {
                Opened = false
                myView.setblock3(turn)
                presentParts.setUsable(false)
                if (turn == 2 && play2_playable) turn = 3
                else if (turn == 3 && play1_playable) turn = 2
                timing = 1
                Handler().postDelayed({ turngo() }, delaytime.toLong())
                partsExpansion()
            }
        }*/

        red_block = myView.count_block(2)
        blue_block = myView.count_block(3)
        textView.setText("red:" + red_block.toString() + "blue:" + blue_block.toString())
    }

    fun restart_game(){
        play1_playable = true
        play2_playable = true
        timing = 1
        red_block = 0
        blue_block = 0
        turn = 2
        partsExpansion()
        textView.setText("red:" + red_block.toString() + "blue:" + blue_block.toString())
        myView.restart()
        //var hogeFragment = getSupportFragmentManager().findFragmentByTag("tag")
        //if(hogeFragment != null)finish_fragment(hogeFragment)
    }

    fun finish_fragment(flag: Fragment){
        getSupportFragmentManager().beginTransaction().remove(flag).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        alertDialog = AlertDialog.Builder(applicationContext)
            .setTitle("ゲームを終了しますか？")
            .setPositiveButton(
                "はい",
                DialogInterface.OnClickListener { dialog, which -> moveTaskToBack(true) })
            .setNegativeButton(
                "いいえ",
                DialogInterface.OnClickListener { dialog, which -> alertDialog.dismiss() })
            .show()
    }

}
