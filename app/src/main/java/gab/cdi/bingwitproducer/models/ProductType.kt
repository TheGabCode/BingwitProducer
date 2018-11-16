package gab.cdi.bingwitproducer.models

import org.json.JSONObject

/**
 * Created by Default on 23/10/2018.
 */
data class ProductType(var jsonObject: JSONObject) {
    var id = jsonObject.optString("id")
    var name = jsonObject.optString("name")


    override fun toString(): String {
        return name
    }
}