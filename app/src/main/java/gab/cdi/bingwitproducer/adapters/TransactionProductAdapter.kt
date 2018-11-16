package gab.cdi.bingwitproducer.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.models.TransactionProduct
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.widget.ImageView
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.extensions.convertToCurrencyDecimalFormat

/**
 * Created by Default on 30/10/2018.
 */


/**
 * Created by Default on 09/10/2018.
 */



class TransactionProductAdapter(val transaction_products : ArrayList<TransactionProduct>, val context : Context?) : RecyclerView.Adapter<TransactionProductAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return transaction_products.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.transaction_product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val this_transaction_product = transaction_products[position]

        holder.transaction_product_name.text = this_transaction_product.transaction_product_name
//        holder.transaction_product_price_per_kilo.text = "Php ${this_transaction_product.amount.convertToCurrencyDecimalFormat()}/kg"
        holder.transaction_product_quantity.text = "Qty: ${this_transaction_product.quantity}kg"
        holder.transaction_product_total_price.text = "Php ${(this_transaction_product.amount).convertToCurrencyDecimalFormat()}"
        holder.transaction_product_rating.rating = this_transaction_product.rating.toFloat()
        if(this_transaction_product.comment == "null" || this_transaction_product.comment == null){
            holder.transaction_product_comment.text = ""
            holder.transaction_product_rating.visibility = View.GONE
            holder.transaction_product_comment.visibility = View.GONE
        }
        else{
            holder.transaction_product_comment.text = this_transaction_product.comment
        }

        if(context != null){
            GlideApp.with(context).load(this_transaction_product.transaction_product_image_url).placeholder(R.drawable.ic_bingwit_logo).into(holder.transaction_product_image)
        }

        if(this_transaction_product.is_cancelled){
            holder.transaction_product_cancelled_cover.visibility = View.VISIBLE
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val transaction_product_name : TextView = view.findViewById(R.id.transaction_product_name)
        val transaction_product_quantity : TextView = view.findViewById(R.id.transaction_product_quantity)
        val transaction_product_total_price : TextView = view.findViewById(R.id.transaction_product_total_price)
        val transaction_product_rating : RatingBar = view.findViewById(R.id.transaction_product_rating)
        val transaction_product_comment : TextView = view.findViewById(R.id.transaction_product_comment)
        val transaction_product_image : ImageView = view.findViewById(R.id.transaction_product_image)
        val transaction_product_cancelled_cover : ConstraintLayout = view.findViewById(R.id.transaction_product_cancelled_cover)
    }
}