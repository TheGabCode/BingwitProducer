package gab.cdi.bingwitproducer.models

import org.json.JSONObject

/**
 * Created by Default on 11/10/2018.
 */
data class Rating(var jsonObject: JSONObject){
    var rating : Double = jsonObject.optDouble("rating",0.0)
    var rating_id : String? = jsonObject.optString("rating_id")
    var rating_user_id : String? = jsonObject.optString("rating_user_id")
    var rating_user_fullname : String? = jsonObject.optString("rating_user_fullname","Juwan Dela Cruz")
    var rating_comment : String? = jsonObject.optString("rating_comment","")
    var rating_purchased_date : String = jsonObject.optString("rating_purchased_date")
    var rating_transaction = Transaction(jsonObject.optJSONObject("rating_transaction"))

}