package gab.cdi.bingwitproducer.models

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
    var name : String = jsonObject.optJSONObject("Product").optString("name")
    var stock : Double = jsonObject.optJSONObject("Product").optDouble("stock")
    var image_url : String = jsonObject.optJSONObject("Product").optString("image_url")
    var type : String = jsonObject.optJSONObject("Product").optJSONObject("product_type").optString("name","Seafood")
    var product_category : String = jsonObject.optJSONObject("Product").optJSONObject("product_type").optJSONObject("product_category").optString("name")
    /*
    *  "product": {
    "id": "63438c9d-2902-4c45-bf5e-60b6d3269704",
    "product_id": "4542df63-018a-43ee-9aac-742e72a80e6b",
    "min_price": "300.00",
    "max_price": "1000.00",
    "start": "2018-10-28T03:00:00.000Z",
    "end": "2018-10-29T06:00:00.000Z",
    "Product": {
      "id": "4542df63-018a-43ee-9aac-742e72a80e6b",
      "name": "Rellenong Bangus",
      "image_url": "http://sample_rellenongbangus.img.url/",
      "producer_user_id": "cb602e12-e193-4f06-848c-db8c00cffcca",
      "product_type_id": "d14e4af0-f5c7-4d6b-9d57-47168378665d",
      "Product_type": {
        "name": "Milkfish"
      },
      "User": {
        "full_name": "Sample Name",
        "area_id": null,
        "Area": null
      }
    }
  },*/
}