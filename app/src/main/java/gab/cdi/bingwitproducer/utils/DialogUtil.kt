package gab.cdi.bingwitproducer.utils

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import com.android.volley.VolleyError
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.extensions.showVolleyErrorDialog
import gab.cdi.bingwitproducer.fragments.CustomAlertDialogFragment
import org.json.JSONObject
import java.nio.charset.Charset

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


    fun showVolleyErrorDialog(support_fragment_manager : FragmentManager, error : VolleyError?){
        if(error != null && error.networkResponse != null && error.networkResponse.data != null){
            Log.d("Huh","here")
            var body = String(error.networkResponse.data, Charset.forName("UTF-8"))
            var dialog : CustomAlertDialogFragment? = null
            Log.d("Error",body)
            try{
                var error_message = JSONObject(body).getJSONObject("error").getString("message").capitalize()
                dialog = CustomAlertDialogFragment.newInstance(error_message!!,1500)
            }catch (e : NoSuchElementException){
                var error_message = JSONObject(body).getJSONObject("error").getJSONObject("error").getString("code").toString().capitalize()
                dialog = CustomAlertDialogFragment.newInstance(error_message!!,1500)

            }catch (e: Exception){
                dialog = CustomAlertDialogFragment.newInstance("Server Error",1500)
            }
            dialog?.show(support_fragment_manager,"error_alert_dialog")
        }
        else{
            CustomAlertDialogFragment.newInstance("No response from server, try again",2000).show(
                    support_fragment_manager,"error_alert_dialog"
            )
        }

    }

    fun showErrorDialog(support_fragment_manager: FragmentManager, duration : Long, message : String){
        val dialog = CustomAlertDialogFragment.newInstance(message,duration)
        dialog.show(support_fragment_manager,"error_alert_dialog")
    }


}