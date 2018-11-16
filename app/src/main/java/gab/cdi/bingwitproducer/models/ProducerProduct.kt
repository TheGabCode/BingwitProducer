package gab.cdi.bingwitproducer.models

import org.json.JSONObject
import java.io.Serializable

/**
 * Created by Default on 23/10/2018.
 */
class ProducerProduct( jsonObject: JSONObject) : Serializable{
    var id : String = jsonObject.optString("id","")
    var name : String = jsonObject.optString("name","")
    var image_url : String = jsonObject.optString("image_url","")
    var stock : Int = jsonObject.optInt("stock",0)
    var price_per_kilo : Double = jsonObject.optDouble("price_per_kilo",0.00)
    var product_type : String = jsonObject.optJSONObject("product_type").optString("name","fish")
    var product_category : String = jsonObject.optJSONObject("product_type").optJSONObject("product_category").optString("name")
    var created_at : String = jsonObject.optString("createdAt")

}