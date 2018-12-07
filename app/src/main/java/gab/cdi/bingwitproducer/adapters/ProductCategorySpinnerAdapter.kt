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
import gab.cdi.bingwitproducer.utils.ImageUtil
import kotlinx.android.synthetic.main.product_category_dropdown_item.view.*


class ProductCategorySpinnerAdapter(context : Context, custom_view_id : Int, objects : List<String>, product_categories : ArrayList<ProductCategory> ) : ArrayAdapter<String>(context, custom_view_id,objects) {
    var product_categories_arraylist : ArrayList<ProductCategory> = product_categories
    var this_context = context


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position,convertView,parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position,convertView,parent)
    }

    fun getCustomView(position : Int, view : View?, parentView : ViewGroup) : View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val my_spinner : View = inflater.inflate(R.layout.product_category_dropdown_item,parentView,false)
        val product_category_name_textview : TextView = my_spinner.findViewById(R.id.category_name)
        val product_category_icon_imageview : ImageView = my_spinner.findViewById(R.id.category_icon)
        product_category_name_textview.text = product_categories_arraylist[position].name

        GlideApp.with(this_context).load(ImageUtil.placeholder(product_categories_arraylist[position].name.toUpperCase())).into(product_category_icon_imageview)
        return my_spinner
    }
}