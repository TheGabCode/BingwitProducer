package gab.cdi.bingwitproducer.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import kotlinx.android.synthetic.main.fragment_enter_password_reset_code_dialog.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EnterPasswordResetCodeDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EnterPasswordResetCodeDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnterPasswordResetCodeDialogFragment : DialogFragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private lateinit var mSession : Session
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        mSession = Session(this@EnterPasswordResetCodeDialogFragment.context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_password_reset_code_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        reset_password_button.setOnClickListener {
            sendPasswordResetCode()
        }
    }

    fun sendPasswordResetCode(){

        val message = "Please wait..."

        val headers : HashMap<String,String> = HashMap()
        headers.put("Content-Type","application/x-www-form-urlencoded")

        val params : HashMap<String,String> = HashMap()
        params.put("password_reset_code",text_reset_code.text.toString().trim())


        ApiRequest.post(this@EnterPasswordResetCodeDialogFragment.context, API.ENTER_PASSWORD_RESET,headers,params,message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        val json = JSONObject(response)
                        Log.d("Enter password reset",response)
                        val uuid = json.getString("link")
                        reset_password_new_password_layout.visibility = View.VISIBLE
                        reset_password_confirm_new_password_layout.visibility = View.VISIBLE
                        reset_password_reset_code_layout.visibility = View.GONE
                        reset_password_button.setOnClickListener(null)
                        reset_password_button.text = "Confirm"
                        reset_password_button.setOnClickListener {
                            updatePassword(uuid)
                        }

                    }

                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Toast.makeText(this@EnterPasswordResetCodeDialogFragment.context,"Errorzzz", Toast.LENGTH_SHORT).show()
                        Log.d("Error ", error.toString())
                    }

                })
    }

    fun updatePassword(uuid : String){
        if(TextUtils.isEmpty(text_new_password.text.toString().trim())){
            reset_password_new_password_layout.error = "Please provide new password."
            return
        }

        if(TextUtils.isEmpty(text_confirm_new_password.text.toString().trim()) || text_confirm_new_password.text.toString().trim() != text_new_password.text.toString().trim()){
            reset_password_confirm_new_password_layout.error = "Passwords do not match, try again."
            return
        }

        val params :HashMap<String,String> = HashMap()
        params.put("password",text_new_password.text.toString().trim())
        params.put("confirm_password",text_confirm_new_password.text.toString().trim())

        val message = "Updating password..."


        ApiRequest.post(this@EnterPasswordResetCodeDialogFragment.context, "${API.RESET_PASSWORD}/$uuid",params, message,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Reset password", response)
                        val json = JSONObject(response)
                        this@EnterPasswordResetCodeDialogFragment.dismiss()
                    }

                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Toast.makeText(this@EnterPasswordResetCodeDialogFragment.context,"Error",Toast.LENGTH_SHORT).show()
                        Log.d("Error ", error.toString())
                        this@EnterPasswordResetCodeDialogFragment.dismiss()
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
         * @return A new instance of fragment EnterPasswordResetCodeDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): EnterPasswordResetCodeDialogFragment {
            val fragment = EnterPasswordResetCodeDialogFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
