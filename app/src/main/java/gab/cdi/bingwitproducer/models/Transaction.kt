package gab.cdi.bingwitproducer.models

import org.json.JSONObject
import java.io.Serializable

/**
 * Created by Default on 09/10/2018.
 */
class Transaction (jsonObject: JSONObject) : Serializable{
    var id : String = jsonObject.optString("id")
    var status : String = jsonObject.optString("status","order placed")
    var tracking_number : String = jsonObject.optString("tracking_number","Not available")
    var total_amount : Double = jsonObject.optDouble("total_amount")
    var rating : Double = jsonObject.optDouble("rating",0.0)
    var comment : String = jsonObject.optString("additional_information")
    var deletedAt : String = jsonObject.optString("deletedAt","")
    var createdAt : String = jsonObject.optString("createdAt")
    var updatedAt : String = jsonObject.optString("updatedAt")
    var product_user_id : String = jsonObject.optString("product_user_id")
    var consumer_user_id : String = jsonObject.optString("consumer_user_id")
    var consumer_name : String = jsonObject.optJSONObject("consumer").optString("full_name")


    /*
    "id": "e5c616d4-8b5d-4551-9106-b35431a5d860",
        "address": "asdf",
        "status": "order placed",
        "tracking_number": "f57148a9-d0ba-4721-b5af-0e51eefd5899",
        "total_amount": 1,
        "rating": 1,
        "comment": "1",
        "deletedAt": null,
        "createdAt": "2018-10-22T00:29:17.000Z",
        "updatedAt": "2018-10-22T00:33:14.000Z",
        "producer_user_id": "604d7b49-97d1-4fb9-aa13-dcd1d995604f",
        "consumer_user_id": "92cfd956-a6db-4b99-8d28-4f8ea313c301"


     */

}