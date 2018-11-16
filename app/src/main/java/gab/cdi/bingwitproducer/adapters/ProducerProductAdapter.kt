package gab.cdi.bingwitproducer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.fragments.ViewProductFragment
import gab.cdi.bingwitproducer.models.ProducerProduct
import gab.cdi.bingwitproducer.utils.ImageUtil
import kotlinx.android.synthetic.main.app_bar_main.*

/**
 * Created by Default on 09/10/2018.
 */



class ProducerProductAdapter(val products : ArrayList<ProducerProduct>, val context : Context?, val selling_method : String) : RecyclerView.Adapter<ProducerProductAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return products.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_product_placement_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val this_product = products[position]
        holder.product_name.text = this_product.name
        holder.product_type.text = this_product.product_type
        holder.product_stock.text = " • ${this_product.stock}kg in stock"
        holder.product_price_stock.visibility = View.VISIBLE
        holder.product_price.text = "P${"%.2f".format(this_product.price_per_kilo)}/kg"
        GlideApp.with(context!!).load(this_product.image_url).placeholder(ImageUtil.placeholder(this_product.product_category.toUpperCase())).into(holder.product_image)

        holder.itemView.setOnClickListener {
            val mActivity = context as MainActivity
            val params : HashMap<String,Any> = HashMap()
            params.put("product_id",this_product.id!!)
            params.put("product_type","fixed")
            mActivity.fragmentAddBackStack(ViewProductFragment.newInstance(this_product,"fixed"),"view_product_fragment")
            mActivity.fab.hide()
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        var product_name = view.findViewById<TextView>(R.id.product_name)
        var product_type = view.findViewById<TextView>(R.id.product_type)
        var product_stock = view.findViewById<TextView>(R.id.product_stock)
        var product_price = view.findViewById<TextView>(R.id.product_price)
        var product_price_stock = view.findViewById<LinearLayout>(R.id.product_price_stock)
        var product_image = view.findViewById<ImageView>(R.id.product_image)
    }
}