package gab.cdi.bingwitproducer.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity

abstract class BaseFragment() : Fragment(){
    lateinit var mActivity : MainActivity

    abstract fun setTitle(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = context as MainActivity
        mActivity.setToolbar(true)
        // mActivity.toolbar.title = setTitle()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (mActivity.fm!!.backStackEntryCount > 0){
            mActivity.setToolbar(true)
        }else{
            mActivity.setToolbar(false)
            mActivity.title = context?.getString(R.string.app_name)
        }
    }
}