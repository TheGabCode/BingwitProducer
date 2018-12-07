package gab.cdi.bingwitproducer.models

import org.json.JSONObject

class Barangay(jsonObject: JSONObject){
    var barangay_name : String = jsonObject.optString("brgyDesc")
    var municipality_code : String = jsonObject.optString("citymunCode")
    var baranggay_code : String = jsonObject.optString("brgyCode")

    override fun toString(): String {
        return barangay_name
    }
}