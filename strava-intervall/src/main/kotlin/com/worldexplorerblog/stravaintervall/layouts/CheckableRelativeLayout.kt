package com.worldexplorerblog.stravaintervall.layouts

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.RelativeLayout

class CheckableRelativeLayout : RelativeLayout, Checkable {

    private var checkedLayout = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)

        if (checkedLayout) {
            mergeDrawableStates(drawableState, intArrayOf(android.R.attr.state_checked))
        }

        return drawableState
    }

    override fun toggle() {
        checkedLayout = !checkedLayout
    }

    override fun isChecked(): Boolean {
        return checkedLayout
    }

    override fun setChecked(checked: Boolean) {
        if (checkedLayout != checked) {
            checkedLayout = checked
            refreshDrawableState()
        }
    }
}