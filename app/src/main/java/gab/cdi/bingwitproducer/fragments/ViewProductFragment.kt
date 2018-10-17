package gab.cdi.bingwitproducer.fragments


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import douglasspgyn.com.github.circularcountdown.CircularCountdown
import douglasspgyn.com.github.circularcountdown.listener.CircularListener

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dummy.Dummy
import gab.cdi.bingwitproducer.models.Product
import kotlinx.android.synthetic.main.fragment_view_product.*
import kotlinx.android.synthetic.main.fragment_view_product_2.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ViewProductFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ViewProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewProductFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var product_id : String? = null
    private lateinit var edit_product_button : Button
    private lateinit var remove_product_button : Button
    private lateinit var view_product_price : TextView
    private lateinit var view_product_product_name : TextView
    private lateinit var product_image : ImageView
    private lateinit var auction_timer : CircularCountdown
    private lateinit var view_product_product_type  : TextView
    private lateinit var view_product_product_stock : TextView
    private lateinit var view_product_product_selling_method : TextView
    private lateinit var auction_timer_background : View
    private lateinit var mActivity : MainActivity

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            product_id = arguments!!.getString(PRODUCT_ID)
        }
        mActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_product_2, container, false)
       initUI(view)
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun initUI(view : View){
        edit_product_button = view.findViewById(R.id.edit_product_button)
        remove_product_button = view.findViewById(R.id.remove_product_button)
        view_product_price = view.findViewById(R.id.view_product_price)
        view_product_product_name = view.findViewById(R.id.view_product_product_name)
        product_image = view.findViewById(R.id.product_image)
        auction_timer = view.findViewById(R.id.auction_timer)
        view_product_product_type = view.findViewById(R.id.view_product_product_type)
        view_product_product_stock = view.findViewById(R.id.view_product_product_stock)
        view_product_product_selling_method = view.findViewById(R.id.view_product_product_selling_method)
        auction_timer_background = view.findViewById(R.id.auction_timer_background)





        val this_product : Product = Dummy.dummy_products_hashmap[product_id] as Product
        view_product_product_name.text = this_product.product_name
        view_product_price.text = "PHP ${this_product.product_price}/Kg"
        view_product_product_type.text = this_product.product_type
        view_product_product_stock.text = "${this_product.product_weight}Kg"

        if(this_product.product_selling_method == 1){
            view_product_price.text = "PHP ${this_product.product_price}/Kg"
            view_product_product_selling_method.text = "Fixed price"
            auction_timer_background.visibility = View.INVISIBLE
            auction_timer.visibility = View.INVISIBLE
        }
        else{
            view_product_price.text = "PHP ${this_product.product_price}"
            view_product_product_selling_method.text = "Placed for auction"
            var start_auction_price = this_product.product_price
            val decrement_auction_price = 1.0 //compute later
            auction_timer.create(0, 3600, CircularCountdown.TYPE_SECOND)
                    .listener(object : CircularListener {
                        override fun onTick(progress: Int) {
                            view_product_price.text = "PHP ${(start_auction_price - decrement_auction_price)}"
                            start_auction_price -= 1.0
                        }

                        override fun onFinish(newCycle: Boolean, cycleCount: Int) {

                        }
                    })
                    .start()
        }


        edit_product_button.setOnClickListener {
            val params : HashMap<String,Any> = HashMap()
            params.put("product_id",product_id!!)
            mActivity.displaySelectedId(R.id.nav_edit_product,params)
        }

        remove_product_button.setOnClickListener {
            val remove_product_dialog = RemoveProductDialogFragment()
            var bundle = Bundle()
            bundle.putString("product_name",this_product.product_name)
            remove_product_dialog.arguments = bundle
            remove_product_dialog.show(activity?.supportFragmentManager,"remove_product")
        }


//        edit_product_button = view.findViewById(R.id.edit_product_button)
//        remove_product_button = view.findViewById(R.id.remove_product_button)
//
//        var product_name = view.findViewById<TextView>(R.id.product_name)
//        var product_type = view.findViewById<TextView>(R.id.product_type)
//        var product_weight = view.findViewById<TextView>(R.id.product_weight)
//        var product_price = view.findViewById<TextView>(R.id.product_price_per_kilogram)
//        var product_selling_method = view.findViewById<TextView>(R.id.product_selling_method)
//        Dummy().initDummyProducts()
//        var this_product : Product = Dummy.dummy_products_hashmap[product_id] as Product
//        product_name.text = this_product.product_name
//        product_type.text = this_product.product_type
//        product_weight.text = this_product.product_weight.toString() + " Kg"
//        if(this_product.product_selling_method == 1){
//            product_price.text = this_product.product_price.toString() + "PHP/Kg"
//            product_selling_method.text = "Fixed price"
//        }
//        else{
//            product_price.text = this_product.product_price.toString() + "PHP"
//            product_selling_method.text = "Placed for auction"
//        }
//
//        var start_auction_price = this_product.product_price
//        var decrement_auction_price = 1.0 //compute later
//
//        edit_product_button.setOnClickListener {
//            val params : HashMap<String,Any> = HashMap()
//            params.put("product_id",product_id!!)
//            mActivity.displaySelectedId(R.id.nav_edit_product,params)
//        }
//
//        remove_product_button.setOnClickListener {
//            val remove_product_dialog = RemoveProductDialogFragment()
//            var bundle = Bundle()
//            bundle.putString("product_name",this_product.product_name)
//            remove_product_dialog.arguments = bundle
//            remove_product_dialog.show(activity?.supportFragmentManager,"remove_product")
//        }
//        val auction_timer : CircularCountdown = view.findViewById(R.id.auction_timer)
//        auction_timer.create(0, 3600, CircularCountdown.TYPE_SECOND)
//                .listener(object : CircularListener {
//                    override fun onTick(progress: Int) {
//                        product_price.text = (start_auction_price - decrement_auction_price).toString()
//                        start_auction_price -= 1.0
//                    }
//
//                    override fun onFinish(newCycle: Boolean, cycleCount: Int) {
//
//                    }
//                })
//                .start()
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
        private val PRODUCT_ID = "product_id"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(product_id: String): ViewProductFragment {
            val fragment = ViewProductFragment()
            val args = Bundle()
            args.putString(PRODUCT_ID, product_id)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
