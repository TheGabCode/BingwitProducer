package gab.cdi.bingwitproducer.models

import org.json.JSONObject

data class ProductCategory(var jsonObject: JSONObject) {
    var id   : String = jsonObject.optString("id")
    var name : String = jsonObject.optString("name")
    var product_types_arraylist : ArrayList<ProductType> = ArrayList()
    override fun toString(): String {
        return name
    }
}