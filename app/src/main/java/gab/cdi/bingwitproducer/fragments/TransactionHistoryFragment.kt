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
import android.widget.Toast


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
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var transaction_tab_layout : TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPosition = arguments!!.getInt(POSITION)

        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_transaction_history, container, false)
        initUI(view)
        activity?.toolbar?.title = "Transaction History"
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun initUI(view : View){
        transaction_tab_layout = view.findViewById(R.id.transaction_history_tabs)
        var transactions_view_pager : ViewPager = view.findViewById(R.id.container)
        val mPagerAdapter = SectionsPagerAdapter(fragmentManager)
        transactions_view_pager.adapter = mPagerAdapter


        transactions_view_pager.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(transaction_tab_layout){

        })

        transaction_tab_layout.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(transactions_view_pager){

        })

        transactions_view_pager.setCurrentItem(mPosition!!)



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


    inner class SectionsPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            var fragment : Fragment? = null
            if(position == 0){
                fragment = ViewTransactionsFragment()
                val bundle = Bundle()
                bundle.putString("transaction_status","on-going")
                fragment.arguments = bundle
                return fragment
            }
            else if(position == 1){
                fragment = ViewTransactionsFragment()
                val bundle = Bundle()
                bundle.putString("transaction_status","delivered")
                fragment.arguments = bundle
                return fragment
            }
            else if(position == 2){
                fragment = ViewTransactionsFragment()
                val bundle = Bundle()
                bundle.putString("transaction_status","returned")
                fragment.arguments = bundle
                return fragment
            }
            else if(position == 3){
                fragment = RatingsFragment()

                return fragment
            }


            return null
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 4
        }


    }
}// Required empty public constructor
