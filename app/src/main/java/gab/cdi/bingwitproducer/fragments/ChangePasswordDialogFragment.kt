package gab.cdi.bingwitproducer.fragments

import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.activities.RegistrationVerificationActivity
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import kotlinx.android.synthetic.main.fragment_change_password_dialog.*
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChangePasswordDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ChangePasswordDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePasswordDialogFragment : DialogFragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var mSession : Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        mSession = Session(context)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_change_password_dialog, container, false)
        initUI(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        change_password_cancel_button.setOnClickListener {
            this.dismiss()
        }

        change_password_confirm_button.setOnClickListener {
            submitChangePassword()

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog

    }
    fun initUI(view : View){


    }

    fun submitChangePassword() {
        val params : HashMap<String,String> = HashMap()

        params.put("password",change_password_old_password.text.toString().trim())
        params.put("new_password",change_password_new_password.text.toString().trim())
        params.put("confirm_new_password",change_password_confirm_new_password.text.toString().trim())

        val header : HashMap<String,String> = HashMap()

        val authorization = "Bearer ${mSession.token()}"
        header.put("Content-Type","application/x-www-form-urlencoded")
        header.put("Authorization",authorization)

        ApiRequest.put(this@ChangePasswordDialogFragment.context, API.CHANGE_PASSWORD,header,params,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("Change password", response)
                        this@ChangePasswordDialogFragment.dismiss()
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Log.d("Error ", error.toString())
                        val body = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val error_message = JSONObject(body).getJSONObject("err").getString("message")
                        val dialog = CustomAlertDialogFragment.newInstance(error_message)
                        dialog.show(this@ChangePasswordDialogFragment.activity?.supportFragmentManager,"change_password_error")
                        this@ChangePasswordDialogFragment.dismiss()
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
         * @return A new instance of fragment ChangePasswordDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ChangePasswordDialogFragment {
            val fragment = ChangePasswordDialogFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
