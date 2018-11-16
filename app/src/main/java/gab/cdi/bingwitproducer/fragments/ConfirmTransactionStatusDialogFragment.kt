package gab.cdi.bingwitproducer.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window

import gab.cdi.bingwitproducer.R
import kotlinx.android.synthetic.main.fragment_confirm_transaction_status_dialog.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ConfirmTransactionStatusDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ConfirmTransactionStatusDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfirmTransactionStatusDialogFragment : DialogFragment() {

    // TODO: Rename and change types of parameters
    private var transaction_status: String? = null
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            transaction_status = arguments!!.getString(TRANSACTION_STATUS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_confirm_transaction_status_dialog, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog

    }

    fun initUI(){
        transaction_status_change_prompt_message.text = transaction_status_change_prompt_message.text.toString().replace("_status_",transaction_status!!.capitalize(),true)

        val intent = Intent()
        change_transaction_status_cancel.setOnClickListener {
            intent.putExtra("change_status",0)
            targetFragment?.onActivityResult(1, Activity.RESULT_OK,intent)
            this.dismiss()
        }

        change_transaction_status_confirm.setOnClickListener {
            intent.putExtra("change_status",1)
            intent.putExtra("transaction_status_string",transaction_status)
            targetFragment?.onActivityResult(1, Activity.RESULT_OK,intent)
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
        private val TRANSACTION_STATUS = "transaction_status"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfirmTransactionStatusDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(transaction_status: String): ConfirmTransactionStatusDialogFragment {
            val fragment = ConfirmTransactionStatusDialogFragment()
            val args = Bundle()
            args.putString(TRANSACTION_STATUS, transaction_status)

            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
