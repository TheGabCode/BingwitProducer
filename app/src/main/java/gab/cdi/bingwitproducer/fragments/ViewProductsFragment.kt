package gab.cdi.bingwitproducer.fragments


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_view_products.*
import kotlinx.android.synthetic.main.fragment_view_products.view.*
import kotlinx.android.synthetic.main.fragment_view_products_tab.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ViewProductsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ViewProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewProductsFragment : Fragment() {

    // TODO: Rename and change types of parameters
    var mPosition : Int? = null
    private var mListener: OnFragmentInteractionListener? = null

    private lateinit var mSession : Session
    lateinit var mActivity : MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPosition = arguments!!.getInt("tab_position")
        }
        mSession = Session(context)
        mActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_products, container, false)
        activity?.toolbar?.title = "Dashboard"
        mActivity.setToolbar(false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
     }

    fun initUI(){
        val m_sections_pager_adapter = SectionsPagerAdapter(fragmentManager)
        products_view_pager.adapter = m_sections_pager_adapter
        products_view_pager.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(producer_products_tabs){})
        producer_products_tabs.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(products_view_pager){})

        products_view_pager.setCurrentItem(mPosition!!)
    }




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
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        private val TAB_POSITION = "tab_position"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewProductsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(tab_position : Int): ViewProductsFragment {
            val fragment = ViewProductsFragment()
            val args = Bundle()
            args.putInt(TAB_POSITION, tab_position)

            fragment.arguments = args
            return fragment
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            var fragment : Fragment? = null
            if(position == 0){
                fragment = ViewProductsTabFragment.newInstance("fixed")
                Log.d("Method","fixed")
                return fragment
            }
            else if(position == 1){
                fragment = ViewProductsTabFragment.newInstance("auction")
                Log.d("Method","auction")
                return fragment
            }
            return null
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 2
        }


    }
}// Required empty public constructor
