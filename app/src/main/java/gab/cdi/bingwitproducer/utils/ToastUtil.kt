package gab.cdi.bingwitproducer.utils

import android.content.Context
import android.os.CountDownTimer
import android.view.ContextMenu
import android.view.Gravity
import android.view.View
import android.widget.Toast
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.R.id.bingwit_navigation_activity
import gab.cdi.bingwitproducer.R.id.notif_bar
import gab.cdi.bingwitproducer.activities.MainActivity
import kotlinx.android.synthetic.main.bingwit_custom_toast_notification.view.*
import kotlinx.android.synthetic.main.fragment_view_products_tab.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_view_products.*


object ToastUtil{
    fun bingwitDisplayCustomToastNotification(context : Context, message :String){
        val mActivity = context as MainActivity
        val toast_layout = mActivity.layoutInflater.inflate(R.layout.bingwit_custom_toast_notification,null,false)
        toast_layout.toast_message.text = message
        val toast = Toast(mActivity.applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = toast_layout
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL,0,0)

        toast.show()
    }

    fun notReallyAToast(context: Context, message: String){
        val mActivty = context as MainActivity
        mActivty.notif_bar.visibility = View.VISIBLE
        mActivty.notif_bar.text = message
        val notif_timer = object : CountDownTimer(2000,1000){
            override fun onFinish() {
                mActivty.notif_bar.visibility = View.INVISIBLE
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }

        notif_timer.start()


    }
}