package gab.cdi.bingwitproducer.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.models.Product

/**
 * Created by Default on 09/10/2018.
 */



class ProductAdapter(val products : ArrayList<Product>, val context : Context?) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return products.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_product_placement_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val this_product = products[position]
        holder.product_name_textview.text = this_product.product_name
        holder.product_type_textview.text = this_product.product_type
        holder.product_weight_textview.text = this_product.product_weight.toString() + " Kg"
        when(this_product.product_selling_method){
            1 -> {
                holder.product_price_textview.text = this_product.product_price.toString() + "PHP/Kg"
            }
            0 -> {
                holder.product_price_textview.text = this_product.product_price.toString() + "PHP"
                holder.product_selling_method.text = "Placed for auction"
            }
        }

        holder.itemView.setOnClickListener {
            val mActivity = context as MainActivity
            val params : HashMap<String,Any> = HashMap()
            params.put("product_id",this_product.product_id!!)
            mActivity.displaySelectedId(R.id.nav_view_product,params)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        var product_name_textview = view.findViewById<TextView>(R.id.product_name)
        var product_type_textview = view.findViewById<TextView>(R.id.product_type)
        var product_weight_textview = view.findViewById<TextView>(R.id.product_weight)
        var product_price_textview = view.findViewById<TextView>(R.id.product_price_per_kilogram)
        var product_selling_method = view.findViewById<TextView>(R.id.product_selling_method)
        var product_image = view.findViewById<ImageView>(R.id.product_image)
    }
}