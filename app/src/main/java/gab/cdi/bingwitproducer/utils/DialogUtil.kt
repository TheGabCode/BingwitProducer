package gab.cdi.bingwitproducer.utils

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import gab.cdi.bingwitproducer.R

/**
 * Created by Default on 10/10/2018.
 */
object DialogUtil {

    fun showForgotPassWord(context: Context){
        val alert = AlertDialog.Builder(context)
        val aView = LayoutInflater.from(context).inflate(R.layout.fragment_forgot_password_dialog, null)
        alert.setView(aView)

        val forgot_password_cancel_button : Button = aView.findViewById(R.id.forgot_password_cancel_button)
        val forgot_password_submit_button : Button = aView.findViewById(R.id.forgot_password_submit_button)

        alert.show()
    }
}