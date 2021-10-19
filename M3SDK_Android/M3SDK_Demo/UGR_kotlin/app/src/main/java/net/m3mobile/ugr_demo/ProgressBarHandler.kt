package net.m3mobile.ugr_demo

import android.R
import android.view.ViewGroup
import android.app.Activity
import android.content.Context
import android.widget.RelativeLayout
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar

/**
 * Created by M3 on 2017-12-14.
 */
class ProgressBarHandler(context: Context) {
    private val mProgressBar: ProgressBar
    fun show() {
        mProgressBar.visibility = View.VISIBLE
    }

    fun hide() {
        mProgressBar.visibility = View.INVISIBLE
    }

    init {
        val layout = (context as Activity).findViewById<View>(R.id.content).rootView as ViewGroup
        mProgressBar = ProgressBar(context, null, R.attr.progressBarStyleLarge)
        mProgressBar.isIndeterminate = true
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        val rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        rl.addView(mProgressBar)
        layout.addView(rl, params)
        hide()
    }
}