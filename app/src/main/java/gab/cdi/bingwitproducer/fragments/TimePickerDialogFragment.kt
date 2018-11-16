package gab.cdi.bingwitproducer.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast

import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.utils.DialogUtil
import kotlinx.android.synthetic.main.fragment_date_picker_dialog.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TimePickerDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TimePickerDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimePickerDialogFragment : DialogFragment() {

    // TODO: Rename and change types of parameters
    private var time_start_end_code: Int? = null
    private var year_month_day_string : String? = null


    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var add_product_auction_startdate_datepicker : DatePicker


    private val INTERVAL = 15


    private val FORMATTER = DecimalFormat("00")

    lateinit var time_picker : TimePicker
    lateinit var minute_picker : NumberPicker
    lateinit var hour_picker : NumberPicker
    var hour : Int? = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    var min : Int? = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            time_start_end_code = arguments!!.getInt(TIME_START_END)
            year_month_day_string = arguments!!.getString(YEAR_MONTH_DAY)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun setMinutePicker() {
        val numValues = 60 / INTERVAL
        val displayedValues = arrayOfNulls<String>(numValues)
        for (i in 0 until numValues) {
            displayedValues[i] = FORMATTER.format(i * INTERVAL)
        }
//        val cal = Calendar.getInstance()
//        val month = cal.get(Calendar.MONTH)+1
//        val day = cal.get(Calendar.DAY_OF_MONTH)
//        val year = cal.get(Calendar.YEAR)
//
//        Toast.makeText(context,"$month $day $year",Toast.LENGTH_SHORT).show()
//
//        val simple_date_format = SimpleDateFormat("yyyy MM dd")
//        val formatted_ymd = simple_date_format.parse(year_month_day_string)
//        val formatted_current = simple_date_format.parse("$year $month $day")
//
//        if(formatted_ymd.compareTo(formatted_current) > 0){
//            val minute = time_picker?.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("minute", "id", "android"))
//            if (minute != null) {
//                minute_picker = minute
//                minute_picker!!.minValue = 0
//                minute_picker!!.maxValue = numValues - 1
//                minute_picker!!.displayedValues = displayedValues
//            }
//            return
//        }
//        else if(formatted_ymd.compareTo(formatted_current) == 0){ //if same day
//            val hour = time_picker?.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("hour","id","android"))
//
//            hour_picker = hour
//            hour_picker!!.minValue = 0
//            hour_picker!!.maxValue = numValues - 1
//            hour_picker.displayedValues = displayedValues
//        }


        //if argument month and day year == current month and day year
        //  remove hour less than current hour
        //  for current hour
        //      only add intervals with (interval - current_minute >= 15)

        val minute = time_picker?.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("minute", "id", "android"))

//        val hour = time_picker?.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("hour","id","android"))
//
//        hour_picker = hour
//        hour_picker!!.minValue = 0
//        hour_picker!!.maxValue = numValues - 1
//        hour_picker.displayedValues = displayedValues
        if (minute != null) {
            minute_picker = minute
            minute_picker!!.minValue = 0
            minute_picker!!.maxValue = numValues - 1
            minute_picker!!.displayedValues = displayedValues
        }
    }

    fun getMinute(): Int {
        return if (minute_picker != null) {
            minute_picker!!.getValue() * INTERVAL
        } else {
            time_picker!!.currentMinute
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_date_picker_dialog, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI(view)
    }

    fun initUI(view : View) {
        when(time_start_end_code){
            6 -> time_picker_title.text = "Start Time"
            7 -> time_picker_title.text = "End Time"
        }

        time_picker = view.findViewById(R.id.add_product_auction_time_picker)
        time_picker_confirm.setOnClickListener {
            val intent = Intent()
            intent.putExtra("hour_minute","${hour}:${min}")
            targetFragment?.onActivityResult(time_start_end_code!!,Activity.RESULT_OK,intent)
            this@TimePickerDialogFragment.dismiss()
        }
        _time_picker_cancel.setOnClickListener {
            this@TimePickerDialogFragment.dismiss()
        }
        setMinutePicker()
        min = getMinute()
        time_picker.setOnTimeChangedListener { view, hourOfDay, minute ->
            hour = hourOfDay
            min = minute*INTERVAL
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
        private val TIME_START_END = "time_start_end"
        private val YEAR_MONTH_DAY = "year_month_day"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimePickerDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(activity_code : Int, year_month_day : String): TimePickerDialogFragment {
            val fragment = TimePickerDialogFragment()
            val args = Bundle()
            args.putInt(TIME_START_END, activity_code)
            args.putString(YEAR_MONTH_DAY, year_month_day)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
