package gab.cdi.bingwitproducer.adapters

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.CountDownTimer
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.instacart.library.truetime.TrueTime
import com.jackandphantom.circularprogressbar.CircleProgressbar
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.R.id.*
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.extensions.convertToCurrencyDecimalFormat
import gab.cdi.bingwitproducer.fragments.ViewProductAuctionFragment
import gab.cdi.bingwitproducer.fragments.ViewProductFragment
import gab.cdi.bingwitproducer.models.ProducerAuctionProduct
import gab.cdi.bingwitproducer.utils.DialogUtil
import gab.cdi.bingwitproducer.utils.ImageUtil
import gab.cdi.bingwitproducer.utils.TimeUtil
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_view_product_2.*
import java.net.SocketTimeoutException
import java.sql.Time
import java.util.*

/**
 * Created by Default on 09/10/2018.
 */



class ProducerAuctionProductAdapter(val products : ArrayList<ProducerAuctionProduct>, val context : Context?, val selling_method : String) : RecyclerView.Adapter<ProducerAuctionProductAdapter.ViewHolder>() {
    lateinit var auction_timer : CountDownTimer
    lateinit var auction_countdown_timer : CountDownTimer
    lateinit var ten_minute_timer : CountDownTimer
    override fun getItemCount(): Int {
        return products.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_product_placement_item_2, parent, false)
        val viewholder = ViewHolder(view)
        return viewholder
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val this_product = products[position]
        val current_time_in_ms = TimeUtil.getCurrentTimeMillis()
        val auction_end_time_in_ms = TimeUtil.convertDateStringToLong(this_product.end)
        val auction_start_time_in_ms = TimeUtil.convertDateStringToLong(this_product.start)

        holder.product_name.text = this_product.name
        holder.product_type.text = this_product.type
        holder.product_price_stock.visibility = View.VISIBLE
        holder.product_price.visibility = View.GONE
        holder.product_auction_price_range.visibility = View.VISIBLE
        holder.product_stock.text = "${this_product.stock.toInt()}kg in stock"
        holder.product_auction_price_range.visibility = View.VISIBLE
        holder.product_auction_start_datetime.visibility = View.VISIBLE
        holder.product_auction_start_datetime.text = holder.product_auction_start_datetime.text.toString().replace("_datetime_",TimeUtil.changeDateFormat(this_product.start))
        holder.product_auction_price_range.text = holder.product_auction_price_range.text.toString().replace("_start_","${this_product.max_price.convertToCurrencyDecimalFormat()}").replace("_end_","${this_product.min_price.convertToCurrencyDecimalFormat()}")

        GlideApp.with(context!!).load(this_product.image_url).placeholder(ImageUtil.placeholder(this_product.product_category.toUpperCase())).into(holder.product_image)

        if(current_time_in_ms < auction_start_time_in_ms && current_time_in_ms < auction_end_time_in_ms){
            startCountdownToBidding(holder,this_product)
        }
        else if(current_time_in_ms > TimeUtil.convertDateStringToLong(this_product.start) && System.currentTimeMillis() < TimeUtil.convertDateStringToLong(this_product.end) && TimeUtil.convertDateStringToLong(this_product.end) - TimeUtil.getCurrentTimeMillis() > 600000) {
            startBidding(holder, this_product)
        }else if(TimeUtil.convertDateStringToLong(this_product.end) - TimeUtil.getCurrentTimeMillis() <= 600000){
            startCountdownTenMinutes(holder,this_product)
        }


        holder.itemView.setOnClickListener {
            val mActivity = context as MainActivity
            val params : HashMap<String,Any> = HashMap()
            params.put("product_id",this_product.auction_id!!)
            params.put("product_type","auction")
            mActivity.fragmentAddBackStack(ViewProductFragment.newInstance(this_product,"auction"),"view_product_fragment")
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
        var product_auction_price_range = view.findViewById<TextView>(R.id.product_auction_price_range)
        var product_auction_start_datetime = view.findViewById<TextView>(R.id.product_auction_start_datetime)
        var product_auction_current_price = view.findViewById<TextView>(R.id.product_auction_current_price)
        var auction_timer_background = view.findViewById<View>(R.id.auction_timer_background)
        var product_auction_timer = view.findViewById<CircleProgressbar>(R.id.product_auction_timer)
        var product_auction_remaining_time = view.findViewById<TextView>(R.id.product_auction_remaining_time)
        var product_auction_actual_price_layout = view.findViewById<LinearLayout>(R.id.product_auction_actual_price_layout)
        var timer_countdown_to_start_bidding : CountDownTimer? = null
        var timer_start_bidding : CountDownTimer? = null
        var timer_countdown_ten_minutes : CountDownTimer? = null
        var auction_cancelled_indicator = view.findViewById<TextView>(R.id.auction_cancelled_indicator)
    }

    fun startBidding(holder : ViewHolder, this_product : ProducerAuctionProduct){
        holder.auction_timer_background.visibility = View.VISIBLE
        holder.product_auction_timer.visibility = View.VISIBLE
        holder.product_auction_remaining_time.visibility = View.VISIBLE
        holder.product_auction_actual_price_layout.visibility = View.VISIBLE
        holder.product_auction_start_datetime.visibility = View.GONE
        holder.product_auction_timer.foregroundProgressColor = Color.parseColor("#ffff00")
        val max = (TimeUtil.convertDateStringToLong(this_product.end)) - (TimeUtil.convertDateStringToLong(this_product.start)) - 600000
        val max_min_time_diff_in_seconds = max/1000
        val now_min_time_diff_in_seconds = (TimeUtil.getCurrentTimeMillis() - TimeUtil.convertDateStringToLong(this_product.start))/1000
        val range_price_diff = this_product.max_price - this_product.min_price
        val decrement = range_price_diff/max_min_time_diff_in_seconds
        var current_price = this_product.max_price - (now_min_time_diff_in_seconds*decrement)

        var remaining = TimeUtil.convertDateStringToLong(this_product.end) - TimeUtil.getCurrentTimeMillis() - 600000
       holder.timer_start_bidding = object : CountDownTimer(remaining,1000){
            override fun onFinish() {
                holder.timer_start_bidding?.cancel()
                holder.product_auction_remaining_time?.text = "00:00:00"
                holder.product_auction_timer?.progress = 0f
                holder.product_auction_current_price?.text = this_product.min_price.convertToCurrencyDecimalFormat().toFloat().toInt().toString()
                startCountdownTenMinutes(holder,this_product)
            }

            override fun onTick(millisUntilFinished: Long) {
                remaining = TimeUtil.convertDateStringToLong(this_product.end) - TimeUtil.getCurrentTimeMillis() - 600000
                holder.product_auction_remaining_time.text = TimeUtil.hmsTimeFormatter(remaining) + " before bidding ends"
                holder.product_auction_timer.progress = ((remaining.toFloat()/1000)/(max.toFloat()/1000))*100
                holder.product_auction_current_price?.text = "P${current_price.convertToCurrencyDecimalFormat().toFloat().toInt()}"
                current_price -= decrement

            }
        }.start()
    }

    fun startCountdownToBidding(holder : ViewHolder, this_product: ProducerAuctionProduct){
        holder.auction_timer_background.visibility = View.VISIBLE
        holder.product_auction_timer.visibility = View.VISIBLE
        holder.product_auction_remaining_time.visibility = View.VISIBLE
        val max = (TimeUtil.convertDateStringToLong(this_product.start)) - (TimeUtil.convertDateStringToLongUTC8(this_product.created_at))
        var remaining = TimeUtil.convertDateStringToLong(this_product.start) - TimeUtil.getCurrentTimeMillis()
        holder.timer_countdown_to_start_bidding = object : CountDownTimer(remaining,1000) {
            override fun onFinish() {
                holder.product_auction_start_datetime.visibility = View.GONE
                holder.timer_countdown_to_start_bidding?.cancel()
                startBidding(holder,this_product)
            }

            override fun onTick(millisUntilFinished: Long) {
                remaining = TimeUtil.convertDateStringToLong(this_product.start) - TimeUtil.getCurrentTimeMillis()
                holder.product_auction_remaining_time.text = TimeUtil.hmsTimeFormatter(remaining)+ " before bidding starts"
                holder.product_auction_timer.progress = ((remaining.toFloat()/1000)/(max.toFloat()/1000))*100
            }
        }.start()
    }

    fun startCountdownTenMinutes (holder : ViewHolder, this_product : ProducerAuctionProduct){
        holder.auction_timer_background.visibility = View.VISIBLE
        holder.product_auction_timer.visibility = View.VISIBLE
        holder.product_auction_remaining_time.visibility = View.VISIBLE
        holder.product_auction_start_datetime.visibility = View.GONE
        holder.product_auction_actual_price_layout.visibility = View.VISIBLE
        holder.product_auction_current_price?.text = "P${this_product.min_price.convertToCurrencyDecimalFormat().toFloat().toInt()}.00"
        holder.product_auction_timer.foregroundProgressColor = Color.parseColor("#ff6f00")
        val max = TimeUtil.convertDateStringToLong(this_product.end)
        var remaining = max - TimeUtil.getCurrentTimeMillis()
        holder.timer_countdown_ten_minutes = object : CountDownTimer(remaining,1000){
            override fun onFinish() {
                holder.timer_countdown_ten_minutes?.cancel()
                products.remove(this_product)
                notifyItemRemoved(holder.adapterPosition)
                notifyItemRangeChanged(holder.adapterPosition,products.size)
            }

            override fun onTick(millisUntilFinished: Long) {
                remaining = max - TimeUtil.getCurrentTimeMillis()
                holder.product_auction_remaining_time.text = TimeUtil.hmsTimeFormatter(remaining)+ " left to sell product"
                holder.product_auction_timer.progress = ((remaining.toFloat())/600000)*100
            }
        }.start()
    }

}