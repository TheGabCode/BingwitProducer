package gab.cdi.bingwitproducer.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony.BaseMmsColumns.TRANSACTION_ID
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.adapters.TransactionProductAdapter
import gab.cdi.bingwitproducer.extensions.convertToCurrencyDecimalFormat
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.Transaction
import gab.cdi.bingwitproducer.models.TransactionProduct
import gab.cdi.bingwitproducer.utils.TimeUtil
import kotlinx.android.synthetic.main.fragment_view_transaction.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ViewTransactionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ViewTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewTransactionFragment : BaseFragment() {

    // TODO: Rename and change types of parameters
    var mPosition: Int? = null
    var transaction_id : String? = null
    lateinit var transaction : Transaction
    private lateinit var mSession : Session
    private var mListener: OnFragmentInteractionListener? = null
    private var current_progress : Int? = null

    private var transaction_status_text_arraylist : ArrayList<String> = arrayListOf("order placed","ready for delivery","shipped","delivered","returned upon delivery")

    private var transaction_products_arraylist : ArrayList<TransactionProduct> = ArrayList()
    val CHANGE_TRANSACTION_STATUS = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSession = Session(context)
        if (arguments != null) {
            mPosition = arguments!!.getInt(POSITION)
            transaction_id = arguments!!.getString(TRANSACTION_ID)
            transaction = arguments!!.getSerializable(TRANSACTION) as Transaction
        }
    }

    override fun setTitle(): String {
        return ""
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_transaction, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.return_transaction,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id){
            R.id.action_return_transaction -> {
                val dialog = ConfirmTransactionStatusDialogFragment.newInstance(transaction_status_text_arraylist[4])
                dialog.setTargetFragment(this@ViewTransactionFragment, CHANGE_TRANSACTION_STATUS)
                dialog.show(activity?.supportFragmentManager, "confirm_status_change")
            }
        }
        return true
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun initUI(view : View) {
        displayTransactionDetails()
    }

    fun setTransactionStage(stage : Int){

        val message = "Updating transaction status..."

        val headers : HashMap<String,String> = HashMap()
        val authorization = "Authorization ${mSession.token()}"

        headers.put("Authorization", authorization)
        headers.put("Content-Type","application/x-www-form-urlencoded")

        val params : HashMap<String,String> = HashMap()
        params.put("status",transaction_status_text_arraylist[stage])
        ApiRequest.put(context,"${API.UPDATE_TRANSACTION_STAGE}/${transaction.id}",message,headers,params,
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("Update",response)
                        current_progress = stage
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {

                    }
                })
    }
    fun displayTransactionDetails(){
        transaction_recipient_name?.text = "${transaction.consumer_name}"
        transaction_order_number?.text = "Tracking # ${transaction.tracking_number}"
        transaction_order_date?.text = "Order placed on ${TimeUtil.changeDateFormatToUserFriendlyDate(transaction.createdAt)}"
        transaction_shipping_address?.text = "${transaction.address} "
        transaction_total?.text = "Php${transaction.total_amount.convertToCurrencyDecimalFormat()}"
        transaction_subtotal?.text = "Php ${transaction.total_amount.convertToCurrencyDecimalFormat()}"
        if(!TextUtils.isEmpty(transaction.comment)){
            consumer_additional_notes_layout.visibility = View.VISIBLE
            consumer_additional_notes?.text = "${transaction.comment}"
        }

        seekbar?.progress = transaction_status_text_arraylist.indexOf(transaction.status)
        if(transaction.status == "delivered"){
            setHasOptionsMenu(true)
        }
        else if(transaction.status == "returned upon delivery"){
            if(context != null) fetchTransactionProducts()
            isReturned()
            return
        }
        seekbar?.visibility = View.VISIBLE
        transaction_status_texts?.visibility = View.VISIBLE
        current_progress = seekbar?.progress
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(seekBar!!.progress <= current_progress!!){
                    seekbar?.progress = current_progress!!
                }
                else{
                    val dialog = ConfirmTransactionStatusDialogFragment.newInstance(transaction_status_text_arraylist[seekBar!!.progress])
                    dialog.setTargetFragment(this@ViewTransactionFragment,CHANGE_TRANSACTION_STATUS)
                    dialog.show(activity?.supportFragmentManager,"confirm_status_change")
                }
            }
        })
        if(context != null){
            fetchTransactionProducts()
        }
    }

    fun fetchTransactionById(){
        val headers : HashMap<String,String> = HashMap()

        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization",authorization)
        headers.put("Content-Type","application/x-www-form-urlencoded")

        ApiRequest.get(context,"${API.GET_TRANSACTION_BY_ID}/${mSession.id()}/transactions/${transaction_id}",headers,HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("Transaction",response)
                        val json = JSONObject(response).optJSONObject("transaction")
//                        transaction_recipient_name?.text = "Sold to ${json.optJSONObject("consumer").optString("full_name")}"
                        transaction_order_number?.text = "Tracking # ${json.optString("tracking_number")}"
                        val created_at_tokens = json.optString("createdAt").split("T")
                        val created_at_time_tokens = created_at_tokens[1].split(":")
                        transaction_order_date?.text = "Order placed on ${TimeUtil.changeDateFormatToUserFriendlyDate(json.optString("createdAt"))}"
                        transaction_shipping_address?.text = json.optString("address")+" "
                        transaction_total?.text = "Php ${json.optDouble("total_amount").convertToCurrencyDecimalFormat()}"
                        transaction_subtotal?.text = "Php ${json.optDouble("total_amount").convertToCurrencyDecimalFormat()}"
                        seekbar?.progress = transaction_status_text_arraylist.indexOf(json.optString("status"))
                        if(json.optString("status") == "delivered"){
                            setHasOptionsMenu(true)
                        }
                        else if(json.optString("status") == "returned upon delivery"){
                            if(context != null) fetchTransactionProducts()
                            isReturned()
                            return
                        }
                        seekbar?.visibility = View.VISIBLE
                        transaction_status_texts?.visibility = View.VISIBLE
                        current_progress = seekbar?.progress
                        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                if(seekBar!!.progress <= current_progress!!){
                                    seekbar?.progress = current_progress!!
                                }
                                else{
                                    val dialog = ConfirmTransactionStatusDialogFragment.newInstance(transaction_status_text_arraylist[seekBar!!.progress])
                                    dialog.setTargetFragment(this@ViewTransactionFragment,CHANGE_TRANSACTION_STATUS)
                                    dialog.show(activity?.supportFragmentManager,"confirm_status_change")
                                }
                            }
                        })
                        if(context != null){
                            fetchTransactionProducts()
                        }

                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {

                    }
                })
    }

    fun fetchTransactionProducts(){
        val headers : HashMap<String,String> = HashMap()

        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization", authorization)
        headers.put("Content-Type","application/x-www-form-urlencoded")
        ApiRequest.get(context,"${API.GET_TRANSACTION_PRODUCTS}/${mSession.id()}/transactions/${transaction.id}",headers, HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("t_products",response)
                        val json = JSONObject(response).optJSONObject("transaction_products")
                        var subtotal_amount : Double = 0.0
                        val t_products : JSONArray = json.optJSONArray("rows")
                        for(i in 0..t_products.length()-1){
                            val t_product = t_products[i] as JSONObject
                            transaction_products_arraylist.add(TransactionProduct(t_product))
                            subtotal_amount += t_product.optDouble("amount")
                        }
                        transaction_total?.text = "P${subtotal_amount.convertToCurrencyDecimalFormat()}"
                        transaction_subtotal?.text = "P${subtotal_amount.convertToCurrencyDecimalFormat()}"

                        transaction_products_recyclerview?.adapter = TransactionProductAdapter(transaction_products_arraylist,context)
                        transaction_products_recyclerview?.layoutManager = LinearLayoutManager(context)
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {

                    }
                })

    }

    fun isReturned(){
        transaction_status_delivered_text?.text = "Returned upon delivery"
        setHasOptionsMenu(false)
        seekbar?.visibility = View.GONE
        transaction_status_texts?.visibility = View.GONE
        seekbar_returned?.visibility = View.VISIBLE
        seekbar_returned?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekbar_returned?.progress = 2
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekbar_returned?.progress = 2
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekbar_returned?.progress = 2
            }
        })
        transaction_status_returned_texts?.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            CHANGE_TRANSACTION_STATUS -> {
                if(resultCode == Activity.RESULT_OK){
                    if(data != null){
                      if(data.getIntExtra("change_status",0) == 1){
                          Log.d("status",data.getStringExtra("transaction_status_string"))
                          if(data.getStringExtra("transaction_status_string") == "returned upon delivery"){
                              seekbar.thumb = ResourcesCompat.getDrawable(resources,R.drawable.transaction_status_seekbar_thumb_red,null)
                              isReturned()
//                              return_transaction_button_container.visibility = View.GONE
                          }

                          setTransactionStage(transaction_status_text_arraylist.indexOf(data.getStringExtra("transaction_status_string")))
                      }
                        else{
                          seekbar.progress = current_progress!!
                      }
                    }
                }
            }
        }
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
        private val POSITION = "position"
        private val TRANSACTION_ID = "transaction_id"
        private val TRANSACTION = "transaction"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(tab_position: Int?,transaction_id : String?): ViewTransactionFragment {
            val fragment = ViewTransactionFragment()
            val args = Bundle()
            args.putInt(POSITION, tab_position!!)
            args.putString(TRANSACTION_ID, transaction_id!!)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(transaction : Transaction) : ViewTransactionFragment{
            val fragment = ViewTransactionFragment()
            val args = Bundle()
            args.putSerializable(TRANSACTION,transaction)
            fragment.arguments = args
            return fragment
        }


    }
}// Required empty public constructor
