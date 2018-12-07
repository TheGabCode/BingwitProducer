package gab.cdi.bingwitproducer.utils

import android.content.Context
import gab.cdi.bingwitproducer.R

object SkeletonUtil{
    fun getSkeletonCount(context : Context) : Int{
        val px_height = getDeviceHeight(context)
        val skeleton_row_height = context.resources.getDimension(R.dimen.skeleton_layout_height)
        return Math.ceil(px_height/skeleton_row_height.toDouble()).toInt()
    }

    fun getDeviceHeight(context: Context) : Int{
        val resources = context.resources
        val display_metrics = resources.displayMetrics
        return display_metrics.heightPixels
    }
}