package gab.cdi.bingwitproducer.models

import android.os.CountDownTimer
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by Default on 26/10/2018.
 */
class ProducerAuctionProduct (jsonObject: JSONObject) : Serializable {
    var auction_id : String = jsonObject.optString("id")
    var product_id : String = jsonObject.optString("product_id")
    var min_price : Double = jsonObject.optDouble("min_price",0.0)
    var max_price : Double = jsonObject.optDouble("max_price",0.0)
    var start : String = jsonObject.optString("start")
    var end : String = jsonObject.optString("end")
    var created_at : String = jsonObject.optString("createdAt")
    var name : String = jsonObject.optJSONObject("product").optString("name")
    var stock : Double = jsonObject.optJSONObject("product").optDouble("stock")
    var image_url : String = jsonObject.optJSONObject("product").optString("image_url")
    var type : String = jsonObject.optJSONObject("product").optJSONObject("product_type").optString("name","Seafood")
    var product_category : String = jsonObject.optJSONObject("product").optJSONObject("product_type").optJSONObject("product_category").optString("name")

}