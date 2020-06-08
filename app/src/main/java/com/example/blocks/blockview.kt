package com.example.blocks

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class blockview(context:Context?, attrs:AttributeSet?) : View(context,attrs){
    var num = 0
    var turn = 2
    var paint:Paint
    var partsbox = Array(7, {i -> Array(7, {i -> 0})})
    var tmpbox = Array(7, {i -> Array(7, {i -> 0})})
    init {
        paint = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val arealine = 70
        val corretion = 10F
        val topbottom_corretion = 50
        val leftright_corretion = 250
        canvas.drawColor(Color.BLUE)
        for(i in 0..6){
            for(j in 0..6){
                colorselect(paint, partsbox[i][j])
                paint.strokeWidth = 2f
                paint.style = Paint.Style.FILL_AND_STROKE

                canvas.drawRect(
                    arealine * j.toFloat() + corretion + leftright_corretion,
                    arealine * i.toFloat() + corretion + topbottom_corretion,
                    arealine * (j + 1).toFloat() + corretion + leftright_corretion,
                    arealine * (i + 1).toFloat() + corretion + topbottom_corretion,
                    paint
                )
                paint.setColor(Color.BLACK)
                paint.strokeWidth = 2f
                paint.style = Paint.Style.STROKE
                canvas.drawRect(
                    arealine * j.toFloat() + corretion + leftright_corretion,
                    arealine * i.toFloat() + corretion + topbottom_corretion,
                    arealine * (j + 1).toFloat() + corretion + leftright_corretion,
                    arealine * (i + 1).toFloat() + corretion + topbottom_corretion,
                    paint
                )
            }
        }
        if(parts[turn - 2][num].getUsable()){
            presentParts.changekind(num, partsbox)
            presentParts.setUsable(true)
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
    fun makeblock(Colors: Int){
        turn = Colors
        for(i in 0..6) {
            for (j in 0..6) {
                partsbox[i][j] = change_color(parts[turn - 2][num].data[i][j], turn)
            }
        }
        num++
        if(num > 20) num = 0
        invalidate()
    }

    fun change_color(num:Int, Colors:Int):Int{
        if(num == 2) return Colors
        else if(num == 6) return num + Colors - 2
        return num
    }

    fun LRinverter(){
        for(i in 0..6) {
            for (j in 0..6) {
                tmpbox[i][j] = partsbox[i][6 - j]
            }
        }
        for(i in 0..6) {
            for (j in 0..6) {
                partsbox[i][j] = tmpbox[i][j]
            }
        }
        invalidate()
    }

    fun TBinverter(){
        for(i in 0..6) {
            for (j in 0..6) {
                tmpbox[i][j] = partsbox[6 - i][j]
            }
        }
        for(i in 0..6) {
            for (j in 0..6) {
                partsbox[i][j] = tmpbox[i][j]
            }
        }
        invalidate()
    }

    fun spinLeft(){
        for(i in 0..6) {
            for (j in 0..6) {
                tmpbox[6 - j][i] = partsbox[i][j]
            }
        }
        for(i in 0..6) {
            for (j in 0..6) {
                partsbox[i][j] = tmpbox[i][j]
            }
        }
        invalidate()
    }

    fun spinRight(){
        for(i in 0..6) {
            for (j in 0..6) {
                tmpbox[j][6 - i] = partsbox[i][j]
            }
        }
        for(i in 0..6) {
            for (j in 0..6) {
                partsbox[i][j] = tmpbox[i][j]
            }
        }
        invalidate()
    }
}