package gab.cdi.bingwitproducer.models

import org.json.JSONObject

/**
 * Created by Default on 09/10/2018.
 */
data class Product(var jsonObject: JSONObject) {
    var product_name : String? = jsonObject.optString("product_name")
    var product_type : String? = jsonObject.optString("product_type")
    var product_id   : String? = jsonObject.optString("product_id")
    var product_weight : Double? = jsonObject.optDouble("product_weight",1.0)
    var product_selling_method : Int? = jsonObject.optInt("product_selling_method",1)
    var product_price : Double = jsonObject.optDouble("product_price",0.0)
    var product_initial_bidding_price : Double? = jsonObject.optDouble("auction_price",0.0)
}