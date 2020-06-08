package com.example.blocks

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class boardview(context: Context?,attrs: AttributeSet?) :
    View(context, attrs) {
    var paint: Paint
    val arealine = 75.5F
    val corretion = 10F
    val linesize = 20
    val rowsize = 20
    var play1_begin = true
    var play2_begin = true

    //表示エリア14×14,エラー回避のために上下左右に3つずつ追加して20×20
    var box = Array(linesize, {i -> Array(rowsize, {i -> 9})})

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

    fun makerect(){
        box[8][10] = 1
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

            if(line < 3 || line > 16 || row < 3 || row > 16) count += 0
            else {
                var tmp = presentParts.data[i][j]
                var content = box[line][row]
                if (tmp < 2) tmp = 0
                //if (tmp < 2 && content == (num + 2)) tmp = 0 //同じ色の時に重ねられる
                if (content != (num + 2) && tmp > 5) content = 0 //別の色と重複不可箇所は重ねてオーケー
                count += content * tmp
            }
        }
    }
    if(count == 0){
        setblock(touchX, touchY, num)
        return true
    }
    else return false
}

fun setblock(touchX:Int, touchY:Int, num: Int){
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

/*fun check_able_set2(){
    for(i in 0..6){
        for(j in 0..6){
            if(presentParts.data[i][j] == 1){
                select_Cyanblock(i, j)
            }
        }
    }

}

fun select_Cyanblock(line:Int, row:Int){
    var flag_break = false
    var pointing = arrayOf(0,0)
    val differ_line = line - presentParts.startpointLine
    val differ_row = row - presentParts.startpointRow
    for(k in 0..3){
        pointing = select_redblock(k, 0, 0)
        for(i in presentParts.startpointLine..presentParts.endpointLine){
            for(j in 0..6){
                var tmp =  presentParts.data[i][j]
                if(tmp > 6 || tmp < 3) tmp = 0
                val multi = box[touchY + i - presentParts.startpointLine + 3][touchX + j - presentParts.startpointRow + 3] * tmp
                if(multi != 0){
                    flag_break = true
                    break
                }
            }
            if(flag_break) break
        }
    }
}

fun select_redblock(num:Int, touchX:Int, touchY:Int): Array<Int>{
    var arr = arrayOf(0, 0)
    when(num) {
        0 -> arr = arrayOf(touchY - 1, touchX - 1)
        1 -> arr = arrayOf(touchY - 1, touchX + 1)
        2 -> arr = arrayOf(touchY + 1, touchX - 1)
        3 -> arr = arrayOf(touchY + 1, touchX + 1)
    }
    return arr
}*/



}




