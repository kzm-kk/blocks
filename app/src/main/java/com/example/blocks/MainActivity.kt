package com.example.blocks

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
    private val handler = Handler()
    private var runnable: Runnable? = null
    var delaytime = 1500
    var timing = 1
    var red_block = 0
    var blue_block = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myView = findViewById(R.id.view1)
        button.setOnClickListener{
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
        button3.setOnClickListener {
            alertDialog = AlertDialog.Builder(this@MainActivity)
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
    }

    fun turngo(){
        runnable = object : Runnable {
            override fun run() {
                action()
            }
        }
        handler.post(runnable)
    }

    fun action(){
        while(timing == 1){
            timing = 0
            play1_playable = myView.set_state(2)
            play2_playable = myView.set_state(3)
            result(play1_playable, play2_playable)
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
                if(myView.recheck_able_set(pointX, pointY, turn - 2)){//myView.check_dup_cyan(pointX, pointY, turn - 2)){
                    //myView.setblock(pointX, pointY, turn - 2)
                    //myView.setblock2(turn)
                    myView.setblock3(turn)
                    Opened = false
                    presentParts.setUsable(false)
                    if(turn == 2 && play2_playable) turn = 3
                    else if(turn == 3 && play1_playable)turn = 2
                    timing = 1
                    Handler().postDelayed({ turngo() }, delaytime.toLong())
                }
            }
        }
        red_block = myView.count_block(2)
        blue_block = myView.count_block(3)
        textView.setText("red:" + red_block.toString() + "blue:" + blue_block.toString())
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

    fun skip_player(num:Int){
        if(num == 2) {
            play1_playable = false
            turn = 3
        } else {
            play2_playable = false
            turn = 2
        }
    }

    fun result(player1:Boolean, player2: Boolean){
        var minus = red_block - blue_block
        if(!player1 && !player2){
            var winner = ""
            if(minus < 0) winner = "player2"
            else winner = "player1"
            alertDialog = AlertDialog.Builder(this@MainActivity)
                .setTitle(winner+" wins " +Math.abs(minus) + "point")
                .setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, which -> alertDialog.dismiss() })
                .show()
        }
    }

    fun restart_game(){
        play1_playable = true
        play2_playable = true
        Opened = false
        timing = 1
        red_block = 0
        blue_block = 0
        turn = 2
        myView.restart()
        var hogeFragment = getSupportFragmentManager().findFragmentByTag("tag")
        if(hogeFragment != null)finish_fragment(hogeFragment)
    }

    fun finish_fragment(flag:Fragment){
        getSupportFragmentManager().beginTransaction().remove(flag).commit()
    }

}
