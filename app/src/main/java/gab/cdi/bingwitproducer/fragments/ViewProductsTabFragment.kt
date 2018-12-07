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
import com.android.volley.VolleyError

import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.adapters.ProducerAuctionProductAdapter
import gab.cdi.bingwitproducer.adapters.ProducerProductAdapter
import gab.cdi.bingwitproducer.adapters.SkeletonAdapter
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.ProducerAuctionProduct
import gab.cdi.bingwitproducer.models.ProducerProduct
import gab.cdi.bingwitproducer.utils.DialogUtil
import gab.cdi.bingwitproducer.utils.SkeletonUtil
import gab.cdi.bingwitproducer.utils.TimeUtil
import kotlinx.android.synthetic.main.fragment_view_products_tab.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ViewProductsTabFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ViewProductsTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewProductsTabFragment : Fragment() {

    // TODO: Rename and change types of parameters
    var m_product_selling_method: String? = null

    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var mSession : Session

    var fixed_products_array_list : ArrayList<ProducerProduct> = ArrayList()
    var auction_products_array_list : ArrayList<ProducerAuctionProduct> = ArrayList()

    lateinit var product_placement_recyclerview : RecyclerView

    var fixed_products_adapter : ProducerProductAdapter? = null
    var auction_products_adapter : ProducerAuctionProductAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            m_product_selling_method = arguments!!.getString(PRODUCT_SELLING_METHOD)
        }
        mSession = Session(context)
//        when(m_product_selling_method){
//            "fixed" -> fixed_products_adapter = ProducerProductAdapter(fixed_products_array_list,context,m_product_selling_method!!)
//            "auction" ->  auction_products_adapter = ProducerAuctionProductAdapter(auction_products_array_list,context,m_product_selling_method!!)
//        }


        //
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_products_tab, container, false)
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
        product_placement_recyclerview = view.findViewById(R.id.product_placement_recyclerview)
        skeleton_recyclerview?.adapter = SkeletonAdapter(SkeletonUtil.getSkeletonCount(context!!),context,R.layout.product_list_item_skeleton_layout)
        skeleton_recyclerview?.layoutManager = LinearLayoutManager(context)
        shimmer_layout?.startShimmerAnimation()
        initUI()
    }



    fun initUI(){
        when(m_product_selling_method){
            "fixed" -> fetchProducerProducts()


            "auction" -> fetchAuctionProducerProducts()
        }

        view_products_refresh_layout.setOnRefreshListener {
            when(m_product_selling_method){
                "fixed" -> fetchProducerProducts()

                "auction" -> fetchAuctionProducerProducts()
            }
        }
        product_placement_recyclerview.layoutManager = LinearLayoutManager(context)
    }

    fun fetchProducerProducts() {
        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization",authorization)
        Log.d("Products","fixed")

        ApiRequest.get(context, "${API.GET_PRODUCTS}/${mSession.id()}/products",headers, HashMap(),object : ApiRequest.URLCallback{
            override fun didURLResponse(response: String) {
                Log.d("response",response)
                fixed_products_array_list.clear()
                val json = JSONObject(response)
                val products : JSONArray = json.getJSONObject("product").getJSONArray("rows")
                for (i in 0..products.length()-1){
                    val product_object : JSONObject = products.get(i) as JSONObject
                    fixed_products_array_list.add(ProducerProduct(product_object))
                }
                fixed_products_adapter?.notifyDataSetChanged()
                fixed_products_adapter = ProducerProductAdapter(fixed_products_array_list,context,m_product_selling_method!!)
                product_placement_recyclerview.adapter = fixed_products_adapter
                if(view_products_refresh_layout.isRefreshing){
                    view_products_refresh_layout.isRefreshing = false
                }
                skeleton_recyclerview?.visibility = View.GONE
            }
        },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                    }
                })
    }

    fun fetchAuctionProducerProducts() {
        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization",authorization)

        Log.d("Products","auction")
        ApiRequest.get(context, "${API.GET_PRODUCTS}/${mSession.id()}/products/auctions",headers, HashMap(),object : ApiRequest.URLCallback{
            override fun didURLResponse(response: String) {
                auction_products_array_list.clear()
                Log.d("Tag",response)
                val json = JSONObject(response)
                val products = json.optJSONObject("product").optJSONArray("rows")
                for (i in 0..products.length()-1){
                    val product_object : JSONObject = products.get(i) as JSONObject
                    val product_auction = ProducerAuctionProduct(product_object)
                    if(System.currentTimeMillis() < TimeUtil.convertDateStringToLong(product_auction.end)){
                        auction_products_array_list.add(product_auction)
                    }

                }
                auction_products_adapter?.notifyDataSetChanged()
                auction_products_adapter = ProducerAuctionProductAdapter(auction_products_array_list,context,m_product_selling_method!!)
                product_placement_recyclerview.adapter = auction_products_adapter
                if(view_products_refresh_layout?.isRefreshing == true){
                    view_products_refresh_layout?.isRefreshing = false
                }
                skeleton_recyclerview?.visibility = View.GONE
                shimmer_layout?.stopShimmerAnimation()
            }
        },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                    }
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
        private val PRODUCT_SELLING_METHOD = "product_selling_method"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewProductsTabFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(product_selling_method: String): ViewProductsTabFragment {
            val fragment = ViewProductsTabFragment()
            val args = Bundle()
            args.putString(PRODUCT_SELLING_METHOD, product_selling_method)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
