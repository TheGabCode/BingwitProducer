package gab.cdi.bingwitproducer.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.models.Product
import gab.cdi.bingwitproducer.models.Transaction

/**
 * Created by Default on 09/10/2018.
 */



class TransactionAdapter(val transactions : ArrayList<Transaction>, val context : Context?) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return transactions.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_product_transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val this_transaction = transactions[position]
        holder.transaction_product_name_textview.text = this_transaction.product.product_name
        holder.transaction_product_type_textview.text = this_transaction.product.product_type
        holder.transaction_product_weight_textview.text = this_transaction.product.product_weight.toString() + "Kg"
        if(this_transaction.product.product_selling_method == 1){
            holder.transaction_product_price_textview.text = this_transaction.product.product_price.toString() + "PHP/Kg"
        }
        else{
            holder.transaction_product_price_textview.text = this_transaction.product.product_price.toString() + "PHP"
        }
        holder.transaction_product_status.text = this_transaction.transaction_status

        var status : String = this_transaction.transaction_status!!
        var position : Int?
        when(status){
            "On-going","Ready for delivery" -> {
                position = 0
            }
            "Delivered" -> {
                position = 1
            }
            "Returned" -> {
                position = 2
            }
            else -> {
                position = 0
            }
        }


        holder.itemView.setOnClickListener {
            var mActivity = context as MainActivity
            var params : HashMap<String,Any> = hashMapOf("position" to position as Any)
            mActivity.displaySelectedId(R.id.nav_view_transaction,params)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        var transaction_product_name_textview = view.findViewById<TextView>(R.id.transaction_product_name)
        var transaction_product_type_textview = view.findViewById<TextView>(R.id.transaction_product_type)
        var transaction_product_weight_textview = view.findViewById<TextView>(R.id.transaction_product_ordered_weight)
        var transaction_product_price_textview = view.findViewById<TextView>(R.id.transaction_product_price_total_order)
        var transaction_product_status = view.findViewById<TextView>(R.id.transaction_status)
        var transaction_product_image = view.findViewById<ImageView>(R.id.transaction_product_image)
    }
}