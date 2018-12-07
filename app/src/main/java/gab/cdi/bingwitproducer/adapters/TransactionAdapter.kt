package gab.cdi.bingwitproducer.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.models.Product
import gab.cdi.bingwitproducer.models.Transaction
import gab.cdi.bingwitproducer.extensions.convertToCurrencyDecimalFormat
import gab.cdi.bingwitproducer.fragments.ViewTransactionFragment
import gab.cdi.bingwitproducer.utils.TimeUtil

/**
 * Created by Default on 09/10/2018.
 */



class TransactionAdapter(val transactions : ArrayList<Transaction>, val context : Context?) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return transactions.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_transaction_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val this_transaction = transactions[position]

        holder.transaction_status.text = this_transaction.status.toUpperCase()
        if(this_transaction.tracking_number != "null") holder.transaction_tracking_number.text = this_transaction.tracking_number
        else holder.transaction_tracking_number.text = "Tracking # not available"
        holder.transaction_total_amount.text = "P${this_transaction.total_amount.convertToCurrencyDecimalFormat()}"
        holder.transaction_last_updated.text = "Last updated ${TimeUtil.changeDateFormatToUserFriendlyDate(this_transaction.updatedAt)}"
        holder.transaction_consumer_name.text = this_transaction.consumer_name
//        holder.transaction_rating.rating = this_transaction.rating.toFloat()
//        if(this_transaction.tracking_number != null){
//            holder.transaction_tracking_number.text = this_transaction.tracking_number
//        }
//
//        holder.transaction_total_amount.text = "Php ${this_transaction.total_amount.convertToCurrencyDecimalFormat()}"
//        holder.transaction_consumer_name.text = this_transaction.consumer_name
//        holder.transaction_consumer_address.text = this_transaction.address
//        val created_at_tokens = this_transaction.createdAt.split("T")
//        val created_at_time_tokens = created_at_tokens[1].split(":")
//        holder.transaction_placed_on.text = "Transaction placed on ${TimeUtil.changeDateFormatToUserFriendlyDate(this_transaction.createdAt)}"
//        val updated_at_tokens = this_transaction.updatedAt.split("T")
//        val updated_at_time_tokens = updated_at_tokens[1].split(":")
//
//        holder.transaction_last_updated.text = "Transaction last updated on ${TimeUtil.changeDateFormatToUserFriendlyDate(this_transaction.updatedAt)} "
//        holder.transaction_comment.text = this_transaction.comment
//
        holder.itemView.setOnClickListener {
//            val mActivity = context as MainActivity
//            mActivity.displaySelectedId(R.id.nav_view_transaction, hashMapOf("position" to 0,"transaction_id" to this_transaction.id))
            val mActivity = context as MainActivity
            mActivity.fragmentAddBackStack(ViewTransactionFragment.newInstance(this_transaction.id),"view_transaction")
        }


    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val transaction_tracking_number : TextView = view.findViewById(R.id.transaction_tracking_number)
        val transaction_total_amount : TextView = view.findViewById(R.id.transaction_total_amount)
        val transaction_status : TextView = view.findViewById(R.id.transaction_status)
        val transaction_last_updated : TextView = view.findViewById(R.id.transaction_last_updated)
        val transaction_consumer_name : TextView = view.findViewById(R.id.transaction_consumer_name)
    }
}