package gab.cdi.bingwitproducer.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
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
import android.widget.*
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.R.id.*
import gab.cdi.bingwitproducer.fragments.CustomAlertDialogFragment
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.*
import gab.cdi.bingwitproducer.utils.DialogUtil
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.concurrent.TimeUnit

class RegistrationActivity : AppCompatActivity(), View.OnClickListener, CustomAlertDialogFragment.OnFragmentInteractionListener {
    private lateinit var mSession : Session
    private var registration_areas_dropdown_array : ArrayList<Area> = ArrayList()

    lateinit var provinces_array_list : ArrayList<Province>
    private var municipality_array_list : ArrayList<Municipality> = ArrayList()
    private var brgy_array_list : ArrayList<Barangay> = ArrayList()

    private var province_municipality : HashMap<String,MutableList<Municipality>> = HashMap()
    private var municipality_brgy : HashMap<String,MutableList<String>> = HashMap()

    private var bryg_spinner_adapter : ArrayAdapter<String>? = null
    private var town_spinner_adapter : ArrayAdapter<Municipality>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        mSession = Session(this)
        //loadProvinces()
        async{
            addAddressSpinnerListeners()
        }
        initUI()


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    suspend fun addAddressSpinnerListeners() = coroutineScope{
        provinces_array_list = doLoadProvinces().await()

        val province_spinner_adapter : ArrayAdapter<Province> = ArrayAdapter(this@RegistrationActivity,android.R.layout.simple_spinner_dropdown_item,provinces_array_list)
        registration_province_spinner.adapter = province_spinner_adapter
        registration_province_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected_province = provinces_array_list[position]
                val selected_province_id = selected_province.province_code
                town_spinner_adapter = ArrayAdapter<Municipality>(this@RegistrationActivity,R.layout.support_simple_spinner_dropdown_item,province_municipality[selected_province_id]!!)
                town_spinner_adapter!!.setNotifyOnChange(true)
                registration_town_spinner.adapter = town_spinner_adapter
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        registration_town_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected_municipality = registration_town_spinner.selectedItem as Municipality
                val selected_municipality_id = selected_municipality.municipality_code
                Log.d("tag",selected_municipality.municipality_name)
                bryg_spinner_adapter = ArrayAdapter<String>(this@RegistrationActivity,R.layout.support_simple_spinner_dropdown_item,municipality_brgy[selected_municipality_id]!!)
                bryg_spinner_adapter!!.setNotifyOnChange(true)
                registration_brgy_spinner.adapter =  bryg_spinner_adapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        loadMunicipalities()
        loadBaranggays()

    }

    fun initUI(){
        getAreas()
    }

    fun doLoadProvinces() = GlobalScope.async{
        loadProvinces1()
    }

    fun loadProvinces1() : ArrayList<Province> {
        val provinces = application.assets.open("refprovince.json").bufferedReader().use{
            it.readText()
        }
        val arraylist_province : ArrayList<Province> = ArrayList()
        val json_provinces : JSONArray = JSONObject(provinces).optJSONArray("RECORDS")
        for(i in 0..json_provinces.length()-1){
            val province_object = Province(json_provinces[i] as JSONObject)
            arraylist_province.add(province_object)
            province_municipality[province_object.province_code] = mutableListOf()
        }
        return arraylist_province
    }


    fun loadMunicipalities() {
            val municipalities = application.assets.open("refcitymun.json").bufferedReader().use{
                it.readText()
            }
            val json_municipality = JSONObject(municipalities).optJSONArray("RECORDS")
            for(i in 0..json_municipality.length()-1){
                val municipality_object = Municipality(json_municipality[i] as JSONObject)
                province_municipality[municipality_object.provincial_code]?.add(municipality_object)
                municipality_brgy[municipality_object.municipality_code] = mutableListOf()
                municipality_array_list.add(municipality_object)
            }

            runOnUiThread {
                town_spinner_adapter?.notifyDataSetChanged()
            }
    }


    fun loadBaranggays() {
        val brgys = application.assets.open("refbrgy.json").bufferedReader().use{
            it.readText()
        }
        val json_brgys = JSONObject(brgys).optJSONArray("RECORDS")
        for(i in 0..json_brgys.length()-1) {
            val brgy_object = Barangay(json_brgys[i] as JSONObject)
            municipality_brgy[brgy_object.municipality_code]?.add(brgy_object.barangay_name)
        }
        runOnUiThread {
            bryg_spinner_adapter?.notifyDataSetChanged()
        }
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
        params.put("contact_number",formatted_phone_number)
        params.put("username",registration_username_edit_text.text.toString().trim())
        params.put("password",registration_password_edit_text.text.toString().trim())
        params.put("type","producer")
        val json_address = JSONObject()
        json_address.put("province",(registration_province_spinner.selectedItem as Province).province_name)
        json_address.put("municipality",(registration_town_spinner.selectedItem as Municipality).municipality_name)
        json_address.put("barangay",(registration_brgy_spinner.selectedItem as String))
        json_address.put("street",registration_address_edit_text.text.toString())
        val stringified_json_address = json_address.toString()
        params.put("address",stringified_json_address)
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
                            this_params.put("type",params.get("type")!!)
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
