package com.example.blocks

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.parts_detail.*


class partsfragment: Fragment()/*,View.OnTouchListener*/ {
    public fun newInstance(str:String):partsfragment{
        var fragment:partsfragment = partsfragment()
        var barg:Bundle = Bundle()
        barg.putString("Message",str)
        fragment.setArguments(barg)

        return fragment
    }

    override public fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.parts_detail, container, false)
    }

    override public fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = getArguments()
        var num = 2
        if(args != null){
            var tmp = args.getString("Message")
            if(tmp != null)num = Integer.parseInt(tmp)
        }
        textView2.setTextColor(Color.WHITE)
        textView2.setText(""+ presentParts.cyan_count)
        button2.setOnClickListener {
            view2.makeblock(num)
        }

        switchLR.setOnClickListener {
            view2.LRinverter()
        }
        switchTB.setOnClickListener {
            view2.TBinverter()
        }

        spinL.setOnClickListener {
            view2.spinLeft()
        }
        spinR.setOnClickListener {
            view2.spinRight()
        }

        view2.makeblock(num)

    }

    /*override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val tmp = getFragmentManager()
        if(tmp != null) tmp.beginTransaction().remove(this).commit()
        return true
    }*/

}