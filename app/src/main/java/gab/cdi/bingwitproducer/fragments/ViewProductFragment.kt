package gab.cdi.bingwitproducer.fragments


import android.content.Context
import android.net.Uri
import android.os.Build.PRODUCT
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.*
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.extensions.convertToCurrencyDecimalFormat
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.ProducerAuctionProduct
import gab.cdi.bingwitproducer.models.ProducerProduct
import gab.cdi.bingwitproducer.utils.ImageUtil
import gab.cdi.bingwitproducer.utils.TimeUtil
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_view_product_3.*
import org.json.JSONObject
import java.io.Serializable
import kotlin.collections.HashMap






/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ViewProductFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ViewProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewProductFragment : BaseFragment() {

    // TODO: Rename and change types of parameters
     var product_id : String? = null
     var product_type : String? = null
     var product : ProducerProduct? = null
     var product_auction : ProducerAuctionProduct? = null
    private var max : Float = 65f
    private var max_change : Float = max

    private lateinit var mSession : Session

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var timer : CountDownTimer
    var con : Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            product_type = arguments?.getString(PRODUCT_TYPE)
            when(product_type){
                "fixed" -> {
                    product = arguments?.getSerializable(PRODUCT) as ProducerProduct
                    product_id = product!!.id
                }
                "auction" -> {
                    product_auction = arguments?.getSerializable(PRODUCT) as ProducerAuctionProduct
                    product_id = product_auction!!.auction_id
                }
            }
        }

        con = context
        mActivity = activity as MainActivity
        mSession = Session(con)
        mActivity.toolbar.title = setTitle()


    }

    override fun setTitle(): String {
        return ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_product_3, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }
    override fun onStart() {
        super.onStart()
    }



    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun initUI(){
        edit_product_button.setOnClickListener {

            val popup : PopupMenu = PopupMenu(context,edit_product_button)
            popup.menuInflater.inflate(R.menu.product_popup_menu,popup.menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when(item.itemId){
                        R.id.action_edit_product -> {
                            val params : HashMap<String,Any> = HashMap()
                            params.put("product_id",product_id!!)
                            params.put("product_type",product_type!!)
                            mActivity.displaySelectedId(R.id.nav_edit_product,params)
                        }
                        R.id.action_delete_product-> {
                            val remove_product_dialog = RemoveProductDialogFragment()
                            val bundle = Bundle()
                            bundle.putString("product_name",product_name.text.toString())
                            bundle.putString("product_type",product_type)
                            bundle.putString("product_id",product_id)
                            remove_product_dialog.arguments = bundle
                            remove_product_dialog.show(activity?.supportFragmentManager,"remove_product")
                        }
                    }
                    return true
                }
            })
            popup.show()
        }

        when(product_type){
            "fixed" -> displayProductDetails()

            "auction" -> displayProductAuctionDetails()
        }

    }

    fun displayProductDetails(){
        product_name?.text = product?.name
        product_price_per_kilogram?.visibility = View.VISIBLE
        product_price_per_kilogram?.text = "P${"%.2f".format(product?.price_per_kilo)}/kg"
        product_product_type?.text = product?.product_type
        product_stock?.text = " • ${product?.stock}kg in stock"
        product_price_stock?.visibility = View.VISIBLE
        product_auction_prices?.visibility = View.GONE
        product_posted_date?.text = "Posted on ${TimeUtil.changeDateFormatToUserFriendlyDate(product?.created_at!!)}"
        if(this@ViewProductFragment.view?.isAttachedToWindow == true){
            GlideApp.with(this@ViewProductFragment)?.load(product?.image_url)?.placeholder(ImageUtil.placeholder(product?.product_category!!.toUpperCase()))?.into(product_image)

        }
    }

    fun displayProductAuctionDetails(){
        product_name?.text = product_auction?.name
        product_auction_prices?.visibility = View.VISIBLE
        product_price_per_kilogram?.visibility = View.GONE
        auction_schedule_layout?.visibility = View.VISIBLE
        product_auction_max_price?.text = "P${product_auction?.max_price}"
        product_auction_end_price?.text = "P${product_auction?.min_price}"
        product_auction_current_price?.text ="P${product_auction?.max_price}"
        product_product_type?.text = product_auction?.type
        product_price_per_kilogram?.visibility = View.GONE
        product_price_stock?.visibility = View.VISIBLE
        product_stock?.text = product_auction?.stock.toString() + "kg for sale"
        product_posted_date?.text = "Posted on ${TimeUtil.changeDateFormatToUserFriendlyDate(product_auction?.created_at!!)}"
        product_start_datetime?.text = "Starts on ${TimeUtil.changeDateFormatToUserFriendlyDateUTC8(product_auction?.start!!)}"
        product_end_datetime?.text = "Ends on ${TimeUtil.changeDateFormatToUserFriendlyDateUTC8(product_auction?.end!!)}"


        if(TimeUtil.convertDateStringToLong(product_auction?.start!!) - System.currentTimeMillis() <= 300000){
            edit_product_button.visibility = View.GONE
        }

        if(System.currentTimeMillis() < TimeUtil.convertDateStringToLong(product_auction?.end!!) && System.currentTimeMillis() < TimeUtil.convertDateStringToLong(product_auction?.start!!) ) {
            startCountdownBidding(product_auction!!)
        }
        else if(System.currentTimeMillis() > TimeUtil.convertDateStringToLong(product_auction?.start!!) && System.currentTimeMillis() < TimeUtil.convertDateStringToLong(product_auction?.end!!) && TimeUtil.convertDateStringToLong(product_auction?.end!!) - System.currentTimeMillis() > 600000){
            startBidding(product_auction!!)
            edit_product_button.visibility = View.GONE
            setHasOptionsMenu(false)
        }
        else if(TimeUtil.convertDateStringToLong(product_auction?.end!!) - System.currentTimeMillis() <= 600000){
            startCountdownTenMinutes(product_auction!!)
        }
        else if(System.currentTimeMillis() > TimeUtil.convertDateStringToLong(product_auction?.end!!)){
            edit_product_button.visibility = View.GONE
        }

        if(this@ViewProductFragment.view?.isAttachedToWindow == true){
            GlideApp.with(this@ViewProductFragment).load(product_auction?.image_url)?.placeholder(ImageUtil.placeholder(product_auction?.product_category!!.toUpperCase()))?.into(product_image)
        }
    }

    fun fetchProductById(){
        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization",authorization)
        ApiRequest.get(context, "${API.GET_PRODUCT_BY_ID}/$product_id",headers, HashMap(),object : ApiRequest.URLCallback{
            override fun didURLResponse(response: String) {
                Log.d("product",response)
                val json = JSONObject(response).getJSONObject("product")
                product = ProducerProduct(json)
                product_name?.text = product?.name
                //product_name?.text = json.optString("name","")
                product_price_per_kilogram?.visibility = View.VISIBLE
                product_price_per_kilogram?.text = "P${"%.2f".format(product?.price_per_kilo)}/kg"
//                product_price_per_kilogram?.text = "P${"%.2f".format(json.optDouble("price_per_kilo"))}/kg"
                product_product_type?.text = product?.product_type
//                product_product_type?.text = json.optJSONObject("product_type").optString("name")
                product_stock?.text = " • ${json.optInt("stock")}kg in stock"
                product_price_stock?.visibility = View.VISIBLE
                product_auction_prices?.visibility = View.GONE
//                product_posted_date?.text = "Posted on ${TimeUtil.changeDateFormatToUserFriendlyDate(json.optString("createdAt"))}"
                product_posted_date?.text = "Posted on ${TimeUtil.changeDateFormatToUserFriendlyDate(product!!.created_at)}"
                if(this@ViewProductFragment.view?.isAttachedToWindow == true){
                    GlideApp.with(this@ViewProductFragment)?.load(json?.optString("image_url"))?.placeholder(ImageUtil.placeholder(product!!.product_category.toUpperCase()))?.into(product_image)

                }

            }
        }, object : ApiRequest.ErrorCallback{
            override fun didURLError(error: VolleyError) {
            }
        })
    }

    fun fetchProductAuctionById(){
        ApiRequest.get(context,"${API.GET_PRODUCT_AUCTION_BY_ID}/$product_id",HashMap(), HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("Get product auction",response)
                        val json = JSONObject(response).optJSONObject("product")
                        product_auction = ProducerAuctionProduct(json)
                        product_name?.text = json.optJSONObject("Product").optString("name")
                        product_auction_prices?.visibility = View.VISIBLE
                        product_price_per_kilogram?.visibility = View.GONE
                        auction_schedule_layout?.visibility = View.VISIBLE
                        product_auction_max_price?.text = "P${json.optString("max_price")}"
                        product_auction_end_price?.text = "P${json.optString("min_price")}"
                        product_auction_current_price?.text ="P${json.optString("max_price")}"
                        product_product_type?.text = json.optJSONObject("Product").optJSONObject("product_type").optString("name")
                        product_price_per_kilogram?.visibility = View.GONE
                        product_price_stock?.visibility = View.VISIBLE
                        product_stock?.text = json.optJSONObject("Product").optInt("stock").toString() + "kg for sale"
                        product_posted_date?.text = "Posted on ${TimeUtil.changeDateFormatToUserFriendlyDate(json.optString("createdAt"))}"
                        product_start_datetime?.text = "Starts on ${TimeUtil.changeDateFormatToUserFriendlyDateUTC8(json.optString("start"))}"
                        product_end_datetime?.text = "Ends on ${TimeUtil.changeDateFormatToUserFriendlyDateUTC8(json.optString("end"))}"
                        timer?.cancel()
                        if(TimeUtil.convertDateStringToLong(product_auction?.start!!) - System.currentTimeMillis() <= 300000){
                            edit_product_button.visibility = View.GONE
                        }


                        if(System.currentTimeMillis() < TimeUtil.convertDateStringToLong(json.optString("end")) && System.currentTimeMillis() < TimeUtil.convertDateStringToLong(json.optString("start")) ) {
                            startCountdownBidding(product_auction!!)
                        }
                        else if(System.currentTimeMillis() > TimeUtil.convertDateStringToLong(json.optString("start")) && System.currentTimeMillis() < TimeUtil.convertDateStringToLong(json.optString("end"))){
                            startBidding(product_auction!!)
                            edit_product_button.visibility = View.GONE
                            setHasOptionsMenu(false)
                        }
                        else if(System.currentTimeMillis() > TimeUtil.convertDateStringToLong(json.optString("end"))){
                            edit_product_button.visibility = View.GONE
                        }
                        if(this@ViewProductFragment.view?.isAttachedToWindow == true){
                            GlideApp.with(this@ViewProductFragment).load(json?.optJSONObject("Product")?.optString("image_url"))?.placeholder(ImageUtil.placeholder(product_auction!!.product_category.toUpperCase()))?.into(product_image)
                        }
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {}
                })
    }




    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun startBidding(product_auction : ProducerAuctionProduct){
        remaining_time_layout.visibility = View.VISIBLE
        remaining_time?.visibility = View.VISIBLE
        val max = (TimeUtil.convertDateStringToLong(product_auction?.end!!)) - (TimeUtil.convertDateStringToLong(product_auction?.start!!)) - 600000
        val max_min_time_diff_in_seconds = max/1000
        val now_min_time_diff_in_seconds = (System.currentTimeMillis() - TimeUtil.convertDateStringToLong(product_auction?.start!!))/1000
        val range_price_diff = product_auction?.max_price.toDouble() - product_auction?.min_price.toDouble()
        val decrement = range_price_diff/max_min_time_diff_in_seconds
        var current_price = product_auction?.max_price.toDouble() - (now_min_time_diff_in_seconds*decrement)

        var remaining = TimeUtil.convertDateStringToLong(product_auction?.end!!) - System.currentTimeMillis() - 600000
        timer = object : CountDownTimer(remaining,1000){
            override fun onFinish() {
                if(this@ViewProductFragment.view?.isAttachedToWindow == true){
                    this.cancel()
                    remaining_time?.text = "00:00:00"
                    product_auction_current_price?.text = "P${product_auction?.min_price}"
                    remaining_time_banner?.text = "Bidding has concluded"
                    startCountdownTenMinutes(product_auction)
                }

            }

            override fun onTick(millisUntilFinished: Long) {
                remaining = TimeUtil.convertDateStringToLong(product_auction?.end!!) - System.currentTimeMillis() - 600000
                remaining_time_banner?.text = "Bidding ends after"
                remaining_time?.text = TimeUtil.hmsTimeFormatter(remaining)
                product_auction_current_price?.text = "P${current_price.convertToCurrencyDecimalFormat()}"
                current_price -= decrement

            }
        }.start()
    }

    fun startCountdownBidding(product_auction : ProducerAuctionProduct){
        remaining_time?.visibility = View.VISIBLE
        remaining_time_layout.visibility = View.VISIBLE
        val max = (TimeUtil.convertDateStringToLong(product_auction?.start!!)) - (TimeUtil.convertDateStringToLong(product_auction?.created_at!!))
        var remaining = (TimeUtil.convertDateStringToLong(product_auction?.start!!) - System.currentTimeMillis())
        timer = object : CountDownTimer(remaining,1000){
            override fun onFinish() {
                if(this@ViewProductFragment.view?.isAttachedToWindow == true){
                    cancel()
                    edit_product_button?.visibility = View.GONE
                    startBidding(product_auction)
                }

            }

            override fun onTick(millisUntilFinished: Long) {
                remaining = TimeUtil.convertDateStringToLong(product_auction?.start!!) - System.currentTimeMillis()
                remaining_time_banner?.text = "This product will be available to bid after"
                remaining_time?.text = TimeUtil.hmsTimeFormatter(remaining)
                if(TimeUtil.convertDateStringToLong(product_auction?.start!!) - System.currentTimeMillis() <= 300000){
                    edit_product_button.visibility = View.GONE
                }

            }
        }.start()
    }

    fun startCountdownTenMinutes (product_auction : ProducerAuctionProduct){
        remaining_time_layout?.visibility = View.VISIBLE
        remaining_time?.visibility = View.VISIBLE
        edit_product_button?.visibility = View.GONE
        val max = TimeUtil.convertDateStringToLong(product_auction.end)
        var remaining = max - System.currentTimeMillis()
        object : CountDownTimer(remaining,1000){
            override fun onFinish() {
                this.cancel()
            }

            override fun onTick(millisUntilFinished: Long) {
                remaining = max - System.currentTimeMillis()
                remaining_time_banner?.text = "Time remaining to sell product"
                remaining_time?.text = TimeUtil.hmsTimeFormatter(remaining)
            }
        }.start()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val PRODUCT_ID = "product_id"
        private val PRODUCT_TYPE = "product_type"
        private val PRODUCT = "product"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(product_id: String, product_type : String): ViewProductFragment {
            val fragment = ViewProductFragment()
            val args = Bundle()
            args.putString(PRODUCT_ID, product_id)
            args.putString(PRODUCT_TYPE,product_type)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(product : ProducerProduct, product_type : String) : ViewProductFragment {
            val fragment = ViewProductFragment()
            val args = Bundle()
            args.putString(PRODUCT_TYPE,product_type)
            args.putSerializable(PRODUCT,product)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(product : ProducerAuctionProduct, product_type : String) : ViewProductFragment {
            val fragment = ViewProductFragment()
            val args = Bundle()
            args.putString(PRODUCT_TYPE,product_type)
            args.putSerializable(PRODUCT,product)
            fragment.arguments = args
            return fragment
        }
    }


}

//    inner class BingwitCountdownTimer(start_time:Long, end_time:Long) : CountDownTimer(start_time,end_time){
//        override fun onFinish() {
//            remaining_time_text.text = "0"
//            auction_timer.progress = 0f
//        }
//
//        override fun onTick(millisUntilFinished: Long) {
//            remaining_time_text.text = (millisUntilFinished/1000).toInt().toString()
//            auction_timer.progress = (remaining_time_text.text.toString().toFloat()/max)*100
//        }
//    }
// Required empty public constructor
