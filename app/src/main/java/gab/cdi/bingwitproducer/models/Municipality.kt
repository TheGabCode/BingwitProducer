package gab.cdi.bingwitproducer.models

import org.json.JSONObject

class Municipality(jsonObject: JSONObject){
    var municipality_name : String = jsonObject.optString("citymunDesc")
    var provincial_code : String = jsonObject.optString("provCode")
    var municipality_code : String = jsonObject.optString("citymunCode")

    override fun toString(): String {
        return municipality_name
    }
}