package gab.cdi.bingwitproducer.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.Editable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.dummy.Dummy
import gab.cdi.bingwitproducer.models.Product
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_edit_product.*
import org.w3c.dom.Text
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EditProductFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProductFragment : Fragment() {
    var product_id : String? = null

    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var edit_product_name : TextInputEditText
    private lateinit var edit_product_weight : TextInputEditText
    private lateinit var edit_product_selling_method_radiogroup : RadioGroup
    private lateinit var edit_product_product_price : TextInputEditText
    private lateinit var edit_product_price_per_kilogram_layout : TextInputLayout
    private lateinit var edit_product_auction_price_range_layout : LinearLayout
    private lateinit var edit_product_min_price : TextInputEditText
    private lateinit var edit_product_max_price : TextInputEditText
    private lateinit var edit_product_auction_start_date_constraint_layout : ConstraintLayout
    private lateinit var edit_product_auction_end_date_constraint_layout : ConstraintLayout
    private lateinit var edit_product_auction_start_date_icon : ImageView
    private lateinit var edit_product_auction_end_date_icon : ImageView
    private lateinit var edit_product_auction_start_date_text_input : TextInputEditText
    private lateinit var edit_product_auction_end_date_text_input : TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            product_id = arguments!!.getString(PRODUCT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_product, container, false)
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
        edit_product_name = view.findViewById(R.id.edit_product_product_name)
        edit_product_weight = view.findViewById(R.id.edit_product_product_weight)
        edit_product_selling_method_radiogroup = view.findViewById(R.id.edit_product_selling_method)

        edit_product_product_price = view.findViewById(R.id.edit_product_product_price)
        edit_product_price_per_kilogram_layout = view.findViewById(R.id.edit_product_price_per_kilogram)

        edit_product_min_price = view.findViewById(R.id.edit_product_product_min_price)
        edit_product_max_price = view.findViewById(R.id.edit_product_product_max_price)
        edit_product_auction_price_range_layout = view.findViewById(R.id.edit_product_auction_price_range)

        edit_product_auction_start_date_constraint_layout = view.findViewById(R.id.edit_product_auction_start_date_constraint_layout)
        edit_product_auction_end_date_constraint_layout = view.findViewById(R.id.edit_product_auction_end_date_constraint_layout)

        edit_product_auction_start_date_icon = view.findViewById(R.id.start_date_icon)
        edit_product_auction_end_date_icon = view.findViewById(R.id.end_date_icon)

        edit_product_auction_start_date_text_input = view.findViewById(R.id.edit_product_auction_start_date_text_input)
        edit_product_auction_end_date_text_input = view.findViewById(R.id.edit_product_auction_end_date_text_input)

        Dummy().initDummyProducts()
        val thisProduct : Product =  Dummy.dummy_products_hashmap[product_id] as Product

        edit_product_name.setText(thisProduct.product_name)
        edit_product_weight.setText(thisProduct.product_weight.toString())

        if(thisProduct.product_selling_method == 1){
            edit_product_selling_method_radiogroup.check(R.id.edit_product_radio_button_fixed)
            edit_product_product_price.setText(thisProduct.product_price.toString())
            hideFixedPriceOptions()
        }
        else{
            edit_product_selling_method_radiogroup.check(R.id.edit_product_radio_button_auction)
            edit_product_min_price.setText(thisProduct.product_price.toString())
            edit_product_max_price.setText(thisProduct.product_initial_bidding_price.toString())
            hideAuctionPriceOptions()
        }

        edit_product_selling_method_radiogroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.edit_product_radio_button_fixed -> {
                    hideFixedPriceOptions()
                }
                R.id.edit_product_radio_button_auction -> {
                    hideAuctionPriceOptions()
                }
            }
        }

        edit_product_auction_start_date_text_input.setOnClickListener {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val hour_of_day = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val minute = Calendar.getInstance().get(Calendar.MINUTE)
            val date_listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val time_listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    edit_product_auction_start_date_text_input.setText(dayOfMonth.toString() + " " + month.toString() + " " + year.toString() + " - " + hourOfDay.toString() + ":" + minute.toString())
                }
                val time_picker = TimePickerDialog(context,0,time_listener,hour_of_day,minute,false)
                time_picker.show()

            }
            val add_product_auction_start_date_datepicker = DatePickerDialog(context,date_listener,year,month,day)
            add_product_auction_start_date_datepicker.show()
        }

        edit_product_auction_end_date_text_input.setOnClickListener {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val hour_of_day = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val minute = Calendar.getInstance().get(Calendar.MINUTE)
            val date_listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val time_listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    edit_product_auction_end_date_text_input.setText(dayOfMonth.toString() + " " + month.toString() + " " + year.toString() + " - " + hourOfDay.toString() + ":" + minute.toString())
                }
                val time_picker = TimePickerDialog(context,0,time_listener,hour_of_day,minute,false)
                time_picker.show()

            }
            val add_product_auction_enddate_picker = DatePickerDialog(context,date_listener,year,month,day)
            add_product_auction_enddate_picker.show()

        }


    }

    fun hideFixedPriceOptions(){
        edit_product_product_price.visibility = View.VISIBLE
        edit_product_price_per_kilogram_layout.visibility = View.VISIBLE
        edit_product_auction_start_date_constraint_layout.visibility = View.GONE
        edit_product_auction_end_date_constraint_layout.visibility = View.GONE
        edit_product_min_price.visibility = View.GONE
        edit_product_max_price.visibility = View.GONE
        edit_product_auction_price_range_layout.visibility = View.GONE
    }

    fun hideAuctionPriceOptions(){
        edit_product_product_price.visibility = View.GONE
        edit_product_price_per_kilogram_layout.visibility = View.GONE
        edit_product_min_price.visibility = View.VISIBLE
        edit_product_max_price.visibility = View.VISIBLE
        edit_product_auction_start_date_constraint_layout.visibility = View.VISIBLE
        edit_product_auction_end_date_constraint_layout.visibility = View.VISIBLE
        edit_product_auction_price_range_layout.visibility = View.VISIBLE

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

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(product_id: String): EditProductFragment {
            val fragment = EditProductFragment()
            val args = Bundle()
            args.putString(PRODUCT_ID, product_id)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
