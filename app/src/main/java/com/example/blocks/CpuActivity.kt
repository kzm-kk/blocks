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
            if(Opened)auto_thinking(2)
        }
        button3.setOnClickListener {
            alertDialog = AlertDialog.Builder(this@CpuActivity)
                .setTitle("ゲームを最初からにしますか？")
                .setPositiveButton(
                    "はい",
                    DialogInterface.OnClickListener { dialog, which -> restart_game() })
                .setNegativeButton(
                    "いいえ",
                    DialogInterface.OnClickListener { dialog, which -> alertDialog.dismiss() })
                .show()
        }
        restart_game()
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
            if(turn == 3) auto_thinking(3)
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
            alertDialog = AlertDialog.Builder(this@CpuActivity)
                .setTitle(winner+" wins " +Math.abs(minus) + "point")
                .setCancelable(false)
                .setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, which -> alertDialog.dismiss() })
                .show()
        }
    }

    fun auto_thinking(turn : Int){
        var usable = false
        var flag_break = false
        var sendbox = Array(7, {i -> Array(7, {i -> 0})})
        var tmpbox = Array(7, {i -> Array(7, {i -> 0})})
        var line = 0
        var row = 0
        var kind = 0
        var rad = 0
        var reverse = false
        for(h in 20 downTo 0) {
            usable = parts[turn - 2][h].getUsable()
            if (usable) {
                for(i in 3..16){
                    for(j in 3..16){
                        for (k in 0..7) {
                            if(myView.return_state(turn-2, h, k, i, j)){
                                flag_break = true
                                rad = k
                                break
                            }
                        }
                        if (flag_break){
                            row = j
                            break
                        }
                    }
                    if (flag_break){
                        line = i
                        break
                    }
                }
            }
            if(flag_break) {
                kind = h
                break
            }
        }
        var check = (line < 20 && line > -1 && row < 20 && row > -1)
        if(usable && check) {
            for (i in 0..6) {
                for (j in 0..6) {
                    sendbox[i][j] = parts[turn - 2][kind].data[i][j]
                    if (sendbox[i][j] == 2) sendbox[i][j] = turn
                }
            }
            for (i in 0..rad) {
                if (rad != 0) {
                    for (j in 0..6) {
                        for (k in 0..6) tmpbox[6 - k][j] = sendbox[j][k]
                    }
                    for (j in 0..6) {
                        for (k in 0..6) sendbox[j][k] = tmpbox[j][k]
                    }
                    if (rad == 4) {
                        reverse = true
                        for (j in 0..6) {
                            for (k in 0..6) tmpbox[j][k] = sendbox[j][6 - k]
                        }
                        for (j in 0..6) {
                            for (k in 0..6) sendbox[j][k] = tmpbox[j][k]
                        }

                    }
                }
            }
            rad = rad % 4 * 90
            presentParts.changekind(kind, sendbox, rad, reverse)
            presentParts.setUsable(true)
            Opened = false
            myView.setblock3(turn, line, row)
            presentParts.setUsable(false)
            if (turn == 2 && play2_playable) this.turn = 3
            else if (turn == 3 && play1_playable) this.turn = 2
            timing = 1
            Handler().postDelayed({ turngo() }, delaytime.toLong())
            partsExpansion()
        }
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
        alertDialog = AlertDialog.Builder(this@CpuActivity)
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
