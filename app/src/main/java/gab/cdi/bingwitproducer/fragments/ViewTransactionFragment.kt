package gab.cdi.bingwitproducer.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
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
import com.shuhart.stepview.StepView
import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.R.id.*
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.adapters.StatusLogAdapter
import gab.cdi.bingwitproducer.adapters.TransactionProductAdapter
import gab.cdi.bingwitproducer.extensions.convertToCurrencyDecimalFormat
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.StatusLog
import gab.cdi.bingwitproducer.models.Transaction
import gab.cdi.bingwitproducer.models.TransactionProduct
import gab.cdi.bingwitproducer.utils.DialogUtil
import gab.cdi.bingwitproducer.utils.TimeUtil

import kotlinx.android.synthetic.main.fragment_view_transaction_2.*
import kotlinx.android.synthetic.main.fragment_view_transaction_2.view.*
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

    private var isTransactionStatusLogsVisible = false
    private var transaction_products_arraylist : ArrayList<TransactionProduct> = ArrayList()
    val CHANGE_TRANSACTION_STATUS = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSession = Session(context)
        if (arguments != null) {
            transaction_id = arguments!!.getString(TRANSACTION_ID)
        }

        view_transaction_skeleton_shimmer_layout?.startShimmerAnimation()
    }

    override fun setTitle(): String {
        return ""
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_transaction_2, container, false)
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
        step_view.setSteps(mutableListOf("Order placed","Ready for delivery","Shipped","Delivered"))
        step_view.setOnStepClickListener(object : StepView.OnStepClickListener{
            override fun onStepClick(step: Int) {
                val step_difference = step - step_view.currentStep
                if(step > step_view.currentStep && step_difference <= 1){
                    val dialog = ConfirmTransactionStatusDialogFragment.newInstance(transaction_status_text_arraylist[step])
                    dialog.setTargetFragment(this@ViewTransactionFragment,CHANGE_TRANSACTION_STATUS)
                    dialog.show(activity?.supportFragmentManager,"confirm_status_change")
                }
            }
        })


        transaction_status_logs_see_more.setOnClickListener{
            if(!isTransactionStatusLogsVisible) {
                status_logs_recycler_view?.visibility = View.VISIBLE
                isTransactionStatusLogsVisible = true
                transaction_status_logs_see_more?.text = "See less"
            }
            else{
                status_logs_recycler_view?.visibility = View.GONE
                isTransactionStatusLogsVisible = false
                transaction_status_logs_see_more?.text = "See more"
            }

        }
        fetchTransactionById()
    }

    fun setTransactionStage(stage : Int){

        val message = "Updating transaction status..."

        val headers : HashMap<String,String> = HashMap()
        val authorization = "Authorization ${mSession.token()}"

        headers.put("Authorization", authorization)
        headers.put("Content-Type","application/x-www-form-urlencoded")

        val params : HashMap<String,String> = HashMap()
        params.put("status",transaction_status_text_arraylist[stage])
        Log.d("params",params.toString())
        ApiRequest.put(context,"${API.UPDATE_TRANSACTION_STAGE}/${transaction.id}",message,headers,params,
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("Update",response)
                        current_progress = stage
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        var mActivity = context as MainActivity
                        DialogUtil.showVolleyErrorDialog(mActivity.supportFragmentManager,error)
                    }
                })
    }
    fun displayTransactionDetails(){
        if(context != null) {
            fetchTransactionProducts()
        }

        transaction_recipient_name?.text = "${transaction.consumer_name}"
        if(transaction.tracking_number != "null")  transaction_order_number?.text = "${transaction.tracking_number}"
        else transaction_order_number.text = "Tracking # not available"
        //transaction_shipping_address?.text = "${transaction.address} "
        transaction_total?.text = "P${transaction.total_amount.convertToCurrencyDecimalFormat()}"
        transaction_subtotal?.text = "P${transaction.total_amount.convertToCurrencyDecimalFormat()}"
        if( transaction.comment != "null" && transaction.comment.trim() != ""){
            consumer_additional_notes_layout?.visibility = View.VISIBLE
            consumer_additional_notes?.text = "${transaction.comment} "
        }

        if(transaction.status == "returned upon delivery" || transaction.status == "cancelled"){
            step_view_failed?.visibility = View.VISIBLE
            step_view?.visibility = View.GONE
            if(transaction?.status == "returned upon delivery") step_view_failed?.setSteps(mutableListOf("Order placed","Ready for delivery","Shipped","Returned upon delivery"))
            if(transaction?.status == "cancelled") step_view_failed?.setSteps(mutableListOf("Order placed", "Cancelled"))
            step_view_failed?.go(step_view_failed.stepCount-1,true)
            step_view_failed?.done(true)
            isReturned()
        }
        else{
            step_view?.visibility = View.VISIBLE
            step_view?.go(transaction_status_text_arraylist.indexOf(transaction.status),true)
            if(this@ViewTransactionFragment.view?.isAttachedToWindow == true){
                if(transaction_status_text_arraylist.indexOf(transaction.status) == (step_view.stepCount)-1) step_view?.done(true)
                if(transaction?.status == "shipped") setHasOptionsMenu(true)
            }

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
                        transaction = Transaction(json)
                        displayTransactionDetails()


//                        transaction_recipient_name?.text = "Sold to ${json.optJSONObject("consumer").optString("full_name")}"
//                        if(json.optString("tracking_number") != null) transaction_order_number?.text = "Tracking # ${json.optString("tracking_number","NA")}"
//
//                        transaction_shipping_address?.text = json.optString("address")+" "
//                        if(json.optString("status") == "delivered"){
//                            setHasOptionsMenu(true)
//                        }
//                        else if(json.optString("status") == "returned upon delivery"){
//                            if(context != null) fetchTransactionProducts()
//                            isReturned()
//                            return
//                        }
//                        if(context != null){
//                            fetchTransactionProducts()
//                        }

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
                        val status_logs_json = JSONObject(response).optJSONObject("status_log").optJSONArray("rows")
                        var subtotal_amount : Double = 0.0
                        val t_products : JSONArray = json.optJSONArray("rows")
                        for(i in 0..t_products.length()-1){
                            val t_product = t_products[i] as JSONObject
                            transaction_products_arraylist.add(TransactionProduct(t_product))
                            subtotal_amount += t_product.optDouble("amount")
                        }

                        transaction_products_recyclerview?.adapter = TransactionProductAdapter(transaction_products_arraylist,context)
                        transaction_products_recyclerview?.layoutManager = LinearLayoutManager(context)


                        val status_logs_array_list : ArrayList<StatusLog> = ArrayList()
                        for(i in 0..status_logs_json.length()-1){
                            val status_log = status_logs_json[i] as JSONObject
                            status_logs_array_list.add(StatusLog(status_log))
                        }
                        status_logs_recycler_view?.adapter = StatusLogAdapter(status_logs_array_list,this@ViewTransactionFragment.context)
                        status_logs_recycler_view?.layoutManager = LinearLayoutManager(context)
                        view_transaction_nestedscrollview?.visibility = View.VISIBLE
                        skeleton_layout?.visibility = View.GONE
                        view_transaction_skeleton_shimmer_layout?.visibility = View.GONE
                        view_transaction_skeleton_shimmer_layout?.stopShimmerAnimation()
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {

                    }
                })
    }

    fun isReturned(){
          setHasOptionsMenu(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            CHANGE_TRANSACTION_STATUS -> {
                if(resultCode == Activity.RESULT_OK){
                    if(data != null){
                      if(data.getIntExtra("change_status",0) == 1){
                          if(data.getStringExtra("transaction_status_string") == "returned upon delivery"){
                              isReturned()
                          }
                          else if(data.getStringExtra("transaction_status_string") == "shipped"){
                              setHasOptionsMenu(true)
                          }
                          setTransactionStage(transaction_status_text_arraylist.indexOf(data.getStringExtra("transaction_status_string")))
                          step_view.go(transaction_status_text_arraylist.indexOf(data.getStringExtra("transaction_status_string")),true)
                          if(transaction_status_text_arraylist.indexOf(data.getStringExtra("transaction_status_string")) == step_view.stepCount - 1) step_view.done(true)
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
        fun newInstance(transaction_id : String?): ViewTransactionFragment {
            val fragment = ViewTransactionFragment()
            val args = Bundle()
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
