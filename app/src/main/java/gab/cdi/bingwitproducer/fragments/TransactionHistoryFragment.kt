package gab.cdi.bingwitproducer.fragments


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gab.cdi.bingwitproducer.R
import kotlinx.android.synthetic.main.app_bar_main.*
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.Transaction
import kotlinx.android.synthetic.main.fragment_transaction_history.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TransactionHistoryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TransactionHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionHistoryFragment : Fragment() {

    // TODO: Rename and change types of parameters
    var mPosition: Int? = null
    lateinit var mSession : Session
    var transactions_on_going_arraylist : ArrayList<Transaction> = ArrayList()
    var transactions_delivered_arraylist : ArrayList<Transaction> = ArrayList()
    var transactions_returned_arraylist : ArrayList<Transaction> = ArrayList()

    lateinit var mPagerAdapter : SectionsPagerAdapter
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var transaction_tab_layout : TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPosition = arguments!!.getInt(POSITION)
        }
        mSession = Session(context)
        //fetchTransactions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction_history, container, false)
        activity?.toolbar?.title = "Transactions"
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    fun initUI(){
        container.offscreenPageLimit = 4
        mPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        container.adapter = mPagerAdapter


        container.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(transaction_history_tabs){
        })

        transaction_history_tabs.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(container){

        })

        container.setCurrentItem(mPosition!!)

    }

    fun fetchTransactions() {
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
                    transactions_on_going_arraylist.add(Transaction(transaction_object))
                }
            }
        },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {

                    }
                })
    }

    fun getInnerFragment(key : String) : ViewTransactionsFragment{
        return mPagerAdapter.transaction_fragments_hashmap[key] as ViewTransactionsFragment
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
        mPagerAdapter.transaction_fragments_hashmap.clear()
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
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: Int): TransactionHistoryFragment {
            val fragment = TransactionHistoryFragment()
            val args = Bundle()
            args.putInt(POSITION, param1)
            fragment.arguments = args
            return fragment
        }
    }


    inner class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        val transaction_fragments_hashmap : HashMap<String,Fragment> = HashMap()
        override fun getItem(position: Int): Fragment? {
            var fragment : Fragment? = null
            if(position == 0){
                fragment = ViewTransactionsFragment.newInstance("on-going")
                transaction_fragments_hashmap["on-going"] = fragment
                return fragment
            }
            else if(position == 1){
                fragment = ViewTransactionsFragment.newInstance("delivered")
                transaction_fragments_hashmap["delivered"] = fragment
                return fragment
            }
            else if(position == 2){
                fragment = ViewTransactionsFragment.newInstance("returned")
                transaction_fragments_hashmap["returned"] = fragment
                return fragment
            }
            else if(position == 3){
                fragment = ViewTransactionsFragment.newInstance("cancelled")
                transaction_fragments_hashmap["cancelled"] = fragment
                return fragment
            }


            return ViewTransactionsFragment.newInstance("on-going")
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 4
        }


    }
}// Required empty public constructor
