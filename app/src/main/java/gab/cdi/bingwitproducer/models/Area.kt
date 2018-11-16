package gab.cdi.bingwitproducer.models

import org.json.JSONObject

/**
 * Created by Default on 29/10/2018.
 */
class Area(var jsonObject: JSONObject) {
    var area_address : String = jsonObject.optString("area_address")
    var id : String = jsonObject.optString("id")

    override fun toString(): String {
        return area_address
    }
}