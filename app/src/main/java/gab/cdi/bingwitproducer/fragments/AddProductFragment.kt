package gab.cdi.bingwitproducer.fragments


import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gab.cdi.bingwitproducer.R
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog

import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.widget.*
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_date_picker_dialog.*
import java.sql.Time
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddProductFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddProductFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    private lateinit var selling_method_radio_group : RadioGroup
    private lateinit var add_product_price_per_kilogram : TextInputLayout
    private lateinit var add_product_auction_price_range : LinearLayout
    private lateinit var add_product_auction_start_date_constraint_layout :ConstraintLayout
    private lateinit var add_product_auction_end_date_constraint_layout :ConstraintLayout
    private lateinit var add_product_auction_start_date_text_input : TextInputEditText
    private lateinit var add_product_auction_end_date_text_input : TextInputEditText

    private var add_product_auction_start_date_date : Date? = null
    private var add_product_auction_end_date_date : Date? = null


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
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        activity?.toolbar?.title = "Add a Product"
        initUI(view)
        return view
    }

    fun initUI(view : View) {
        selling_method_radio_group = view.findViewById(R.id.add_product_selling_method_radiogroup)
        add_product_price_per_kilogram = view.findViewById(R.id.add_product_price_per_kilogram)
        add_product_auction_price_range = view.findViewById(R.id.add_product_auction_price_range)
        add_product_auction_start_date_constraint_layout = view.findViewById(R.id.add_product_auction_start_date_constraint_layout)
        add_product_auction_end_date_constraint_layout = view.findViewById(R.id.add_product_auction_end_date_constraint_layout)
        selling_method_radio_group.check(R.id.add_product_selling_method_fixed)
        add_product_auction_start_date_text_input = view.findViewById(R.id.add_product_auction_start_date_text_input)
        add_product_auction_end_date_text_input = view.findViewById(R.id.add_product_auction_end_date_text_input)
        when(selling_method_radio_group.checkedRadioButtonId){
            R.id.add_product_selling_method_fixed -> {
                add_product_price_per_kilogram.visibility = View.VISIBLE
                add_product_auction_price_range.visibility = View.GONE
                add_product_auction_start_date_constraint_layout.visibility = View.GONE
                add_product_auction_end_date_constraint_layout.visibility = View.GONE
            }
            R.id.add_product_selling_method_auction -> {
                add_product_price_per_kilogram.visibility = View.GONE
                add_product_auction_price_range.visibility = View.VISIBLE
                add_product_auction_start_date_constraint_layout.visibility = View.VISIBLE
                add_product_auction_end_date_constraint_layout.visibility = View.VISIBLE
            }
        }
        selling_method_radio_group.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.add_product_selling_method_fixed -> {
                    add_product_price_per_kilogram.visibility = View.VISIBLE
                    add_product_auction_price_range.visibility = View.GONE
                    add_product_auction_start_date_constraint_layout.visibility = View.GONE
                    add_product_auction_end_date_constraint_layout.visibility = View.GONE
                }
                R.id.add_product_selling_method_auction -> {
                    add_product_price_per_kilogram.visibility = View.GONE
                    add_product_auction_price_range.visibility = View.VISIBLE
                    add_product_auction_start_date_constraint_layout.visibility = View.VISIBLE
                    add_product_auction_end_date_constraint_layout.visibility = View.VISIBLE
                }
            }
        }


        val array = Arrays.asList("Tilapia", "Bangus")
//        sp_product.adapter = ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,array )

        var simple_date_format : SimpleDateFormat = SimpleDateFormat("d M yyyy HH:mm")

        add_product_auction_start_date_text_input.setOnClickListener {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val hour_of_day = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val minute = Calendar.getInstance().get(Calendar.MINUTE)
            val date_listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val time_listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val date_string = dayOfMonth.toString() + " " + (month+1).toString() + " " + year.toString() + " " + hourOfDay.toString() + ":" + minute.toString()
                    val date_string_result = simple_date_format.parse(date_string)
                    add_product_auction_start_date_date = date_string_result
                    if(add_product_auction_end_date_date != null){
                        val date_diff = add_product_auction_start_date_date!!.compareTo(add_product_auction_end_date_date)
                        if(date_diff < 0){
                            add_product_auction_start_date_text_input.setText(dayOfMonth.toString() + " " + (month+1).toString() + " " + year.toString() + " - " + hourOfDay.toString() + ":" + minute.toString())
                        }
                        else if(date_diff > 0 || date_diff == 0){
                            Toast.makeText(context,"Enter date and time before auction's end date",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        val current_date_time = Calendar.getInstance().time
                        if(add_product_auction_start_date_date!!.compareTo(current_date_time) < 0){
                            Toast.makeText(context,"Enter date and time after current date and time",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            add_product_auction_start_date_text_input.setText(dayOfMonth.toString() + " " + (month+1).toString() + " " + year.toString() + " - " + hourOfDay.toString() + ":" + minute.toString())
                        }
                    }
//                    Toast.makeText(context,date_string_result.toString(),Toast.LENGTH_SHORT).show()
                    }
                val time_picker = TimePickerDialog(context,0,time_listener,hour_of_day,minute,false)
                time_picker.show()

            }
            val add_product_auction_start_date_datepicker = DatePickerDialog(context,date_listener,year,month,day)
            add_product_auction_start_date_datepicker.show()
        }

        add_product_auction_end_date_text_input.setOnClickListener {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val hour_of_day = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val minute = Calendar.getInstance().get(Calendar.MINUTE)
            val date_listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val time_listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val date_string = dayOfMonth.toString() + " " + (month+1).toString() + " " + year.toString() + " " + hourOfDay.toString() + ":" + minute.toString()
                    val date_string_result = simple_date_format.parse(date_string)
                    add_product_auction_end_date_date = date_string_result
                    if(add_product_auction_start_date_date != null){
                        val date_diff = add_product_auction_end_date_date!!.compareTo(add_product_auction_start_date_date)
                        if(date_diff > 0){
                            add_product_auction_end_date_text_input.setText(dayOfMonth.toString() + " " + (month+1).toString() + " " + year.toString() + " - " + hourOfDay.toString() + ":" + minute.toString())
                        }
                        else if(date_diff < 0 || date_diff == 0){
                            Toast.makeText(context,"Enter date and time after auction's start date",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        val current_date_time = Calendar.getInstance().time
                        if(add_product_auction_end_date_date!!.compareTo(current_date_time) < 0){
                            Toast.makeText(context,"Enter date and time after current date and time",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            add_product_auction_end_date_text_input.setText(dayOfMonth.toString() + " " + (month+1).toString() + " " + year.toString() + " - " + hourOfDay.toString() + ":" + minute.toString())
                        }
                    }
                }
                val time_picker = TimePickerDialog(context,0,time_listener,hour_of_day,minute,false)
                time_picker.show()

            }
            val add_product_auction_enddate_picker = DatePickerDialog(context,date_listener,year,month,day)
            add_product_auction_enddate_picker.show()

        }
    }

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
         * @return A new instance of fragment AddProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): AddProductFragment {
            val fragment = AddProductFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
