package gab.cdi.bingwitproducer.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.text.TextUtils.substring
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.fragments.CustomAlertDialogFragment
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.Area
import gab.cdi.bingwitproducer.utils.DialogUtil
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity(), View.OnClickListener, CustomAlertDialogFragment.OnFragmentInteractionListener {
    private lateinit var mSession : Session
    private var registration_areas_dropdown_array : ArrayList<Area> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        mSession = Session(this)
        initUI()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun initUI(){

        getAreas()
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
            R.id.registration_continue_button -> {
                continueRegistration()
            }
        }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    fun continueRegistration() {
        if(TextUtils.isEmpty(registration_fullname_edit_text.text.toString().trim())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in full name")
            return
        }
        if(TextUtils.isEmpty(registration_address_edit_text.text.toString().trim())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in address")
            return
        }
        if(TextUtils.isEmpty(registration_phone_number_edit_text.text.toString().trim())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in phone number")
            return
        }

        if(!registration_phone_number_edit_text.text.toString().trim().matches("^(09|\\+639)\\d{9}\$".toRegex())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Wrong number format")
            return
        }
        if(TextUtils.isEmpty(registration_username_edit_text.text.toString().trim())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in username")
            return
        }
        if(TextUtils.isEmpty(registration_password_edit_text.text.toString())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Fill in password")
            return
        }
        if(TextUtils.isEmpty(registration_confirm_password_edit_text.text.toString())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Confirm password")
            return
        }

        if(!registration_password_edit_text.text.toString().equals(registration_confirm_password_edit_text.text.toString())){
            DialogUtil.showErrorDialog(supportFragmentManager,1500,"Passwords do not match")
            confirm_password_textinputlayout.error = "Passwords do not match!"
            return
        }

        var formatted_phone_number = registration_phone_number_edit_text.text.toString().trim()
        if(formatted_phone_number.startsWith("09")){
            formatted_phone_number = StringBuilder("+639${formatted_phone_number.substring(2)}").toString()
        }
        val params : HashMap<String,String> = HashMap()
        params.put("full_name",registration_fullname_edit_text.text.toString().trim())
        params.put("address",registration_address_edit_text.text.toString().trim())
        params.put("contact_number",formatted_phone_number)
        params.put("username",registration_username_edit_text.text.toString().trim())
        params.put("password",registration_password_edit_text.text.toString().trim())
        params.put("type","producer")
        val selected_area = registration_area_spinner?.selectedItem as Area
        params.put("area_id",selected_area.id)
        signUp(params)

    }

    fun signUp(params : HashMap<String,String>){
        val message = "Signing up..."

        val headers : HashMap<String,String> = HashMap()
        headers.put("Content-Type","application/x-www-form-urlencoded")

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
                        Log.d("Error ", error.toString())
                        DialogUtil.showVolleyErrorDialog(supportFragmentManager,error)
                    }

                })
    }

    fun signIn(params : HashMap<String,String>){
        val message : String? = null
        ApiRequest.post(this@RegistrationActivity,API.SIGN_IN,params,message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Sign in",response)
                        mSession.authorize(response)
                        getUser(mSession.id()!!)
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Log.d("Error ", error.toString())
                        DialogUtil.showVolleyErrorDialog(supportFragmentManager,error)
                    }
                })
    }

    fun getUser(id : String){
        val header : HashMap<String,String> = HashMap()

        val authorization = "Bearer ${mSession.token()}"
        header.put("Authorization",authorization)

        val params : HashMap<String,String> = HashMap()

        ApiRequest.get(this@RegistrationActivity,"${API.GET_USER}/$id",header,params,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Get user",response)
                        val json = JSONObject(response)
                        val user = json.getJSONObject("user")
                        if(user["status"] == "inactive"){
                            val intent = Intent(this@RegistrationActivity, RegistrationVerificationActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else if(user["status"] == "active"){
                            val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Toast.makeText(this@RegistrationActivity,"Error", Toast.LENGTH_SHORT).show()
                        Log.d("Error ", error.toString())
                    }
                })
    }

    fun getAreas() {
        val headers : HashMap<String,String> = HashMap()
        ApiRequest.get(this,API.GET_AREAS,headers,HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        val json = JSONObject(response)
                        Log.d("Get areas",response)
                        val areas_json_array = json.getJSONObject("area").getJSONArray("rows")
                        for(i in 0..areas_json_array.length()-1){
                            registration_areas_dropdown_array.add(Area(areas_json_array[i] as JSONObject))
                        }
                        val areas_spinner_adapter : ArrayAdapter<Area> = ArrayAdapter(this@RegistrationActivity,android.R.layout.simple_spinner_dropdown_item,registration_areas_dropdown_array)

                        Log.d("areas array", registration_areas_dropdown_array.toString())
                        registration_area_spinner.adapter = areas_spinner_adapter
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {

                    }
                })
    }


}
