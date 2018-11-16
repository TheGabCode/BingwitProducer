package gab.cdi.bingwitproducer.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.fragments.CustomAlertDialogFragment
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.utils.DialogUtil
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.json.JSONObject

class SplashScreenActivity : AppCompatActivity(), CustomAlertDialogFragment.OnFragmentInteractionListener {
    lateinit var mSession : Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSession = Session(this)

        setContentView(R.layout.activity_splash_screen)
        if(mSession.isUserLoggedIn() == true){
            checkUserStatus(mSession.id()!!)
        }
        else{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    fun checkUserStatus(id : String) {
        val header : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"
        header.put("Authorization",authorization)
        val params : HashMap<String,String> = HashMap()
        ApiRequest.get(this@SplashScreenActivity,"${API.GET_USER}/$id",header,params,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Get user",response)
                        val json = JSONObject(response)
                        val user = json.getJSONObject("user")
                        if(user["status"] == "inactive" && user["type"] == "producer"){
                            mSession.deauthorize()
                            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else if(user["status"] == "active" && user["type"] == "producer"){
                            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else{
                            mSession.deauthorize()
                            Toast.makeText(this@SplashScreenActivity,"Invalid username and password", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        //Toast.makeText(this@LoginActivity,"Error", Toast.LENGTH_SHORT).show()
                        DialogUtil.showVolleyErrorDialog(supportFragmentManager,error)
                        val intent = Intent(this@SplashScreenActivity,LoginActivity::class.java)
                        startActivity(intent)
                        mSession.deauthorize()
                        finish()
                        Log.d("Error ", error.toString())
                    }
                })
    }
}
