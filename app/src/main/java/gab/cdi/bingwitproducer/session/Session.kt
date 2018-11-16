package gab.cdi.bingwit.session

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Default on 08/08/2018.
 */
class Session {
    var context : Context? = null
    var sharedPrefs : SharedPreferences? = null
    var sharedPrefsEditor : SharedPreferences.Editor? = null
    var userToken : String? = ""
    var SHARED_PREFERENCE = "SHARED_PREFERENCE"
    var TOKEN = "TOKEN"
    var LOGGED = "LOGGED"
    var USER_ID = "USER_ID"
    var USER_DATA = "USER_DATA"
    var USER_VERIFIED = "USER_VERIFIED"


    constructor(context: Context?) {
        this.context        = context
        sharedPrefs         = context?.getSharedPreferences(SHARED_PREFERENCE,Context.MODE_PRIVATE)
        sharedPrefsEditor   = sharedPrefs?.edit()
    }

    fun authorize(raw : String){ //authorize for registration
        var token : String? = null
        var id : String? = null
        try {
            token = JSONObject(raw).getString("token")
            id = JSONObject(raw).getString("id")
            sharedPrefsEditor?.putString(TOKEN,token)?.apply()
            sharedPrefsEditor?.putString(USER_ID,id)
            sharedPrefsEditor?.putBoolean(LOGGED,true)?.apply()
        }
        catch (e : JSONException){
            e.printStackTrace()
        }
    }


    fun authorizeLogIn(raw : String){
        var token : String? = null
        try{
            //token = JSONObject(raw).getJSONObject("data").getJSONArray("items").getJSONObject(0).string("token")
        }
        catch (e : JSONException){
            e.printStackTrace()
        }

        sharedPrefsEditor?.putString(TOKEN,token)?.apply()
        sharedPrefsEditor?.putBoolean(LOGGED,true)?.apply()
        //put other settings here; region, skin, playback speed
    }

    fun token() : String? {
        return sharedPrefs?.getString(TOKEN,"")
    }

    fun id() : String? {
        return sharedPrefs?.getString(USER_ID,"")
    }


    fun isUserLoggedIn() : Boolean? {
        return sharedPrefs?.getBoolean(LOGGED,false)
    }

    fun deauthorize(){
        sharedPrefsEditor?.clear()?.commit()
    }

}