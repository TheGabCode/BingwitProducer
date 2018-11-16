package gab.cdi.bingwitproducer.fragments

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window

import gab.cdi.bingwitproducer.R
import kotlinx.android.synthetic.main.fragment_custom_alert_dialog.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CustomAlertDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CustomAlertDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomAlertDialogFragment : DialogFragment() {

    // TODO: Rename and change types of parameters
    private var mMessage: String? = null
    private var mDuartion : Long? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mMessage = arguments!!.getString(MESSAGE)
            mDuartion = arguments!!.getLong(DURATION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_alert_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        custom_message_textview.setText(mMessage)

        val dialog_countdown_timer = object : CountDownTimer(mDuartion!!,1000) {
            override fun onFinish() {
                if(this@CustomAlertDialogFragment.view?.isAttachedToWindow == true)
                this@CustomAlertDialogFragment.dismiss()
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }

        dialog_countdown_timer.start()
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
        private val MESSAGE = "message"
        private val DURATION = "duration"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CustomAlertDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(message: String, duration : Long): CustomAlertDialogFragment {
            val fragment = CustomAlertDialogFragment()
            val args = Bundle()
            args.putString(MESSAGE, message)
            args.putLong(DURATION,duration)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
