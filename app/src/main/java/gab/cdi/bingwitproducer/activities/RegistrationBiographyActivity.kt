package gab.cdi.bingwitproducer.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import kotlinx.android.synthetic.main.activity_registration_biography.*
import org.json.JSONObject

class RegistrationBiographyActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mSession : Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_biography)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mSession = Session(this)
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
            R.id.biography_continue_button -> {
                continueRegistration()
            }
        }
    }

    fun continueRegistration(){
        val this_intent = intent
        val params = this_intent.getSerializableExtra("registration_details") as HashMap<String,String>

        val headers : HashMap<String,String> = HashMap()
        headers.put("Content-Type","application/x-www-form-urlencoded")

        val message = "Signing up..."
        ApiRequest.post(this, API.SIGN_UP,headers,params,message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        val json = JSONObject(response)
                        Log.d("Sign up",response)
                        if(json.getString("message") == "Ok"){
                            val this_params : HashMap<String,String> = HashMap()

                            this_params.put("username",params.get("username")!!)
                            this_params.put("password",params.get("password")!!)

                            signIn(this_params)


                        }
                    }

                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Toast.makeText(applicationContext,"Errorzzz", Toast.LENGTH_SHORT).show()
                        Log.d("Error ", error.toString())
                    }

                })

    }


    fun signIn(params : HashMap<String,String>) {
        val message : String? = null
        ApiRequest.post(this@RegistrationBiographyActivity,API.SIGN_IN,params,message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Sign in",response)
                        mSession.authorize(response)
                        getUser(mSession.id()!!)
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Toast.makeText(applicationContext,"Error", Toast.LENGTH_SHORT).show()
                        Log.d("Error ", error.toString())
                    }
                })
    }

    fun getUser(id : String){
        val header : HashMap<String,String> = HashMap()

        val authorization = "Bearer ${mSession.token()}"
        header.put("Authorization",authorization)

        val params : HashMap<String,String> = HashMap()

        ApiRequest.get(this@RegistrationBiographyActivity,"${API.GET_USER}/$id",header,params,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Get user",response)
                        val json = JSONObject(response)
                        val user = json.getJSONObject("user")
                        if(user["status"] == "inactive"){
                            val intent = Intent(this@RegistrationBiographyActivity, RegistrationVerificationActivity::class.java)
                            startActivity(intent)
                        }
                        else if(user["status"] == "active"){
                            val intent = Intent(this@RegistrationBiographyActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Toast.makeText(this@RegistrationBiographyActivity,"Error", Toast.LENGTH_SHORT).show()
                        Log.d("Error ", error.toString())
                    }
                })
    }
}
