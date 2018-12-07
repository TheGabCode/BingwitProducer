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
import gab.cdi.bingwitproducer.models.StatusLog
import gab.cdi.bingwitproducer.utils.TimeUtil

/**
 * Created by Default on 09/10/2018.
 */



class StatusLogAdapter(val status_logs : ArrayList<StatusLog>, val context : Context?) : RecyclerView.Adapter<StatusLogAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return status_logs.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.status_log_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val this_status_log = status_logs[position]
        holder.transaction_status_log_status.text = this_status_log.to.capitalize()
        holder.transaction_status_log_updated_at.text = TimeUtil.changeDateFormatToUserFriendlyDate(this_status_log.updatedAt)


    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val transaction_status_log_updated_at : TextView = view.findViewById(R.id.transaction_status_log_updated_at)
        val transaction_status_log_status : TextView = view.findViewById(R.id.transaction_status_log_status)
    }
}