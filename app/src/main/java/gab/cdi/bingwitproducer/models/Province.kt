package gab.cdi.bingwitproducer.models

import org.json.JSONObject

class Province(jsonObject: JSONObject){
    var province_name : String = jsonObject.optString("provDesc")
    var province_code : String = jsonObject.optString("provCode")

    override fun toString(): String {
        return province_name
    }
}