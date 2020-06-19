package com.example.blocks

class present_parts() {
    public var data = Array(7, {i -> Array(7, {i -> 0})})
    private var usable = false
    var kinds = 0
    var cyan_count = 0
    var startpointRow = -1
    var startpointLine = -1
    var endpointLine = 0
    var radian = 0
    var reverse = false
    var leftEdge = 7
    var topEdge = 7
    var rightEdge = -1
    var bottomEdge = -1
    var line_block1 = -1
    var row_block1 = -1
    fun changekind(num:Int, array: Array<Array<Int>>, rad:Int, rev:Boolean){
        radian = rad
        reverse = rev
        startpointRow = -1
        startpointLine = -1
        endpointLine = 0
        cyan_count = 0
        leftEdge = 7
        topEdge = 7
        rightEdge = -1
        bottomEdge = -1
        line_block1 = -1
        row_block1 = -1
        this.kinds = num
        for(i in 0..6){
            for(j in 0..6){
                data[i][j] = array[i][j]
                if(array[i][j] == 1) cyan_count += 1
                if(array[i][j] > 0){
                    endpointLine = i
                    leftEdge = Math.min(leftEdge, j)
                    topEdge = Math.min(topEdge, i)
                    rightEdge = Math.max(rightEdge, j)
                    bottomEdge = Math.max(bottomEdge, i)
                }
                if(line_block1 > -1 && row_block1 > -1) continue
                else if(array[i][j] > 1 && array[i][j] < 6){
                    line_block1 = i
                    row_block1 = j
                }
                if(startpointLine > -1 && startpointRow > -1) continue
                else if(array[i][j] > 0){
                    startpointLine = i
                    startpointRow = j
                }
            }
        }
    }
    fun setUsable(bool:Boolean){
        usable = bool
    }
}

public var presentParts = present_parts()