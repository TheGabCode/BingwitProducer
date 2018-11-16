package gab.cdi.bingwitproducer.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.adapters.TransactionAdapter
import gab.cdi.bingwitproducer.dummy.Dummy
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.Transaction
import gab.cdi.bingwitproducer.utils.DialogUtil
import kotlinx.android.synthetic.main.fragment_view_transactions.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ViewTransactionsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ViewTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewTransactionsFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var m_transaction_status: String? = null
    private lateinit var transactions_recycler_view : RecyclerView
    var transactions_on_going_arraylist : ArrayList<Transaction> = ArrayList()
    var transactions_delivered_arraylist : ArrayList<Transaction> = ArrayList()
    var transactions_returned_arraylist : ArrayList<Transaction> = ArrayList()
    private var mListener: OnFragmentInteractionListener? = null
    lateinit var mSession : Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSession = Session(context)
        var bundle = this.arguments
        m_transaction_status = bundle?.getString("transaction_status")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactions_recycler_view = view.findViewById(R.id.transactions_recycler_view)
        transactions_recycler_view.layoutManager = LinearLayoutManager(context)
        transactions_refresh_layout.setOnRefreshListener {
            fetchTransactions(m_transaction_status!!)
        }
        fetchTransactions(m_transaction_status!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_transactions, container, false)

        return view

    }

    fun fetchTransactions() {
        transactions_on_going_arraylist.clear()
        transactions_delivered_arraylist.clear()
        transactions_returned_arraylist.clear()

        val headers : HashMap<String,String> = HashMap()
        headers.put("Content-Type","application/x-www-form-urlencoded")

        val authorization = "Bearer ${mSession.token()}"
        headers.put("Authorization", authorization)


        ApiRequest.get(context, "${API.GET_TRANSACTIONS}/${mSession.id()}/transactions",headers, HashMap(),object : ApiRequest.URLCallback{
            override fun didURLResponse(response: String) {
                Log.d("Transactions",response)
                val json : JSONArray = JSONObject(response).optJSONObject("transaction").optJSONArray("rows")

                for(i in 0..json.length()-1) {
                    val transaction_object = json[i] as JSONObject
                    if(transaction_object.optString("status") == "order placed" || transaction_object.optString("status") == "ready for delivery" || transaction_object.optString("status") == "shipped"){
                        transactions_on_going_arraylist.add(Transaction(transaction_object))
                    }

                    if(transaction_object.optString("status") == "delivered"){
                        transactions_delivered_arraylist.add(Transaction(transaction_object))
                    }

                    if(transaction_object.optString("status") == "returned upon delivery"){
                        transactions_returned_arraylist.add(Transaction(transaction_object))
                    }

                }


                when(m_transaction_status){
                    "on-going" -> {
                        val on_going_transaction_adapter = TransactionAdapter(transactions_on_going_arraylist,context)
                        on_going_transaction_adapter.notifyDataSetChanged()
                        transactions_recycler_view.adapter = on_going_transaction_adapter
                    }
                    "delivered" -> {
                        val delivered_transaction_adapter = TransactionAdapter(transactions_delivered_arraylist,context)
                        delivered_transaction_adapter.notifyDataSetChanged()
                        transactions_recycler_view.adapter =  delivered_transaction_adapter

                    }
                    "returned" -> {
                        val returned_transaction_adapter = TransactionAdapter(transactions_returned_arraylist,context)
                        returned_transaction_adapter.notifyDataSetChanged()
                        transactions_recycler_view.adapter = returned_transaction_adapter

                    }
                }
                transactions_recycler_view.setHasFixedSize(true)
                if(transactions_refresh_layout.isRefreshing){
                    transactions_refresh_layout.isRefreshing = false
                }
            }

        },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                    }
                })
    }

    fun fetchTransactions(status : String) {
        val headers : HashMap<String,String> = HashMap()
        headers.put("Content-Type","application/x-www-form-urlencoded")

        val authorization = "Bearer ${mSession.token()}"
        headers.put("Authorization", authorization)


        ApiRequest.get(context, "${API.GET_TRANSACTIONS}/${mSession.id()}/transactions",headers, HashMap(),object : ApiRequest.URLCallback{
            override fun didURLResponse(response: String) {
                Log.d("Transactions",response)
                val json : JSONArray = JSONObject(response).optJSONObject("transaction").optJSONArray("rows")
                    when(status) {
                        "on-going" -> {
                            transactions_on_going_arraylist.clear()
                            for (i in 0..json.length() - 1) {
                                val transaction_object = json[i] as JSONObject
                                if (transaction_object.optString("status") == "order placed" || transaction_object.optString("status") == "ready for delivery" || transaction_object.optString("status") == "shipped") {
                                    transactions_on_going_arraylist.add(Transaction(transaction_object))
                                }
                                val on_going_transaction_adapter = TransactionAdapter(transactions_on_going_arraylist, context)
                                on_going_transaction_adapter.notifyDataSetChanged()
                                transactions_recycler_view.adapter = on_going_transaction_adapter
                            }
                        }

                        "delivered" -> {
                            transactions_delivered_arraylist.clear()
                            for(i in 0..json.length()-1){
                                val transaction_object = json[i] as JSONObject
                                if(transaction_object.optString("status") == "delivered"){
                                    transactions_delivered_arraylist.add(Transaction(transaction_object))
                                }
                            }
                            val delivered_transaction_adapter = TransactionAdapter(transactions_delivered_arraylist,context)
                            delivered_transaction_adapter.notifyDataSetChanged()
                            transactions_recycler_view.adapter =  delivered_transaction_adapter

                        }

                        "returned" -> {
                            transactions_returned_arraylist.clear()
                            for(i in 0..json.length()-1){
                                val transaction_object = json[i] as JSONObject
                                if(transaction_object.optString("status") == "returned upon delivery"){
                                    transactions_returned_arraylist.add(Transaction(transaction_object))
                                }
                            }
                            val returned_transaction_adapter = TransactionAdapter(transactions_returned_arraylist,context)
                            returned_transaction_adapter.notifyDataSetChanged()
                            transactions_recycler_view.adapter = returned_transaction_adapter

                        }

                    }

                transactions_recycler_view.setHasFixedSize(true)
                if(transactions_refresh_layout.isRefreshing){
                    transactions_refresh_layout.isRefreshing = false
                }
            }

        },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                    }
                })
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
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
        private val TRANSACTION_STATUS = "transaction_status"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewTransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(transaction_status: String): ViewTransactionsFragment {
            val fragment = ViewTransactionsFragment()
            val args = Bundle()
            args.putString(TRANSACTION_STATUS, transaction_status)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
