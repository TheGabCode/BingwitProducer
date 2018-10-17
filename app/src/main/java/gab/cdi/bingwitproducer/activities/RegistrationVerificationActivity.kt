package gab.cdi.bingwitproducer.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import gab.cdi.bingwitproducer.R
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import org.json.JSONObject


class RegistrationVerificationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var registration_verification_code_input_text : EditText
    private lateinit var mSession : Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_verification)
        mSession = Session(this@RegistrationVerificationActivity)
        initUI()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun initUI(){
        registration_verification_code_input_text = findViewById(R.id.registration_verification_code_input_text)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onClick(v: View?) {
        val id = v?.id
        when(id){
            R.id.finalize_registration_button -> {
                verify()
            }
            R.id.resend_verification_code_option -> {
                resendVerificationCode()
            }
        }
    }

//    fun verifyRegistration(){
//        val this_intent = intent
//        val verification_details = this_intent.getSerializableExtra("verification_details") as HashMap<String,String>
//
//        val params : HashMap<String,String> = HashMap()
//        params.put("username",verification_details["username"]!!)
//        params.put("password",verification_details["password"]!!)
//
//        ApiRequest.post(this@RegistrationVerificationActivity,API.SIGN_IN,params,
//                object : ApiRequest.URLCallback {
//                    override fun didURLResponse(response: String) {
//                        Log.d("Sign in", response)
//                        mSession.authorize(response)
//                        //if account not yet verified
//
//
//                    }
//                },
//                    object : ApiRequest.ErrorCallback{
//                        override fun didURLError(error: VolleyError) {
//                            Toast.makeText(applicationContext,"Error", Toast.LENGTH_SHORT).show()
//                            Log.d("Error ", error.toString())
//                        }
//                    })
////        if(registration_verification_code_input_text.text.toString().trim() != verification_code){
////            Toast.makeText(this@RegistrationVerificationActivity,"Verification code does not match, try again",Toast.LENGTH_SHORT).show()
////            return
////        }
////        val intent = Intent(this, MainActivity::class.java)
////        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////        startActivity(intent)
//    }

    fun verify() {
        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"
        headers.put("Content-Type","application/x-www-form-urlencoded")
        headers.put("Authorization",authorization)

        val verification_code_params : HashMap<String,String> = HashMap()
        verification_code_params.put("verification_code",registration_verification_code_input_text.text.toString().trim())

        val message = "Verifying..."

        ApiRequest.post(this@RegistrationVerificationActivity, API.VERIFY,headers,verification_code_params, message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Verify", response)
                        val intent = Intent(this@RegistrationVerificationActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }

                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Toast.makeText(applicationContext,"Error", Toast.LENGTH_SHORT).show()
                        Log.d("Error ", error.toString())
                    }

                })



    }




    fun resendVerificationCode(){
        val headers = HashMap<String,String>()
        val params = HashMap<String,String>()

        val authorization = "Bearer ${mSession.token()}"


        headers.put("Content-Type","application/x-www-form-urlencoded")
        headers.put("Authorization",authorization)

        ApiRequest.put(this, API.RESEND_VERIFICATION,headers,params,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Resend Verification",response)
                    }

                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Toast.makeText(applicationContext,"Error", Toast.LENGTH_SHORT).show()
                        Log.d("Error ", error.toString())
                    }

                })
    }
}
