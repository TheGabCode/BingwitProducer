package gab.cdi.bingwitproducer.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.text.TextUtils.substring
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var registration_fullname_edit_text : TextInputEditText
    private lateinit var registration_address_edit_text : TextInputEditText
    private lateinit var  registration_phone_number_edit_text : TextInputEditText
    private lateinit var registration_username_edit_text : TextInputEditText
    private lateinit var registration_password_edit_text : TextInputEditText
    private lateinit var registration_confirm_password_edit_text :TextInputEditText
    private lateinit var registration_continue_button : Button
    private lateinit var confirm_password_textinputlayout : TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initUI()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun initUI(){
        registration_fullname_edit_text = findViewById(R.id.registration_fullname_edit_text)
        registration_address_edit_text = findViewById(R.id.registration_address_edit_text)
        registration_phone_number_edit_text = findViewById(R.id.registration_phone_number_edit_text)
        registration_username_edit_text = findViewById(R.id.registration_username_edit_text)
        registration_password_edit_text = findViewById(R.id.registration_password_edit_text)
        registration_confirm_password_edit_text = findViewById(R.id.registration_confirm_password_edit_text)
        registration_continue_button = findViewById(R.id.registration_continue_button)
        confirm_password_textinputlayout= findViewById(R.id.confirm_password_textinputlayout)
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
//                val intent = Intent(this,RegistrationBiographyActivity::class.java)
//                startActivity(intent)
                continueRegistration()
            }
        }
    }

    fun continueRegistration() {
        if(TextUtils.isEmpty(registration_fullname_edit_text.text.toString().trim())){
            Toast.makeText(this,"Fill in full name",Toast.LENGTH_SHORT).show()
            return
        }
        if(TextUtils.isEmpty(registration_address_edit_text.text.toString().trim())){
            Toast.makeText(this,"Fill in address",Toast.LENGTH_SHORT).show()
            return
        }
        if(TextUtils.isEmpty(registration_phone_number_edit_text.text.toString().trim())){
            Toast.makeText(this,"Fill in phone number",Toast.LENGTH_SHORT).show()
            return
        }

        if(!registration_phone_number_edit_text.text.toString().trim().matches("^(09|\\+639)\\d{9}\$".toRegex())){
            Toast.makeText(this,"Wrong number format",Toast.LENGTH_SHORT).show()
            return
        }
        if(TextUtils.isEmpty(registration_username_edit_text.text.toString().trim())){
            Toast.makeText(this,"Fill in username",Toast.LENGTH_SHORT).show()
            return
        }
        if(TextUtils.isEmpty(registration_password_edit_text.text.toString())){
            Toast.makeText(this,"Fill in password",Toast.LENGTH_SHORT).show()
            return
        }
        if(TextUtils.isEmpty(registration_confirm_password_edit_text.text.toString())){
            Toast.makeText(this,"Confirm password",Toast.LENGTH_SHORT).show()
            return
        }

        if(!registration_password_edit_text.text.toString().equals(registration_confirm_password_edit_text.text.toString())){
            Toast.makeText(this,"Passwords do no match",Toast.LENGTH_SHORT).show()
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
        //params.put("image_url",java.util.UUID.randomUUID().toString())
        params.put("type","producer")

        val intent = Intent(this,RegistrationBiographyActivity::class.java)
        intent.putExtra("registration_details",params)
        startActivity(intent)

    }
}
