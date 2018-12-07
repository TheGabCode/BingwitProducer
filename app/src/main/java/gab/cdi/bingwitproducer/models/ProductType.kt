package gab.cdi.bingwitproducer.models

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Default on 23/10/2018.
 */
class ProductType(jsonObject: JSONObject) {
    var id = jsonObject.optString("id")
    var name = jsonObject.optString("name")
    var aliases : ArrayList<String> = ArrayList()
    var product_type_aliases : JSONArray = jsonObject.optJSONArray("product_type_alias")


    fun getAliases(){
        for(i in 0..product_type_aliases.length()-1){
            val product_type_alias = product_type_aliases[i] as JSONObject
            if(product_type_alias.optString("alias") != name){
                aliases.add(product_type_alias.optString("alias"))
            }
        }
    }

    override fun toString(): String {
        return name
    }
}