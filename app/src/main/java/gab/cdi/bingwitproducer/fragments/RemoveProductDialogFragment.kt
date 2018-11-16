package gab.cdi.bingwitproducer.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.utils.DialogUtil
import kotlinx.android.synthetic.main.app_bar_main.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RemoveProductDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RemoveProductDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RemoveProductDialogFragment : DialogFragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var remove_product_yes_button : Button
    private lateinit var remove_product_no_button : Button
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var mSession : Session
    private lateinit var product_id : String
    private lateinit var product_type : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        mSession = Session(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_remove_product_dialog, container, false)
        initUI(view)
        return view
    }

    fun initUI(view : View){
        val remove_prompt_text = view.findViewById<TextView>(R.id.remove_product_prompt_text)
        val product_name : String = arguments!!.getString("product_name")
        product_id = arguments!!.getString("product_id")
        product_type = arguments!!.getString("product_type")
        remove_prompt_text.text = remove_prompt_text.text.toString().replace("this_product",product_name)
        remove_product_no_button = view.findViewById(R.id.remove_product_no_button)
        remove_product_yes_button = view.findViewById(R.id.remove_product_yes_button)

        remove_product_no_button.setOnClickListener {
            this.dismiss()
        }

        remove_product_yes_button.setOnClickListener {
            when(product_type){
                "fixed" -> deleteProductById()

                "auction" -> deleteAuctionProductById()
            }




        }
    }

    fun deleteProductById(){
        val message = "Removing..."

        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization",authorization)
        Log.d("User id",mSession.id())
        Log.d("token",mSession.token())
        ApiRequest.delete(context, "${API.DELETE_PRODUCT_BY_ID}products/$product_id",message,headers,object : ApiRequest.URLCallback{
            override fun didURLResponse(response: String) {
                Log.d("Delete",response)
                val mActivity = activity as MainActivity
                mActivity.fm.popBackStackImmediate()
                mActivity.fab.show()
                val mFragment = mActivity.supportFragmentManager.findFragmentById(R.id.bingwit_navigation_activity) as ViewProductsFragment
                mFragment.mPosition = 0
                mFragment.initUI()
//                mActivity.fragmentReplaceBackStack(ViewProductsFragment.newInstance(0),"remove_product")
//                mActivity.displaySelectedId(R.id.nav_view_products, hashMapOf("tab_position" to 0))
                this@RemoveProductDialogFragment.dismiss()
            }
        },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                        this@RemoveProductDialogFragment.dismiss()
                    }
                })
    }

    fun deleteAuctionProductById(){
        val message = "Removing..."

        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization",authorization)
        Log.d("User id",mSession.id())
        Log.d("token",mSession.token())
        ApiRequest.delete(context, "${API.DELETE_AUCTION}/$product_id",message,headers,object : ApiRequest.URLCallback{
            @SuppressLint("RestrictedApi")
            override fun didURLResponse(response: String) {
                Log.d("Delete",response)
                val mActivity = activity as MainActivity
                mActivity.fm.popBackStackImmediate()
                mActivity.fab.show()
                val mFragment = mActivity.supportFragmentManager.findFragmentById(R.id.bingwit_navigation_activity) as ViewProductsFragment
                mFragment.mPosition = 1
                mFragment.initUI()
                //mActivity.displaySelectedId(R.id.nav_view_products, hashMapOf("tab_position" to 1))
               // mActivity.fragmentReplaceBackStack(ViewProductsFragment.newInstance(1),"remove_auction_product")
                this@RemoveProductDialogFragment.dismiss()
            }
        },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        this@RemoveProductDialogFragment.dismiss()
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
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RemoveProductDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): RemoveProductDialogFragment {
            val fragment = RemoveProductDialogFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
