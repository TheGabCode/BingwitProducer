package gab.cdi.bingwitproducer.adapters
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.models.Product
import gab.cdi.bingwitproducer.models.Rating
import gab.cdi.bingwitproducer.models.Transaction
import org.w3c.dom.Text

/**
 * Created by Default on 11/10/2018.
 */


class RatingsAdapter(val ratings : ArrayList<Transaction>, val context : Context?) : RecyclerView.Adapter<RatingsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return ratings.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_rating_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val this_rating = ratings[position]
//        holder.rating_user_fullname.text = this_rating.consumer_name
//        holder.rating_purchased_date.text = "Purchased on " + this_rating.transaction_date_ordered
//        holder.rating_rating_bar.rating = this_rating.transaction_rating.toFloat()
//        holder.rating_comment.text = this_rating.transaction_comment
//        holder.rating_product_name.text = this_rating.product.product_name
//        holder.rating_product_type.text = this_rating.product.product_type
//        holder.rating_shipping_address.text = this_rating.transaction_address
//        holder.rating_product_weight.text = this_rating.product_ordered_quantity.toString() + "Kg"
//        if(this_rating.product.product_selling_method == 1){
//            holder.rating_product_price_per_kilogram.text = this_rating.transaction_total_amount.toString() + "PHP"
//        }
//        else{
//            holder.rating_product_price_per_kilogram.text = this_rating.transaction_auction_sold_price.toString() + "PHP"
//        }




    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val rating_user_fullname = view.findViewById<TextView>(R.id.rating_user_fullname)
        val rating_purchased_date = view.findViewById<TextView>(R.id.rating_purchased_date)
        val rating_rating_bar = view.findViewById<RatingBar>(R.id.rating_rating_bar)
        val rating_shipping_address = view.findViewById<TextView>(R.id.rating_shipping_address)
        val rating_comment = view.findViewById<TextView>(R.id.rating_comment)
        val rating_product_image = view.findViewById<ImageView>(R.id.rating_product_image)
        val rating_product_name = view.findViewById<TextView>(R.id.rating_product_name)
        val rating_product_type = view.findViewById<TextView>(R.id.rating_product_type)
        val rating_product_weight = view.findViewById<TextView>(R.id.rating_product_weight)
        val rating_product_price_per_kilogram = view.findViewById<TextView>(R.id.rating_product_price_per_kilogram)
    }
}