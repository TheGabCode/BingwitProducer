package gab.cdi.bingwitproducer.models

import org.json.JSONObject

/**
 * Created by Default on 09/10/2018.
 */
data class Transaction (var jsonObject: JSONObject) {
    var transaction_id : String? = jsonObject.optString("transaction_id")
    var consumer_id : String? = jsonObject.optString("consumer_id")
    var consumer_name : String? = jsonObject.optString("consumer_name")
    var producer_id : String? = jsonObject.optString("producer_id")
    var producer_name : String? = jsonObject.optString("producer_name")
    var transaction_date_ordered : String? = jsonObject.optString("transaction_date_ordered")
    var transaction_address : String? = jsonObject.optString("transaction_address")
    var transaction_comment : String? = jsonObject.optString("transaction_comment")
    var transaction_rating : Double = jsonObject.optDouble("transaction_rating",0.0)
    var product = Product(jsonObject.optJSONObject("product"))
    var transaction_status : String? = jsonObject.optString("transaction_status")
    var product_ordered_quantity : Double = jsonObject.optDouble("product_ordered_quantity",0.0)
    var transaction_auction_sold_price : Double = jsonObject.optDouble("transaction_auction_sold_price",0.0)
    var transaction_total_amount : Double = jsonObject.optDouble("transaction_total_amount",0.0)

}