package gab.cdi.bingwitproducer.models

import org.json.JSONObject

class StatusLog(jsonObject: JSONObject){
    var to : String = jsonObject.optString("to")
    var updatedAt : String = jsonObject.optString("updatedAt")
    var isActive : Boolean = false
}