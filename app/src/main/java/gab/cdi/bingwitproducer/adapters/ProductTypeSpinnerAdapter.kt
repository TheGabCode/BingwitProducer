package gab.cdi.bingwitproducer.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.models.ProductCategory
import gab.cdi.bingwitproducer.models.ProductType
import gab.cdi.bingwitproducer.utils.ImageUtil
import kotlinx.android.synthetic.main.product_category_dropdown_item.view.*


class ProductTypeSpinnerAdapter(context : Context, custom_view_id : Int, objects : List<String>, product_types : ArrayList<ProductType> ) : ArrayAdapter<String>(context, custom_view_id,objects) {
    var product_types_arraylist : ArrayList<ProductType> = product_types
    var this_context = context


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position,convertView,parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position,convertView,parent)
    }

    fun getCustomView(position : Int, view : View?, parentView : ViewGroup) : View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val my_spinner : View = inflater.inflate(R.layout.product_type_dropdown_item,parentView,false)
        val product_category_name_textview : TextView = my_spinner.findViewById(R.id.type_name)
        val product_type_aliases_textview : TextView = my_spinner.findViewById(R.id.type_aliases)
        val type_aka_textview : TextView = my_spinner.findViewById(R.id.type_aka)
        val this_product_type = product_types_arraylist[position]

        product_category_name_textview.text = this_product_type.name
        if(this_product_type.aliases.size > 0){
            product_type_aliases_textview.visibility = View.VISIBLE
            type_aka_textview.visibility = View.VISIBLE
        }
        for(i in 0..this_product_type.aliases.size-1){
            product_type_aliases_textview.text =product_type_aliases_textview.text.toString().trim()+ "${this_product_type.aliases[i].trim()},"
        }
        if(product_type_aliases_textview.text.toString().endsWith(",")){
            product_type_aliases_textview.text = product_type_aliases_textview.text.toString().trimEnd(',')

        }
        product_type_aliases_textview.text = product_type_aliases_textview.text.toString() + " "

        return my_spinner
    }
}