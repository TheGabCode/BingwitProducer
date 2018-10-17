package gab.cdi.bingwitproducer.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.adapters.TransactionAdapter
import gab.cdi.bingwitproducer.dummy.Dummy

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
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = this.arguments
        m_transaction_status = bundle?.getString("transaction_status")
        when(m_transaction_status){
            "on-going" -> {
                Dummy().initDummyTransactionsOnGoing()
                return

            }
            "delivered" -> {
                Dummy().initDummyTransactionsDelivered()
                return

            }
            "returned" -> {
                Dummy().initDummyTransactionsReturned()
                return
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_transactions, container, false)
        var total_sales_layout = view.findViewById<LinearLayout>(R.id.transactions_delivered_total_container)
        transactions_recycler_view = view.findViewById(R.id.transactions_recycler_view)
        transactions_recycler_view.layoutManager = LinearLayoutManager(context)


        when(m_transaction_status){
            "on-going" -> {
                total_sales_layout.visibility = View.GONE
                Dummy().initDummyTransactionsOnGoing()
                transactions_recycler_view.adapter = TransactionAdapter(Dummy.dummy_transactions_ongoing,context)
                return view
            }
            "delivered" -> {
                total_sales_layout.visibility = View.VISIBLE
                Dummy().initDummyTransactionsDelivered()
                transactions_recycler_view.adapter = TransactionAdapter(Dummy.dummy_transactions_delivered,context)
                return view
            }
            "returned" -> {
                total_sales_layout.visibility = View.GONE
                Dummy().initDummyTransactionsReturned()
                transactions_recycler_view.adapter = TransactionAdapter(Dummy.dummy_transactions_returned,context)
                return view
            }
        }


        return view

    }

    fun computeDeliveredSales(){
        var total_sales : Double = 0.0
        Dummy.dummy_transactions_delivered.forEach {

        }
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
