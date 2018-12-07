package gab.cdi.bingwitproducer.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony.BaseMmsColumns.TRANSACTION_ID
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.adapters.StatusLogAdapter
import gab.cdi.bingwitproducer.adapters.TransactionProductAdapter
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.StatusLog
import gab.cdi.bingwitproducer.models.Transaction
import gab.cdi.bingwitproducer.models.TransactionProduct
import kotlinx.android.synthetic.main.fragment_transaction_status_logs.*
import kotlinx.android.synthetic.main.fragment_transaction_status_logs.view.*
import kotlinx.android.synthetic.main.fragment_view_transaction_2.*
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TransactionStatusLogsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TransactionStatusLogsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TransactionStatusLogsFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var transaction_id: String? = null
    private var listener: OnFragmentInteractionListener? = null
    lateinit var mSession : Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transaction_id = it.getString(TRANSACTION_ID)
        }
        mSession = Session(context)
    }

    override fun setTitle(): String {
        return ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_status_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
    }

    fun initUI() {
        fetchTransactionProducts()
    }

    fun fetchTransactionProducts(){
        val headers : HashMap<String,String> = HashMap()

        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization", authorization)
        headers.put("Content-Type","application/x-www-form-urlencoded")
        ApiRequest.get(context,"${API.GET_TRANSACTION_PRODUCTS}/${mSession.id()}/transactions/${transaction_id}",headers, HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("t_products",response)
                        val status_logs_mutable_list = mutableListOf<StatusLog>()
                        val status_logs_json = JSONObject(response).optJSONObject("status_log").optJSONArray("rows")
                        val status = JSONObject(response).optJSONObject("transaction").optString("status")
                        for(i in 0..status_logs_json.length()-1){
                            val status_log = status_logs_json[i] as JSONObject
                            val status_log_model_object = StatusLog(status_log)
                            if(i == status_logs_json.length()-1) status_log_model_object.isActive = true
                            status_logs_mutable_list.add(status_log_model_object)
                        }

//                        when(status){
//                            "cancelled" -> status_log_cancelled.visibility = View.VISIBLE
//                            "returned upon delivery" -> status_log_rud.visibility = View.VISIBLE
//                            "delivered" -> status_log_delivered.visibility = View.VISIBLE
//                        }
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {

                    }
                })
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val TRANSACTION_ID = "transaction_id"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionStatusLogsFragment.
         */
        // TODO: Rename and change types and number of parameters


        fun newInstance(transaction_id : String) : TransactionStatusLogsFragment{
            val fragment = TransactionStatusLogsFragment()
            val args = Bundle()
            args.putString(TRANSACTION_ID,transaction_id)
            fragment.arguments = args
            return fragment
        }
    }
}
