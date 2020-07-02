package com.example.blocks

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class boardview(context: Context?,attrs: AttributeSet?) :
    View(context, attrs) {
    var paint: Paint
    val arealine = 75.5F
    val corretion = 0F
    val linesize = 20
    val rowsize = 20
    val barcorretion = 65
    var touchX = 0
    var touchY = 0
    var play1_begin = true
    var play2_begin = true

    //表示エリア14×14,エラー回避のために上下左右に3つずつ追加して20×20
    var box = Array(linesize, {i -> Array(rowsize, {i -> 9})})
    //0:正常0, 1:正常90, 2:正常180, 3:正常270, 4:反転0, 5:反転90, 6:反転180, 7:反転270
    var box_state =
        Array(2, {i -> Array(21, {i ->Array(8, {i ->Array(linesize, {i -> Array(rowsize, {i -> false})})})})})
    var box_block = present_parts()
    var tmpbox = Array(7, {i -> Array(7, {i -> 0})})

    init {
        paint = Paint()
        for(i in 3..16){
            for(j in 3..16){
                box[i][j] = 0
                for(k in 0..1){
                    for(l in 0..20){
                        for(m in 0..7) box_state[k][l][m][i][j] = true
                    }
                }
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        for(i in 0..13){
            for(j in 0..13){
                colorselect(paint, box[i + 3][j + 3])
                paint.strokeWidth = 2f
                paint.style = Paint.Style.FILL_AND_STROKE
                canvas.drawRect(
                    arealine * j.toFloat() + corretion,
                    arealine * i.toFloat() + corretion,
                    arealine * (j + 1).toFloat() + corretion,
                    arealine * (i + 1).toFloat() + corretion,
                    paint
                )
                paint.setColor(Color.BLACK)
                paint.strokeWidth = 2f
                paint.style = Paint.Style.STROKE
                canvas.drawRect(
                    arealine * j.toFloat() + corretion,
                    arealine * i.toFloat() + corretion,
                    arealine * (j + 1).toFloat() + corretion,
                    arealine * (i + 1).toFloat() + corretion,
                    paint
                )
            }
        }
    }


    fun colorselect(paint:Paint, num:Int){
        when {
            num == 0 -> paint.color = Color.GRAY
            num == 1 -> paint.color = Color.CYAN
            num == 2 -> paint.color = Color.RED
            num == 3 -> paint.color = Color.BLUE
            else -> paint.color = Color.BLACK
        }
    }

    fun set_state(turn: Int):Boolean{
        var flag_able = false
        var rev = false
        var rad = 0
        for(h in 0..20) {
            if(parts[turn - 2][h].getUsable()){
                rev = false
                rad = 0
                for(i in 0..6){
                    for(j in 0..6){
                        box_block.data[i][j] = parts[turn - 2][h].data[i][j]
                    }
                }
                for (i in 0..7) {
                    if(i != 0) {
                        for (j in 0..6) {
                            for (k in 0..6) tmpbox[6 - k][j] = box_block.data[j][k]
                        }
                        for (j in 0..6) {
                            for (k in 0..6) box_block.data[j][k] = tmpbox[j][k]
                        }
                        if(i == 4){
                            rev = true
                            for (j in 0..6) {
                                for (k in 0..6) tmpbox[j][k] = box_block.data[j][6 - k]
                            }
                            for (j in 0..6) {
                                for (k in 0..6) box_block.data[j][k] = tmpbox[j][k]
                            }

                        }
                        rad += 90
                        if(rad >= 360) rad -= 360
                    }
                    box_block.changekind(h, box_block.data, rad, rev)
                    for(j in 3..16){
                        for(k in 3..16){
                            if(box[j][k] == 0)box_state[turn - 2][h][i][j][k] = check_dup_cyan3(turn, j, k)
                            else box_state[turn - 2][h][i][j][k] = false
                            if(box_state[turn - 2][h][i][j][k]) flag_able = true
                        }
                    }
                }
            } else {
                for(i in 0..7) {
                    for (j in 3..16) {
                        for (k in 3..16) {
                            box_state[turn - 2][h][i][j][k] = false
                        }
                    }
                }
            }
        }
        return flag_able
    }

    fun check_dup_cyan3(turn: Int, line: Int, row: Int):Boolean{
        var non_cyan_count = 0
        var flag_able = false
        var flag_break = false
        val differ_line = line - (box_block.line_block1)//ok
        val differ_row = row - (box_block.row_block1)//ok
        for (i in box_block.topEdge..box_block.bottomEdge) {
            for (j in box_block.leftEdge..box_block.rightEdge) {
                val now_line = differ_line + i
                val now_row = differ_row + j
                val tmp = box_block.data[i][j]
                if (now_row > 19 || now_line > 19 || now_row < 0 || now_line < 0) {
                    if (tmp < 6 && tmp > 1){
                        flag_break = true
                        break
                    }
                    else if (tmp == 1) non_cyan_count++
                } else {
                    //if (tmp == 1 && box[now_line][now_row] != turn) non_cyan_count++
                    if(tmp == 1) non_cyan_count += dup_cyan_color2(now_line, now_row, turn)
                }
            }
            if (flag_break) break
        }
        if(play1_begin || play2_begin) flag_able = check_able_set3(turn, differ_line, differ_row, line, row)
        else {
            if (non_cyan_count >= box_block.cyan_count) flag_able = false
            else if(flag_break) flag_able = false
            else flag_able = check_able_set3(turn, differ_line, differ_row, line, row)
        }
        return flag_able
    }

    fun check_able_set3(turn: Int, Dline: Int, Drow:Int, line: Int, row: Int):Boolean{
        var count = 0
        for (i in box_block.topEdge..box_block.bottomEdge) {
            for (j in box_block.leftEdge..box_block.rightEdge) {
                val now_line = Dline + i
                val now_row = Drow + j
                var tmp = box_block.data[i][j]
                if (now_line < 3 || now_line > 16 || now_row < 3 || now_row > 16) {
                    if (tmp > 1 && tmp < 6) count++
                } else {
                    var content = box[now_line][now_row]
                    if (tmp < 2) tmp = 0
                    if (content != turn && tmp > 5) content = 0 //別の色と重複不可箇所は重ねてオーケー
                    count += content * tmp
                }
            }
        }
        if(count == 0) {
            return true
        }
        else return false
    }

    fun dup_cyan_color2(line:Int, row:Int, num: Int):Int{
        if(box[line][row] == num) return 0
        else return 1
    }

    fun recheck_able_set(pointX:Float, pointY:Float, num: Int):Boolean{
        var flag = false
        touchX = ((pointX - corretion) / arealine).toInt() + 3
        touchY = ((pointY - corretion - barcorretion) / arealine).toInt() + 3
        if(touchX < 20 || touchY < 20) {
            var index_2 = presentParts.radian / 90
            if (presentParts.reverse) index_2 += 4
            if (box_state[num][presentParts.kinds][index_2][touchY][touchX]) flag = true
            //Log.v("debuglog", "kinds"+ presentParts.kinds+" direct"+index_2+" line"+touchY + " row"+touchX)
            //Log.v("debug", "kinds:"+ presentParts.kinds+ " line"+(touchY - presentParts.line_block1)
            //       + " row"+(touchX - presentParts.row_block1))
        }
        return flag
    }

    fun setblock3(num: Int){
        val differ_line = touchY - presentParts.line_block1
        val differ_row = touchX - presentParts.row_block1
        for (i in presentParts.topEdge..presentParts.bottomEdge) {
            for (j in presentParts.leftEdge..presentParts.rightEdge) {
                val now_line = differ_line + i
                val now_row = differ_row + j
                var tmp = box[now_line][now_row]
                if (presentParts.data[i][j] < 2 || presentParts.data[i][j] > 5)
                    box[now_line][now_row] = Math.max(tmp, 0)

                else box[now_line][now_row] = Math.max(tmp, presentParts.data[i][j])
            }
        }

        if(num == 2 && play1_begin)play1_begin = false
        else if(num == 3 && play2_begin) play2_begin = false
        parts[num - 2][presentParts.kinds].setUsable(false)
        invalidate()
    }

    fun count_block(num:Int):Int{
        var count = 0
        for(i in 3..16){
            for(j in 3..16){
                if(box[i][j] == num) count++
            }
        }
        return count
    }

    fun restart(){

        for(i in 0..1){
            for(j in 0..20){
                parts[i][j].setUsable(true)
                for(k in 0..7){
                    for(l in 3..16){
                        for(m in 3..16){
                            box[l][m] = 0
                            box_state[i][j][k][l][m] = true
                        }
                    }
                }
            }
        }
        play1_begin = true
        play2_begin = true
        invalidate()
    }

}




