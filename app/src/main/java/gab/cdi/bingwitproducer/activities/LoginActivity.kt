package gab.cdi.bingwitproducer.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.fragments.CustomAlertDialogFragment
import gab.cdi.bingwitproducer.fragments.EnterPasswordResetCodeDialogFragment
import gab.cdi.bingwitproducer.fragments.ForgotPasswordDialogFragment
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.utils.DialogUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*
import org.json.JSONObject
import java.nio.charset.Charset

class LoginActivity : AppCompatActivity(), View.OnClickListener, ForgotPasswordDialogFragment.OnFragmentInteractionListener, EnterPasswordResetCodeDialogFragment.OnFragmentInteractionListener, CustomAlertDialogFragment.OnFragmentInteractionListener{

    private lateinit var mSession : Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSession = Session(this@LoginActivity)
        if(mSession.isUserLoggedIn() == true){
            checkUserStatus(mSession.id()!!)

        }
        setContentView(R.layout.activity_login)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when(id){
            R.id.sign_up_option -> {
                val intent = Intent(this, RegistrationActivity::class.java)
                startActivity(intent)
            }
            R.id.btnSignIn -> {
               preSignIn()
            }

            R.id.forgot_password_option -> {
                val forgot_password_dialog = ForgotPasswordDialogFragment()
                forgot_password_dialog.show(supportFragmentManager,"forgot_password")
            }
        }
    }


    private fun preSignIn(){
        if(TextUtils.isEmpty(login_username.text.toString().trim())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in username")
            return
        }

        if(TextUtils.isEmpty(login_password.text.toString().trim())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in password")
            return
        }
        val message : String? = null
        val params : HashMap<String,String> = HashMap()
        val header : HashMap<String,String> = HashMap()
        header.put("Content-Type","application/x-www-form-urlencoded")
        params.put("username",login_username.text.toString().trim())
        ApiRequest.post(this, API.CHECK_USER,header,params, message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        val json = JSONObject(response)
                        Log.d("Login",response)
                        if(json.getString("type") == "producer"){
                            signIn()
                        }
                        else{
                            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Invalid user")
                        }
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        //Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
                        DialogUtil.showVolleyErrorDialog(supportFragmentManager,error)
                    }
                })
    }

    private fun signIn(){
        if(TextUtils.isEmpty(login_username.text.toString().trim())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in username")
            return
        }

        if(TextUtils.isEmpty(login_password.text.toString().trim())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in password")
            return
        }

        val params : HashMap<String,String> = HashMap()
        params.put("username",login_username.text.toString().trim())
        params.put("password",login_password.text.toString().trim())
        val headers : HashMap<String,String> = HashMap()
        headers.put("Content-Type","application/x-www-form-urlencoded")
        val message = "Signing in..."
        ApiRequest.post(this, API.SIGN_IN,params, message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        val json = JSONObject(response)
                        mSession.authorize(response)
                        if(json["success"] == true){
                            checkUserStatus(mSession.id()!!)
                        }
                    }

                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        DialogUtil.showVolleyErrorDialog(supportFragmentManager,error)
                    }
                })
    }
    override fun onFragmentInteraction(uri: Uri) {

    }

    fun checkUserStatus(id : String) {
        val header : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"
        header.put("Authorization",authorization)
        val params : HashMap<String,String> = HashMap()
        ApiRequest.get(this@LoginActivity,"${API.GET_USER}/$id",header,params,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Get user",response)
                        val json = JSONObject(response)
                        val user = json.getJSONObject("user")
                        if(user["status"] == "inactive" && user["type"] == "producer"){
                            val intent = Intent(this@LoginActivity, RegistrationVerificationActivity::class.java)
                            startActivity(intent)
                        }
                        else if(user["status"] == "active" && user["type"] == "producer"){
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else{
                            mSession.deauthorize()
                            Toast.makeText(this@LoginActivity,"Invalid username and password",Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        //Toast.makeText(this@LoginActivity,"Error", Toast.LENGTH_SHORT).show()
                        DialogUtil.showVolleyErrorDialog(supportFragmentManager,error)
                        Log.d("Error ", error.toString())
                    }
                })
    }
}
