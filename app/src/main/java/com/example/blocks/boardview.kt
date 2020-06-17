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
    var turn = 2

    //表示エリア14×14,エラー回避のために上下左右に3つずつ追加して20×20
    var box = Array(linesize, {i -> Array(rowsize, {i -> 9})})
    //0:正常0, 1:正常90, 2:正常180, 3:正常270, 4:反転0, 5:反転90, 6:反転180, 7:反転270
    var box_state =
        Array(2, {i -> Array(21, {i ->Array(8, {i ->Array(linesize, {i -> Array(rowsize, {i -> true})})})})})
    var box_block = present_parts()
    var tmpbox = Array(7, {i -> Array(7, {i -> 0})})
    init {
        paint = Paint()
        for(i in 3..16){
            for(j in 3..16){
                box[i][j] = 0
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        for(i in 0..13){
            for(j in 0..13){
                set_state(turn, i + 3, j + 3)
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

                var tmp = 0
                if(box_state[turn - 2][3][0][i+3][j+3]) tmp = 1
                paint.textSize = 20F
                canvas.drawText(""+tmp,
                    arealine * j.toFloat() + corretion,
                    arealine * i.toFloat() + corretion,paint)
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

    fun set_state(turn: Int, line: Int, row: Int){
        var rev = false
        var rad = 0
        for(h in 0..20) {
            for(i in 0..7)
                box_state[turn - 2][h][i][line][row] = false
            if(box[line][row] == 0 && parts[turn - 2][h].getUsable()){
                for(i in 0..6){
                    for(j in 0..6){
                        box_block.data[i][j] = parts[turn - 2][h].data[i][j]
                    }
                }
                for (i in 0..7) {
                    for(j in 0..6) {
                        for (k in 0..6) {
                            if(i == 4) tmpbox[j][k] = box_block.data[j][6 - k]
                            else tmpbox[6 - k][j] = box_block.data[j][k]
                        }
                    }
                    for(j in 0..6) {
                        for (k in 0..6) {
                            box_block.data[j][k] = tmpbox[j][k]
                        }
                    }
                    rad += 90
                    if(rad >= 360) rad -= 360
                    if(i > 3) rev = true
                    box_block.changekind(h, box_block.data, rad, rev)
                    box_state[turn - 2][h][i][line][row] = check_dup_cyan2(turn, line, row)//check_dup_cyan2(turn, line, row)
                    if(!play1_begin && !play2_begin) {
                        if(box[line][row] < 2) box[line][row] = 0
                            if (box_state[turn - 2][7][2][line][row]) {
                                box[line][row] = 1
                                Log.v("tag", "line:" + line + "row:" + row + "T/F:T")
                            }
                    }
                }
            }
        }
    }

    fun check_dup_cyan2(turn: Int, line: Int, row: Int):Boolean{
        var non_cyan_count = 0
        var flag_able = false
        val differ_line = line - (box_block.startpointLine + 1)
        val differ_row = row - (box_block.startpointRow + 1)
        var now_line = differ_line + box_block.startpointLine
        if(differ_line >= 0 && differ_row >= 0) {
            for (i in box_block.startpointLine..box_block.endpointLine) {
                for (j in 0..6) {
                        val now_row = differ_row + j
                        val tmp = box_block.data[i][j]
                        if (now_row > 19 || now_line > 19) {
                            break
                        }
                        if (tmp == 1) {
                            non_cyan_count +=
                                dup_cyan_color2(now_line, now_row, turn)
                        }
                }
                if (now_line > 19) {
                    break
                }
                now_line++
            }
            if (non_cyan_count == box_block.cyan_count) {
                if (play1_begin || play2_begin) {
                    flag_able = check_able_set2(turn, differ_line, differ_row)
                } else {
                    flag_able = false
                }
            } else flag_able = check_able_set2(turn, differ_line, differ_row)
        }
        return flag_able
    }

    fun check_able_set2(turn: Int, Dline: Int, Drow:Int):Boolean{
        var count = 0
        var now_line = Dline + box_block.startpointLine
        for(i in box_block.startpointLine..box_block.endpointLine) {
            for(j in 0..6){
                val now_row = Drow + j
                var tmp = box_block.data[i][j]
                if(now_line < 3 || now_line > 16 || now_row < 3 || now_row > 16){
                    if(tmp > 5 || tmp < 2)  count += 0
                    else count += 1
                }
                else {
                    var content = box[now_line][now_row]
                    if(content == 1) content = 0
                    if (tmp < 2) tmp = 0
                    //if (tmp < 2 && content == turn) tmp = 0 //同じ色の時に重ねられる
                    if (content != (turn) && tmp > 5) content = 0 //別の色と重複不可箇所は重ねてオーケー
                    count += content * tmp
                }
            }
            now_line++
        }
        if(count == 0) {
            //if(!play1_begin && !play2_begin)box[line][row] = 1
            return true
        }
        else return false
    }

    fun check_able_set3(turn: Int, line: Int, row:Int):Boolean{
        var count = 0
        var non_cyan_count = 0
        var flag_able = false
        val differ_line = line - (box_block.startpointLine + 1)
        val differ_row = row - (box_block.startpointRow + 1)
        var now_line = differ_line + box_block.startpointLine
        if(differ_line >= 0 && differ_row >= 0) {
            for (i in box_block.startpointLine..box_block.endpointLine) {
                for (j in 0..6) {
                    val now_row = differ_row + j
                    var tmp = box_block.data[i][j]
                    if (now_line < 3 || now_line > 16 || now_row < 3 || now_row > 16) {
                        if (tmp > 5 || tmp < 2) count += 0
                        else count += 1
                    } else {
                        var content = box[now_line][now_row]
                        if (tmp == 1) {
                            if(content != turn) non_cyan_count ++
                        }
                        if (content == 1) content = 0
                        if (tmp < 2) tmp = 0
                        //if (tmp < 2 && content == turn) tmp = 0 //同じ色の時に重ねられる
                        if (content != (turn) && tmp > 5) content = 0 //別の色と重複不可箇所は重ねてオーケー
                        count += content * tmp
                    }
                }
                now_line++
            }
            if (count == 0) {
                if (non_cyan_count == box_block.cyan_count) {
                    if (play1_begin || play2_begin) {
                        flag_able = true
                    } else {
                        flag_able = false
                    }
                }
            } else flag_able = false
        }
        return flag_able
    }

    fun dup_cyan_color2(line:Int, row:Int, num: Int):Int{
        if(box[line][row] == num) return 0
        else return 1
    }

    fun recheck_able_set(pointX:Float, pointY:Float, num: Int):Boolean{
        var flag = false
        touchX = ((pointX - corretion) / arealine).toInt() + 3
        touchY = ((pointY - corretion - barcorretion) / arealine).toInt() + 3
        var index_2 = presentParts.radian / 90
        if(presentParts.reverse) index_2 += 4
            if(box_state[num - 2][presentParts.kinds][index_2][touchY][touchX]) flag = true
            Log.v("debuglog", "kinds"+ presentParts.kinds+" direct"+index_2+" line"+touchY + " row"+touchX)
        return flag
    }

    fun setblock2(num: Int){
        touchX--
        touchY--
        for (i in presentParts.startpointLine..presentParts.endpointLine) {
            for (j in 0..6) {
                var tmp =
                    box[touchY + i - presentParts.startpointLine][touchX + j - presentParts.startpointRow]
                if (presentParts.data[i][j] < 2 || presentParts.data[i][j] > 5)
                    box[touchY + i - presentParts.startpointLine][touchX + j - presentParts.startpointRow] =
                        Math.max(tmp, 0)
                else box[touchY + i - presentParts.startpointLine][touchX + j - presentParts.startpointRow] =
                    Math.max(tmp, presentParts.data[i][j])
            }
        }
        if (num == 2){
            turn = 3
            play1_begin = false
        } else if (num == 3) {
            turn = 2
            play2_begin = false
        }
        presentParts.state_unusable(num - 2)
        invalidate()
    }

fun check_dup_cyan(pointX:Float, pointY:Float, num:Int):Boolean{
    var non_cyan_count = 0
    var flag_able = false
    val barcorretion = 65
    val touchX = ((pointX - corretion) / arealine).toInt() - 1
    val touchY = ((pointY - corretion - barcorretion) / arealine).toInt() - 1
    for(i in presentParts.startpointLine..presentParts.endpointLine) {
        for (j in 0..6) {
            var tmp = presentParts.data[i][j]
            if (tmp == 1) {
                var line = touchY + i - presentParts.startpointLine + 3
                var row = touchX + j - presentParts.startpointRow + 3
                non_cyan_count +=
                    dup_cyan_color(line, row, num)
            }
        }
    }
    if (non_cyan_count == presentParts.cyan_count) {
        if (play1_begin || play2_begin) {
            flag_able = check_able_set(touchX, touchY, num)
        } else {
            flag_able = false
        }
    } else flag_able = check_able_set(touchX, touchY, num)
    if(!presentParts.getUsable()) flag_able = false
    return flag_able
}


fun check_able_set(touchX:Int, touchY:Int, num: Int):Boolean{
    var count = 0
    for(i in presentParts.startpointLine..presentParts.endpointLine){
        for(j in 0..6){
            var line = touchY + i - presentParts.startpointLine + 3
            var row = touchX + j - presentParts.startpointRow + 3

            var tmp = presentParts.data[i][j]
            if(line < 3 || line > 16 || row < 3 || row > 16){
                if(tmp > 5 || tmp < 2)  count += 0
                else count += 1
            }
            else {
                var content = box[line][row]
                if (tmp < 2) tmp = 0
                //if (tmp < 2 && content == (num + 2)) tmp = 0 //同じ色の時に重ねられる
                if (content != (num + 2) && tmp > 5) content = 0 //別の色と重複不可箇所は重ねてオーケー
                count += content * tmp
            }
        }
    }
    if(count == 0) return true
    else return false
}

fun setblock(pointX:Float, pointY:Float, num: Int){
    val barcorretion = 65
    val touchX = ((pointX - corretion) / arealine).toInt() - 1
    val touchY = ((pointY - corretion - barcorretion) / arealine).toInt() - 1
    for(i in presentParts.startpointLine..presentParts.endpointLine){
        for(j in 0..6){
            var tmp =  box[touchY + i - presentParts.startpointLine + 3][touchX + j - presentParts.startpointRow + 3]
            if(presentParts.data[i][j] < 2 || presentParts.data[i][j] > 5)
                box[touchY + i - presentParts.startpointLine + 3][touchX + j - presentParts.startpointRow + 3] = Math.max(tmp, 0)
            else box[touchY + i - presentParts.startpointLine + 3][touchX + j - presentParts.startpointRow + 3] = Math.max(tmp ,presentParts.data[i][j])
        }

    }
    if((num + 2) == 2)play1_begin = false
    else if((num + 2) == 3)play2_begin = false
    if (num == 2){
        turn = 3
        play1_begin = false
    } else if (num == 3) {
        turn = 2
        play2_begin = false
    }
    presentParts.state_unusable(num)
    invalidate()
}

    fun dup_cyan_color(line:Int, row:Int, num: Int):Int{
        if(box[line][row] == (num + 2)) return 0
        else return 1
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

}




