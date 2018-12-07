package gab.cdi.bingwitproducer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.telephony.TelephonyManager
import android.text.Editable
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
import gab.cdi.bingwitproducer.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity(), View.OnClickListener, ForgotPasswordDialogFragment.OnFragmentInteractionListener, EnterPasswordResetCodeDialogFragment.OnFragmentInteractionListener, CustomAlertDialogFragment.OnFragmentInteractionListener{

    private lateinit var mSession : Session
    val PERMISSION_READ_PHONE_STATE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSession = Session(this@LoginActivity)
        if(mSession.isUserLoggedIn() == true){
            checkUserStatus(mSession.id()!!)

        }
        setContentView(R.layout.activity_login)
        setupPhonePermission()



        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    fun setupPhonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSION_READ_PHONE_STATE)
            }
            else {
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSION_READ_PHONE_STATE)
            }
        } else {
            setPhoneNumber()
        }
    }

    @SuppressLint("MissingPermission")
    fun setPhoneNumber(){
        val telephone_manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        login_username.setText(telephone_manager.line1Number)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_READ_PHONE_STATE -> {
                if (( grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    ToastUtil.bingwitDisplayCustomToastNotification(this,"Permission Denied")
                    setPhoneNumber()
                }
            }

        }
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when(id){
            R.id.sign_up_option -> {
                val intent = Intent(this, RegistrationActivity::class.java)
                startActivity(intent)
            }
            R.id.btnSignIn -> {
                disableButtons()
                signIn()
            }

            R.id.forgot_password_option -> {
                val forgot_password_dialog = ForgotPasswordDialogFragment()
                forgot_password_dialog.show(supportFragmentManager,"forgot_password")
            }
        }
    }

    fun enableButtons() {
        btnSignIn.isEnabled = true
        btnSignIn.isClickable = true
    }

    fun disableButtons() {
        btnSignIn.isEnabled = false
        btnSignIn.isClickable = false
    }


    private fun signIn(){
        if(TextUtils.isEmpty(login_username.text.toString().trim())){
            enableButtons()
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in username")
            return
        }

        if(TextUtils.isEmpty(login_password.text.toString().trim())){
            enableButtons()
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in password")
            return
        }

        val params : HashMap<String,String> = HashMap()
        params.put("username",login_username.text.toString().trim())
        params.put("password",login_password.text.toString().trim())
        params.put("type","producer")
        val headers : HashMap<String,String> = HashMap()
        headers.put("Content-Type","application/x-www-form-urlencoded")
        val message = "Signing in..."
        ApiRequest.post(this, API.SIGN_IN,params, message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Login",response)
                        val json = JSONObject(response)
                        mSession.authorize(response)
                        if(json["success"] == true){
                            checkUserStatus(mSession.id()!!)
                        }
                    }

                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        enableButtons()
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
                            enableButtons()
                        }
                        else if(user["status"] == "active" && user["type"] == "producer"){
                            btnSignIn.isEnabled = true
                            btnSignIn.isClickable = true
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else{
                            mSession.deauthorize()
                            enableButtons()
                            Toast.makeText(this@LoginActivity,"Invalid username and password",Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        //Toast.makeText(this@LoginActivity,"Error", Toast.LENGTH_SHORT).show()
                        enableButtons()
                        DialogUtil.showVolleyErrorDialog(supportFragmentManager,error)
                        Log.d("Error ", error.toString())
                    }
                })
    }
}
