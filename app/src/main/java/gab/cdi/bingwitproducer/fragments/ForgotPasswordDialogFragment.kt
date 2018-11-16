package gab.cdi.bingwitproducer.fragments

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
import android.widget.EditText
import android.widget.Toast
import com.android.volley.VolleyError

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.utils.DialogUtil
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ForgotPasswordDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ForgotPasswordDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordDialogFragment : DialogFragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null


    private var mListener: OnFragmentInteractionListener? = null

    private lateinit var forgot_password_phone_number : EditText
    private lateinit var forgot_password_cancel_button : Button
    private lateinit var forgot_password_submit_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password_dialog, container, false)
        initUI((view))
        return view
    }

    fun initUI(view : View){
        forgot_password_cancel_button = view.findViewById(R.id.forgot_password_cancel_button)
        forgot_password_submit_button = view.findViewById(R.id.forgot_password_submit_button)

        forgot_password_phone_number = view.findViewById(R.id.forgot_password_phone_number)

        forgot_password_cancel_button.setOnClickListener {
            this.dismiss()
        }
        forgot_password_submit_button.setOnClickListener {
            if(!forgot_password_phone_number.text.toString().trim().matches("^(09|\\+639)\\d{9}\$".toRegex())){
                DialogUtil.showErrorDialog(this@ForgotPasswordDialogFragment.activity!!.supportFragmentManager,2000,"Wrong number format")
            }
            else{
                var phone_number_text_input = forgot_password_phone_number.text.toString().trim()
                var formatted_phone_number = phone_number_text_input
                if(phone_number_text_input.startsWith("09")) {
                    formatted_phone_number = StringBuilder("+639${phone_number_text_input.substring(2)}").toString()
                }

                sendPasswordResetCode(formatted_phone_number)
            }

        }
    }

    fun sendPasswordResetCode(formatted_phone_number : String){
        val header : HashMap<String,String> = HashMap()
        header.put("Content-Type","application/x-www-form-urlencoded")

        val params :  HashMap<String,String> = HashMap()
        params.put("contact_number",formatted_phone_number)

        val message = "Requesting for password reset code"
        ApiRequest.put(this@ForgotPasswordDialogFragment.context, API.SEND_RESET_PASSWORD,message,header,params,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Change password", response)
                        Log.d("contact_number",formatted_phone_number)
                        val enter_reset_code_dialog = EnterPasswordResetCodeDialogFragment()
                        enter_reset_code_dialog.show(this@ForgotPasswordDialogFragment.activity?.supportFragmentManager,"enter_reset_code")
                        this@ForgotPasswordDialogFragment.dismiss()
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Log.d("Error ", error.toString())
                        DialogUtil.showVolleyErrorDialog(this@ForgotPasswordDialogFragment.activity?.supportFragmentManager!!,error)
                        this@ForgotPasswordDialogFragment.dismiss()
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
         * @return A new instance of fragment ForgotPasswordDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ForgotPasswordDialogFragment {
            val fragment = ForgotPasswordDialogFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
