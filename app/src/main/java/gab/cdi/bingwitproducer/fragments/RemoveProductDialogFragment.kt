package gab.cdi.bingwitproducer.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_remove_product_dialog, container, false)
        initUI(view)
        return view
    }

    fun initUI(view : View){
        var remove_prompt_text = view.findViewById<TextView>(R.id.remove_product_prompt_text)
        var product_name : String = arguments!!.getString("product_name")
        remove_prompt_text.text = remove_prompt_text.text.toString().replace("this_product",product_name)
        remove_product_no_button = view.findViewById(R.id.remove_product_no_button)
        remove_product_yes_button = view.findViewById(R.id.remove_product_yes_button)

        remove_product_no_button.setOnClickListener {
            this.dismiss()
        }

        remove_product_yes_button.setOnClickListener {
            val mActivity = activity as MainActivity
            mActivity.displaySelectedId(R.id.nav_view_products,HashMap())
            this.dismiss()


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
