package gab.cdi.bingwitproducer.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
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
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Environment
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.*
import com.android.volley.VolleyError
import com.bumptech.glide.request.RequestOptions
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.adapters.ProductCategorySpinnerAdapter
import gab.cdi.bingwitproducer.adapters.ProductTypeSpinnerAdapter
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.extensions.isEven
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.ProducerAuctionProduct
import gab.cdi.bingwitproducer.models.ProducerProduct
import gab.cdi.bingwitproducer.models.ProductCategory
import gab.cdi.bingwitproducer.models.ProductType
import gab.cdi.bingwitproducer.utils.DialogUtil
import gab.cdi.bingwitproducer.utils.TimeUtil
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddProductFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddProductFragment : BaseFragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var add_product_auction_start_date_date : Date? = null
    private var add_product_auction_end_date_date : Date? = null
    private var isStartAuctionDatePickerShown = false
    private var isEndAuctionDatePickerShown = false
    private lateinit var mSession : Session
    private lateinit var photo_file : File
    private lateinit var current_file_path : String
    private var image_uri : Uri? = null
    private lateinit var byte_array : ByteArray
    private var upload_image_url : String = ""
    private var product_type_dropdown_options : ArrayList<ProductType> = ArrayList()
    private var product_category_dropdown_options : ArrayList<ProductCategory> = ArrayList()
    var product_category_dropdown_options_name_string : MutableList<String> = mutableListOf()
    var product_type_dropdown_options_name_string : MutableList<String> = mutableListOf()
    val IMAGE_CHANGE = 1
    val IMAGE_CHANGE_GALLERY = 2
    val IMAGE_CHANGE_CAMERA = 3
    val PERMISSION_READ_EXT_STORAGE = 4
    val PERMISSION_OPEN_CAMERA = 5
    var con : Context? = null
    val simple_date_format = SimpleDateFormat("d M yyyy HH:mm")
    val simple_date_format_string = "d M yyyy HH:mm"
    val TIME_SELECTED_START = 6
    val TIME_SELECTED_END = 7
    var start_year : Int? = 0
    var start_month : Int? = 0
    var start_day_of_month : Int? = 0
    var end_year : Int? = 0
    var end_month : Int? = 0
    var end_day_of_month : Int? = 0
    var start_date_string : String = ""
    var end_date_string : String = ""
    var start_count = 1
    var end_count = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        con = context
        mSession = Session(con)

    }

    override fun setTitle(): String {
        return "Add Product"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        activity?.toolbar?.title = setTitle()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()


    }

    fun initUI() {
        getProductCategories()
        add_product_product_category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val product_category : ProductCategory = product_category_dropdown_options[position]
                product_type_dropdown_options.clear()
                product_type_dropdown_options_name_string.clear()
                for(i in 0..product_category.product_types_arraylist.size-1){
                    val product_type = product_category.product_types_arraylist[i]
                    product_type.getAliases()
                    product_type_dropdown_options.add(product_type)
                    product_type_dropdown_options_name_string.add(product_type.name)
                }
                val spinner_adapter = ProductTypeSpinnerAdapter(context!!,R.layout.product_type_dropdown_item,product_type_dropdown_options_name_string,product_type_dropdown_options)
                add_product_product_type_spinner?.adapter = spinner_adapter
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        add_product_selling_method_radiogroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.add_product_selling_method_fixed -> hideAuctionProductOptions()
                R.id.add_product_selling_method_auction -> hidefixedProductOptions()
            }
        }
        add_product_selling_method_radiogroup.check(R.id.add_product_selling_method_fixed)
        add_product_image.setOnClickListener {
            val dialog = UploadImageOptionsDialogFragment()
            dialog.setTargetFragment(this,IMAGE_CHANGE)
            dialog.show(this@AddProductFragment.activity?.supportFragmentManager,"image_picker_option")
        }
        add_product_auction_start_date_text_input.setOnClickListener {
            start_count++
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val date_listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                start_year = year
                start_month = month+1
                start_day_of_month = dayOfMonth
                val time_picker_dialog = TimePickerDialogFragment.newInstance(TIME_SELECTED_START,"$start_year $start_month $start_day_of_month")
                time_picker_dialog.setTargetFragment(this@AddProductFragment,TIME_SELECTED_START)
                if(start_count.isEven()){
                    time_picker_dialog.show(activity?.supportFragmentManager,"time_picker")
                    start_count++
                }

            }
            val add_product_auction_start_date_datepicker = DatePickerDialog(context,date_listener,year,month,day)
            add_product_auction_start_date_datepicker.show()
        }

        add_product_auction_end_date_text_input.setOnClickListener {
            end_count++
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val date_listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                end_year = year
                end_month = month+1
                end_day_of_month = dayOfMonth
                val time_picker_dialog = TimePickerDialogFragment.newInstance(TIME_SELECTED_END,"$end_year $end_month $end_day_of_month")
                time_picker_dialog.setTargetFragment(this@AddProductFragment,TIME_SELECTED_END)
                if(end_count.isEven()){
                    time_picker_dialog.show(activity?.supportFragmentManager,"time_picker")
                    end_count++
                }

            }
            val add_product_auction_enddate_picker = DatePickerDialog(context,date_listener,year,month,day)
            add_product_auction_enddate_picker.show()
        }
        add_product_submit_button.setOnClickListener {
            addProduct()
        }

        add_product_auction_min_price.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(add_product_auction_max_price.text.toString().trim() != "" && add_product_auction_min_price.text.toString().trim() != ""){
                    if(add_product_auction_min_price.text.toString().toDouble() >= add_product_auction_max_price.text.toString().toDouble()){
                        add_product_auction_min_price.error = "Set a lower minimum price than original bidding price"
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })



        add_product_auction_max_price.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(add_product_auction_min_price.text.toString().trim() != "" && add_product_auction_max_price.text.toString().trim() != ""){
                    if(add_product_auction_max_price.text.toString().toDouble() <= add_product_auction_min_price.text.toString().toDouble()){
                        add_product_auction_max_price.error = "Set a higher original bidding price than the minimum price"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    fun hideAuctionProductOptions(){
        add_product_price_per_kilogram.visibility = View.VISIBLE
        add_product_auction_price_range.visibility = View.GONE
        add_product_auction_start_date_constraint_layout.visibility = View.GONE
        add_product_auction_end_date_constraint_layout.visibility = View.GONE
    }

    fun hidefixedProductOptions(){
        add_product_price_per_kilogram.visibility = View.GONE
        add_product_auction_price_range.visibility = View.VISIBLE
        add_product_auction_start_date_constraint_layout.visibility = View.VISIBLE
        add_product_auction_end_date_constraint_layout.visibility = View.VISIBLE
    }


    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun addProduct(){
        if(TextUtils.isEmpty(add_product_product_name_text.text.toString().trim())){
            DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Please provide your product's name")
            return
        }

        if(TextUtils.isEmpty(add_product_product_weight.text.toString().trim())){
            DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Please provide your product's available stock")
            return
        }
        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"
        headers.put("Content-Type","application/x-www-form-urlencoded")
        headers.put("Authorization",authorization)
        val params : HashMap<String,String> = HashMap()
        params.put("name",add_product_product_name_text.text.toString().trim())
        params.put("stock",add_product_product_weight.text.toString().trim())
        val selected_product_type = product_type_dropdown_options[add_product_product_type_spinner.selectedItemPosition]
        params.put("product_type_id",selected_product_type.id) //dummy muna
        if(upload_image_url != ""){
            params.put("image_url",upload_image_url)
        }
        val message = "Adding Product"
        Log.d("params",params.toString())
        when(add_product_selling_method_radiogroup.checkedRadioButtonId){
            R.id.add_product_selling_method_fixed -> {
                if(!TextUtils.isEmpty(add_product_price_per_kilogram_edit_text.text.toString()) && add_product_price_per_kilogram_edit_text.text.toString().toInt() <= 0){
                    DialogUtil.showErrorDialog(activity!!.supportFragmentManager,2500,"Please provide an asking price greater than zero to make sure you gain profit from your goods")
                    return
                }
                if(TextUtils.isEmpty(add_product_price_per_kilogram_edit_text.text.toString().trim())){
                    DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Please provide your your asking price per kilo")
                    return
                }
                params.put("price_per_kilo",add_product_price_per_kilogram_edit_text.text.toString().trim())
                Log.d("Added product",params.toString())
                ApiRequest.post(context, "${API.ADD_PRODUCT}/products",headers,params,message,object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("Add product",response)
                        mActivity.fab.show()
                        mActivity.fm.popBackStackImmediate()
                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                    }
                })
            }
            R.id.add_product_selling_method_auction -> {
                if(!TextUtils.isEmpty(add_product_auction_min_price.text.toString()) && add_product_auction_min_price.text.toString().toDouble() <= 0){
                    DialogUtil.showErrorDialog(activity!!.supportFragmentManager,2500,"Please provide an end price greater than zero to make sure you gain profit this auction")
                    return
                }
                if(add_product_auction_min_price.text.toString().trim() == ""){
                    DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Fill in minimum price")
                    return
                }
                if(add_product_auction_max_price.text.toString().trim() == ""){
                    DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Fill in starting price")
                    return
                }
                if(add_product_auction_min_price.text.toString().trim().toDouble() >= add_product_auction_max_price.text.toString().trim().toDouble()){
                    DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Minimum price should not be greater or equal to the starting price")
                    return
                }
                if(add_product_auction_max_price.text.toString().trim().toDouble() <= add_product_auction_min_price.text.toString().trim().toDouble()){
                    DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Starting price should not be lesser or equal to the minimum price")
                    return
                }
                if(TextUtils.isEmpty(start_date_string.trim()) || TextUtils.isEmpty((end_date_string.trim()))){
                    DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Please provide complete auction schedule date")
                    return
                }
                params.put("min_price",add_product_auction_min_price.text.toString())
                params.put("max_price",add_product_auction_max_price.text.toString())
                params.put("start",start_date_string)
                params.put("end",end_date_string)
                Log.d("Params",params.toString())
                ApiRequest.post(context, "${API.ADD_PRODUCT}products/auctions",headers,params,message,object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("Add product",response)
                        val mActivity = activity as MainActivity
                        mActivity.fab.show()
                        mActivity.fm.popBackStackImmediate()
                    }
                },
                        object : ApiRequest.ErrorCallback{
                            override fun didURLError(error: VolleyError) {
                                DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                            }
                        })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            IMAGE_CHANGE -> {
                if(resultCode == Activity.RESULT_OK){
                    if(data != null){
                        sendImageUploadOption(data.getIntExtra("image_upload_mode",1))
                    }
                }
            }
            IMAGE_CHANGE_GALLERY -> {
                if(resultCode == Activity.RESULT_OK){
                    image_uri = data?.data
                    val input_stream = context?.contentResolver?.openInputStream(image_uri)
                    byte_array = getBytes(input_stream!!)
                    add_product_upload_image_option_container.setBackgroundColor(Color.parseColor("#ffffff"))
                    add_product_upload_image_option_container.setPadding(0,0,0,0)
                    GlideApp.with(activity!!).load(image_uri).into(add_product_image)
                    add_product_image_toggle.visibility = View.INVISIBLE
                    uploadImage()
                }
            }
            IMAGE_CHANGE_CAMERA -> {
                if(resultCode == Activity.RESULT_OK){
                    val m_photo_file_bitmap : Bitmap = BitmapFactory.decodeFile(photo_file.absolutePath)
                    val stream = ByteArrayOutputStream()
                    m_photo_file_bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream)
                    byte_array = stream.toByteArray()
                    add_product_upload_image_option_container.setBackgroundColor(Color.parseColor("#ffffff"))
                    add_product_upload_image_option_container.setPadding(0,0,0,0)
                    GlideApp.with(activity!!).load(m_photo_file_bitmap).into(add_product_image)
                    add_product_image_toggle.visibility = View.INVISIBLE
                    uploadImage()
                }
            }
            TIME_SELECTED_START -> {
                if(resultCode == Activity.RESULT_OK){
                    val hour_min_string = data?.getStringExtra("hour_minute")
                    val date_string = "${start_day_of_month} ${start_month} ${start_year} ${hour_min_string}"
                    val date_string_result = simple_date_format.parse(date_string)
                    add_product_auction_start_date_date = date_string_result
                    if(add_product_auction_end_date_date != null){
                        val date_diff = add_product_auction_start_date_date!!.compareTo(add_product_auction_end_date_date)
                        val current_date_time = Calendar.getInstance().time
                        if(date_diff < 0 && add_product_auction_start_date_date!!.compareTo(current_date_time) > 0){
                            if(add_product_auction_start_date_date!!.time - System.currentTimeMillis() >= 900000){
                                start_date_string = "${start_year}-${start_month}-${start_day_of_month} $hour_min_string"
                                add_product_auction_start_date_text_input.setText(TimeUtil.changeDateFormat(date_string,simple_date_format_string))
//                                add_product_auction_start_date_text_input.setText("${start_year}-${start_month}-${start_day_of_month} $hour_min_string")
                            }
                            else{
                                Toast.makeText(context,"Start time must be at least 15 minutes after time posted.",Toast.LENGTH_SHORT).show()
                            }
                            isStartAuctionDatePickerShown = false
                        }
                        else if(date_diff > 0 || date_diff == 0){
                            Toast.makeText(context,"Enter date and time before auction's end date",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context,"Enter date and time before auction's end date",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        val current_date_time = Calendar.getInstance().time
                        if(add_product_auction_start_date_date!!.compareTo(current_date_time) < 0){
                           Toast.makeText(context,"Enter date and time after current date and time",Toast.LENGTH_SHORT).show()
                        }
                        else{
//                            add_product_auction_start_date_text_input.setText("${start_year}-${start_month}-${start_day_of_month} $hour_min_string")
                            if(add_product_auction_start_date_date!!.time - System.currentTimeMillis() >= 900000){
                                start_date_string = "${start_year}-${start_month}-${start_day_of_month} $hour_min_string"
                                add_product_auction_start_date_text_input.setText(TimeUtil.changeDateFormat(date_string,simple_date_format_string))
                                //add_product_auction_start_date_text_input.setText("${start_year}-${start_month}-${start_day_of_month} $hour_min_string")
                            }
                            else{
                                Toast.makeText(context,"Start time must be at least 15 minutes after time posted.",Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }
                isStartAuctionDatePickerShown = false
            }

            TIME_SELECTED_END -> {
                if(resultCode == Activity.RESULT_OK){
                    val hour_min_string = data?.getStringExtra("hour_minute")
                    val date_string = "${end_day_of_month} ${end_month} ${end_year} ${hour_min_string}"
                    val date_string_result = simple_date_format.parse(date_string)
                    add_product_auction_end_date_date = date_string_result
                    if(add_product_auction_start_date_date != null){
                        val date_diff = add_product_auction_end_date_date!!.compareTo(add_product_auction_start_date_date)
                        if(date_diff > 0){
                            end_date_string = "${end_year}-${end_month}-${end_day_of_month} $hour_min_string"
                            add_product_auction_end_date_text_input.setText(TimeUtil.changeDateFormat(date_string,simple_date_format_string))
//                            add_product_auction_end_date_text_input.setText("${end_year}-${end_month}-${end_day_of_month} $hour_min_string")
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
                            end_date_string = "${end_year}-${end_month}-${end_day_of_month} $hour_min_string"
                            add_product_auction_end_date_text_input.setText(TimeUtil.changeDateFormat(date_string,simple_date_format_string))
//                            add_product_auction_end_date_text_input.setText("${end_year}-${end_month}-${end_day_of_month} $hour_min_string")
                        }
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        len = inputStream.read(buffer)
        while (len != -1) {
            byteBuffer.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }
        return byteBuffer.toByteArray()
    }

    fun sendImageUploadOption(option: Int) {
        if(option == 1){ //start image picker intent
            if (ContextCompat.checkSelfPermission(context!!,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val mActivity = context as Activity
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openGallery() }
                else {
                    ActivityCompat.requestPermissions(mActivity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_READ_EXT_STORAGE)
                }
            } else { openGallery() }
        }
        if(option == 0){
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                val mActivity = context as Activity
                if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)){
                    openCamera()
                }
                else {
                    ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.CAMERA),PERMISSION_OPEN_CAMERA)
                }
            }
            else {
                openCamera()
            }
        }
    }

    fun uploadImage(){
        val message = "Uploading image..."
        ApiRequest.multipartPost(context!!,API.UPLOAD_IMAGE,message,".jpg","profile.jpg",byte_array,object : ApiRequest.URLCallback {
            override fun didURLResponse(response: String) {
                Log.d("Response",response)
                val json = JSONObject(response)
                val url = json.getString("url")
                upload_image_url = url
            }
        },object : ApiRequest.ErrorCallback{
            override fun didURLError(error: VolleyError) {
                Log.d("Error",error.toString())
            }
        })
    }

    fun openGallery(){
        val gallery_intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery_intent,IMAGE_CHANGE_GALLERY)
    }

    fun openCamera(){
        try {
            photo_file  = createImageFile()
            if(photo_file != null){
                val photo_uri = FileProvider.getUriForFile(context!!,"gab.cdi.bingwitproducer.fileprovider",photo_file)
                val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,photo_uri)
                startActivityForResult(camera_intent,IMAGE_CHANGE_CAMERA)
            }
        }catch (e : IOException){
            e.printStackTrace()
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_"+"${mSession.id()}"+timeStamp+"_"
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        current_file_path = image.absolutePath
        return image
    }

    fun getProductCategories() {
        Log.d("Cat","cat")
        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"
        headers.put("Authorization",authorization)
        ApiRequest.get(context,API.GET_PRODUCT_CATEGORY,headers, HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("categories",response)
                        product_category_dropdown_options.clear()
                        product_category_dropdown_options_name_string.clear()
                        val json = JSONObject(response)
                        val product_category_json_array : JSONArray = json.optJSONObject("category").optJSONArray("rows")
                        for(i in 0..product_category_json_array.length()-1){
                            val product_category_json = product_category_json_array[i] as JSONObject
                            val this_product_category = ProductCategory(product_category_json)
                            val this_product_category_types : JSONArray = product_category_json.optJSONArray("product_type")
                            for(j in 0..this_product_category_types.length()-1){
                                val this_product_type = this_product_category_types[j] as JSONObject
                                this_product_category.product_types_arraylist.add(ProductType(this_product_type))
                            }
                            product_category_dropdown_options.add(this_product_category)
                            product_category_dropdown_options_name_string.add(this_product_category.name)
                        }


                        if(this@AddProductFragment.view?.isAttachedToWindow == true){
                            Log.d("HERE_PLS","here_pls")
                            val spinner_adapter = ProductCategorySpinnerAdapter(context!!,R.layout.product_category_dropdown_item, product_category_dropdown_options_name_string,product_category_dropdown_options)
                            add_product_product_category_spinner?.adapter = spinner_adapter

                        }

                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Log.d("error_cat","error_cat")
                        DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                    }
                })
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
