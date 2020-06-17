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
    fun changekind(num:Int, array: Array<Array<Int>>, rad:Int, rev:Boolean){
        radian = rad
        reverse = rev
        startpointRow = -1
        startpointLine = -1
        endpointLine = 0
        cyan_count = 0
        this.kinds = num
        for(i in 0..6){
            for(j in 0..6){
                data[i][j] = array[i][j]
                if(array[i][j] == 1) cyan_count += 1
                if(array[i][j] > 0){
                    endpointLine = i
                }
                if(startpointLine > -1 && startpointRow > -1) continue
                else if(array[i][j] > 0){
                    startpointLine = i
                    startpointRow = j
                }
            }
        }
    }
    fun getUsable() :Boolean {
        return usable
    }
    fun setUsable(bool:Boolean){
        usable = bool
    }
    fun state_unusable(num: Int){
        parts[num][kinds].setUsable(false)
    }
}

public var presentParts = present_parts()