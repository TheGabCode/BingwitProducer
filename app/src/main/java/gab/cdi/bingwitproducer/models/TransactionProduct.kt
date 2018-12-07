package gab.cdi.bingwitproducer.models

import org.json.JSONObject

/**
 * Created by Default on 30/10/2018.
 */
class TransactionProduct(jsonObject: JSONObject){
    var id : String = jsonObject.optString("id")
    var quantity : Int = jsonObject.optInt("quantity",0)
    val amount : Double = jsonObject.optDouble("amount")
    var rating : Double = jsonObject.optDouble("rating",0.0)
    var comment : String = jsonObject.optString("comment","")
    var transaction_product_name : String = jsonObject.optJSONObject("product").optString("name")
    var transaction_product_image_url : String = jsonObject.optJSONObject("product").optString("image_url")
    var is_cancelled : Boolean = jsonObject.optBoolean("isCancelled",false)
    var transaction_category_name = jsonObject.optJSONObject("product").optJSONObject("product_type").optJSONObject("product_category").optString("name")

    /*
      "quantity": 1,
        "amount": 22,
        "rating": null,
        "comment": null,
        "deletedAt": "2018-10-25T07:34:43.000Z",
        "createdAt": "2018-10-25T07:28:06.000Z",
        "updatedAt": "2018-10-25T07:34:43.000Z",
        "transaction_id": "b8cade23-869c-4bdc-a179-4c80fc17e0ad",
        "product_id": "e8cfc2b7-d665-11e8-acb1-8c16453380ac",
        "Product": {
          "image_url": "122",
          "name": "tipals"
        }
     */
}