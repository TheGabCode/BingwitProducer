package gab.cdi.bingwitproducer.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.extensions.convertToCurrencyDecimalFormat
import gab.cdi.bingwitproducer.extensions.isEven
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.utils.DialogUtil
import gab.cdi.bingwitproducer.utils.TimeUtil
import kotlinx.android.synthetic.main.fragment_edit_product.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


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
    var product_type : String? = null
    private lateinit var mSession : Session
    private lateinit var product_json_object : JSONObject
    private var mListener: OnFragmentInteractionListener? = null
    var edit_product_auction_start_date_date : Date? = null
    var edit_product_auction_end_date_date : Date? = null
    private lateinit var photo_file : File
    private lateinit var current_file_path : String
    private var image_uri : Uri? = null
    private lateinit var byte_array : ByteArray
    private var upload_image_url : String? = null
    val IMAGE_CHANGE = 1
    val IMAGE_CHANGE_GALLERY = 2
    val IMAGE_CHANGE_CAMERA = 3
    val PERMISSION_READ_EXT_STORAGE = 4
    val PERMISSION_OPEN_CAMERA = 5
    @SuppressLint("SimpleDateFormat")
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
            product_id = arguments!!.getString(PRODUCT_ID)
            product_type = arguments!!.getString(PRODUCT_TYPE)
        }
        mSession = Session(context)

        when(product_type){
            "fixed" ->  fetchProductById()

            "auction" -> fetchProductAuctionById()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_product, container, false)
        //initUI(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun initUI() {
        edit_product_selling_method.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.edit_product_radio_button_fixed -> hideAuctionPriceOptions()

                R.id.edit_product_radio_button_auction -> hideFixedPriceOptions()
            }

        }

        edit_product_image.setOnClickListener {
            val dialog = UploadImageOptionsDialogFragment()
            dialog.setTargetFragment(this,IMAGE_CHANGE)
            dialog.show(this@EditProductFragment.activity?.supportFragmentManager,"image_picker_option")
        }

        edit_product_save.setOnClickListener {
            updateProduct()
        }


        edit_product_product_min_price.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(edit_product_product_max_price.text.toString().trim() != "" && edit_product_product_min_price.text.toString().trim() != ""){
                    if(edit_product_product_min_price.text.toString().toDouble() >= edit_product_product_max_price.text.toString().toDouble()){
                        edit_product_product_min_price.error = "Set a lower minimum price than original bidding price"
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })



        edit_product_product_max_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(edit_product_product_min_price.text.toString().trim() != "" && edit_product_product_max_price.text.toString().trim() != ""){
                    if(edit_product_product_max_price.text.toString().toDouble() <= edit_product_product_min_price.text.toString().toDouble()){
                        edit_product_product_max_price.error = "Set a higher original bidding price than the minimum price"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })



            edit_product_auction_start_date_text_input.setOnClickListener {
                start_count++
                val year = Calendar.getInstance().get(Calendar.YEAR)
                val month = Calendar.getInstance().get(Calendar.MONTH)
                val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                val date_listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    start_year = year
                    start_month = month+1
                    start_day_of_month = dayOfMonth
                    val time_picker_dialog = TimePickerDialogFragment.newInstance(TIME_SELECTED_START,"$start_year $start_month $start_day_of_month")
                    time_picker_dialog.setTargetFragment(this@EditProductFragment,TIME_SELECTED_START)
                    if(start_count.isEven()){
                        time_picker_dialog.show(activity?.supportFragmentManager,"time_picker")
                        start_count++
                    }

                }
                val edit_product_auction_start_date_datepicker = DatePickerDialog(context,date_listener,year,month,day)
                edit_product_auction_start_date_datepicker.show()
        }

        edit_product_auction_end_date_text_input.setOnClickListener {
            end_count++
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val date_listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                end_year = year
                end_month = month+1
                end_day_of_month = dayOfMonth
                val time_picker_dialog = TimePickerDialogFragment.newInstance(TIME_SELECTED_END,"$end_year $end_month $end_day_of_month")
                time_picker_dialog.setTargetFragment(this@EditProductFragment,TIME_SELECTED_END)
                if(end_count.isEven()){
                    time_picker_dialog.show(activity?.supportFragmentManager,"time_picker")
                    end_count++
                }

            }
            val edit_product_auction_enddate_picker = DatePickerDialog(context,date_listener,year,month,day)
            edit_product_auction_enddate_picker.show()

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
                    GlideApp.with(activity!!).load(image_uri).into(edit_product_image)
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
                    GlideApp.with(activity!!).load(m_photo_file_bitmap).into(edit_product_image)
                    uploadImage()
                }
            }

            TIME_SELECTED_START -> {
                if(resultCode == Activity.RESULT_OK){
                    val hour_min_string = data?.getStringExtra("hour_minute")
                    val date_string = "${start_day_of_month} ${start_month} ${start_year} ${hour_min_string}"
                    val date_string_result = simple_date_format.parse(date_string)
                    edit_product_auction_start_date_date = date_string_result
                    //Toast.makeText(context,add_product_auction_start_date_date.toString(),Toast.LENGTH_SHORT).show()
                    if(edit_product_auction_end_date_date != null){
                        val date_diff = edit_product_auction_start_date_date!!.compareTo(edit_product_auction_end_date_date)
                        val current_date_time = Calendar.getInstance().time
                        if(date_diff < 0 && edit_product_auction_start_date_date!!.compareTo(current_date_time) > 0){
                            if(edit_product_auction_start_date_date!!.time - System.currentTimeMillis() >= 900000){
                                edit_product_auction_start_date_text_input.setText(TimeUtil.changeDateFormat(date_string,simple_date_format_string))
                                start_date_string = "${start_year}-${start_month}-${start_day_of_month} $hour_min_string"
                                //                            edit_product_auction_start_date_text_input.setText("${start_year}-${start_month}-${start_day_of_month} $hour_min_string")
                            }
                            else{
                                Toast.makeText(context,"Start time must be at least 15 minutes after time posted.",Toast.LENGTH_SHORT).show()
                            }
                            //edit_product_auction_start_date_text_input.setText("${start_year}-${start_month}-${start_day_of_month} $hour_min_string")
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
                        if(edit_product_auction_start_date_date!!.compareTo(current_date_time) < 0){
                            Toast.makeText(context,"Enter date and time after current date and time",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            if(edit_product_auction_start_date_date!!.time - System.currentTimeMillis() >= 900000){
                                start_date_string = "${start_year}-${start_month}-${start_day_of_month} $hour_min_string"
                                edit_product_auction_start_date_text_input.setText(TimeUtil.changeDateFormat(date_string,simple_date_format_string))
 //                               edit_product_auction_start_date_text_input.setText("${start_year}-${start_month}-${start_day_of_month} $hour_min_string")
                            }
                            else{
                                Toast.makeText(context,"Start time must be at least 15 minutes after time posted.",Toast.LENGTH_SHORT).show()
                            }
                            //edit_product_auction_start_date_text_input.setText("${start_year}-${start_month}-${start_day_of_month} $hour_min_string")

                        }
                    }
                }
            }

            TIME_SELECTED_END -> {
                val hour_min_string = data?.getStringExtra("hour_minute")
                val date_string = "${end_day_of_month} ${end_month} ${end_year} ${hour_min_string}"
                val date_string_result = simple_date_format.parse(date_string)
                edit_product_auction_end_date_date = date_string_result
                if(edit_product_auction_start_date_date != null){
                    val date_diff = edit_product_auction_end_date_date!!.compareTo(edit_product_auction_start_date_date)
                    if(date_diff > 0){
                        end_date_string = "${end_year}-${end_month}-${end_day_of_month} $hour_min_string"
                        edit_product_auction_end_date_text_input.setText(TimeUtil.changeDateFormat(date_string,simple_date_format_string))
                        //edit_product_auction_end_date_text_input.setText("${end_year}-${end_month}-${end_day_of_month} $hour_min_string")
                    }
                    else if(date_diff < 0 || date_diff == 0){
                        Toast.makeText(context,"Enter date and time after auction's start date",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    val current_date_time = Calendar.getInstance().time
                    if(edit_product_auction_end_date_date!!.compareTo(current_date_time) < 0){
                        Toast.makeText(context,"Enter date and time after current date and time",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        end_date_string = "${end_year}-${end_month}-${end_day_of_month} $hour_min_string"
                        edit_product_auction_end_date_text_input.setText(TimeUtil.changeDateFormat(date_string,simple_date_format_string))
//                        edit_product_auction_end_date_text_input.setText("${end_year}-${end_month}-${end_day_of_month} $hour_min_string")
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val mActivity = context as Activity
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
        val imageFileName = "JPEG_"+timeStamp+"_"
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

    fun hideFixedPriceOptions(){
        edit_product_auction_price_range.visibility = View.VISIBLE
        edit_product_auction_start_date_constraint_layout.visibility = View.VISIBLE
        edit_product_auction_end_date_constraint_layout.visibility = View.VISIBLE
        edit_product_price_per_kilogram.visibility = View.GONE

    }

    fun hideAuctionPriceOptions(){
        edit_product_auction_price_range.visibility = View.GONE
        edit_product_auction_start_date_constraint_layout.visibility = View.GONE
        edit_product_auction_end_date_constraint_layout.visibility = View.GONE
        edit_product_price_per_kilogram.visibility = View.VISIBLE
    }

    fun fetchProductById(){
        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"
        headers.put("Authorization",authorization)
        ApiRequest.get(context, "${API.GET_PRODUCT_BY_ID}/$product_id",headers, HashMap(),object : ApiRequest.URLCallback{
            override fun didURLResponse(response: String) {
                Log.d("Get product",response)
                product_json_object = JSONObject(response).getJSONObject("product")
                edit_product_product_name?.setText(product_json_object.optString("name"))
                edit_product_product_weight?.setText(product_json_object.optInt("stock").toString())
                edit_product_product_price?.setText(product_json_object.optDouble("price_per_kilo").toInt().toString())
                if(this@EditProductFragment.view?.isAttachedToWindow == true){
                    GlideApp.with(this@EditProductFragment).load(product_json_object.optString("image_url")).into(edit_product_image)
                }

                upload_image_url = product_json_object.optString("image_url")
                Log.d("image_url",upload_image_url)
                edit_product_selling_method?.check(R.id.edit_product_radio_button_fixed)
                edit_product_product_type?.setText(product_json_object.optJSONObject("product_type").optString("name"))

            }
        }, object : ApiRequest.ErrorCallback{
            override fun didURLError(error: VolleyError) {

            }
        })
    }

    fun fetchProductAuctionById(){
        ApiRequest.get(context,"${API.GET_PRODUCT_AUCTION_BY_ID}/$product_id",HashMap(), HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("Get product auction",response)
                        product_json_object = JSONObject(response).optJSONObject("product")
                        edit_product_product_name?.setText(product_json_object.optJSONObject("product").optString("name"))
                        edit_product_selling_method?.check(R.id.edit_product_radio_button_auction)
                        edit_product_product_min_price?.setText(product_json_object.optString("min_price").toDouble().convertToCurrencyDecimalFormat())
                        edit_product_product_max_price?.setText(product_json_object.optString("max_price").toDouble().convertToCurrencyDecimalFormat())

                        val start_date : String = product_json_object.optString("start")
                        val start_date_tokens = start_date.split("T")
                        val start_date_date_tokens = start_date_tokens[0].split("-")
                        val start_date_time_tokens = start_date_tokens[1].split(":")
                        val start_date_formatted = "${start_date_tokens[0]} ${start_date_time_tokens[0].toInt()}:${start_date_time_tokens[1]}"
                        start_date_string = start_date_formatted
                       // Toast.makeText(context,start_date_formatted,Toast.LENGTH_SHORT).show()
                        val start_date_dispay_to_format = "${start_date_date_tokens[2]} ${start_date_date_tokens[1]} ${start_date_date_tokens[0]} ${start_date_time_tokens[0]}:${start_date_time_tokens[1]}"
                        edit_product_auction_start_date_text_input?.setText(TimeUtil.changeDateFormat(start_date_dispay_to_format,simple_date_format_string))
                        edit_product_auction_start_date_date = simple_date_format.parse("${start_date_date_tokens[2]} ${start_date_date_tokens[1]} ${start_date_date_tokens[0]} ${start_date_time_tokens[0]}:${start_date_time_tokens[1]}")

                        val end_date : String = product_json_object.optString("end")
                        val end_date_tokens = end_date.split("T")
                        val end_date_date_tokens = end_date_tokens[0].split("-")
                        val end_date_time_tokens = end_date_tokens[1].split(":")
                        val end_date_formatted = "${end_date_tokens[0]} ${end_date_time_tokens[0].toInt()}:${end_date_time_tokens[1]}"
                        end_date_string = end_date_formatted
                        val end_date_display_to_format = "${end_date_date_tokens[2]} ${end_date_date_tokens[1]} ${end_date_date_tokens[0]} ${end_date_time_tokens[0]}:${end_date_time_tokens[1]}"
                        edit_product_auction_end_date_text_input?.setText(TimeUtil.changeDateFormat(end_date_display_to_format,simple_date_format_string))

                        edit_product_auction_end_date_date = simple_date_format.parse("${end_date_date_tokens[2]} ${end_date_date_tokens[1]} ${end_date_date_tokens[0]} ${end_date_time_tokens[0]}:${end_date_time_tokens[1]}")
                        if(this@EditProductFragment.view?.isAttachedToWindow == true){
                            GlideApp.with(this@EditProductFragment).load(product_json_object.optJSONObject("product").optString("image_url")).into(edit_product_image)
                        }

                        upload_image_url = product_json_object.optJSONObject("product").optString("image_url")
                        Log.d("image_url",upload_image_url)
                        edit_product_product_type?.text = product_json_object.optJSONObject("product").optJSONObject("product_type").optString("name")
                        edit_product_product_weight?.setText(product_json_object.optJSONObject("product").optInt("stock").toString())


                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {}
                })
    }
    fun updateProduct () {
        val header : HashMap<String,String> = HashMap()
        header.put("Content-Type","application/x-www-form-urlencoded")
        val authorization = "Bearer ${mSession.token()}"
        header.put("Authorization",authorization)
        val message = "Uploading product..."
        val params : HashMap<String,String> = HashMap()
        params.put("name",edit_product_product_name.text.toString().trim())
        if(upload_image_url != null){
            Log.d("ImageURL","NOT NULL")
            Log.d("not null",upload_image_url)
            params.put("image_url",upload_image_url!!)
        }

        if(upload_image_url == "null" || upload_image_url == null){
            Log.d("isNull","it is null")
            params.remove("image_url")
        }
        //UPDATING PRODUCT
        if(product_type == "fixed" && edit_product_selling_method.checkedRadioButtonId == R.id.edit_product_radio_button_fixed){
            params.put("stock",product_json_object.optInt("stock").toString())
            params.put("product_type_id",product_json_object.optJSONObject("product_type").optString("id"))

            params.put("price_per_kilo",edit_product_product_price.text.toString().trim())
            ApiRequest.put(context,"${API.UPDATE_PRODUCT}products/$product_id",message,header,params,
                    object : ApiRequest.URLCallback{
                        override fun didURLResponse(response: String) {
                            Log.d("Edit prodict",response)
                            val mActivity = activity as MainActivity
                            mActivity.fm.popBackStackImmediate()
                            val mFragment = mActivity.supportFragmentManager.findFragmentById(R.id.bingwit_navigation_activity) as ViewProductFragment
                            mFragment.fetchProductById()
                            }
                    },
                    object : ApiRequest.ErrorCallback{
                        override fun didURLError(error: VolleyError) {
                            DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                        }
                    })
        }

        //UPDATING PRODUCT TO AUCTION
        if(product_type == "fixed" && edit_product_selling_method.checkedRadioButtonId == R.id.edit_product_radio_button_auction){
            params.remove("image_url")

            if(edit_product_product_min_price.text.toString().trim() == ""){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Indicate minimum price")
                return
            }

            if(edit_product_product_max_price.text.toString().trim() == ""){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Indicate starting price")
                return
            }

            if(edit_product_product_min_price.text.toString().toDouble() >= edit_product_product_max_price.text.toString().toDouble()){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"End price must be lower than starting price")
                return
            }

            if(edit_product_product_max_price.text.toString().toDouble() <= edit_product_product_min_price.text.toString().toDouble()){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Starting price must be higher than end price")
                return
            }

            if(TextUtils.isEmpty(start_date_string.trim()) || TextUtils.isEmpty(end_date_string.trim())){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Please provide complete auction schedule details")
                return
            }

            params.put("end",end_date_string+":00")
            params.put("start",start_date_string+":00")

            params.put("min_price",edit_product_product_min_price.text.toString())
            params.put("max_price",edit_product_product_max_price.text.toString())

            params.put("product_type_id",product_json_object.optJSONObject("product_type").optString("id"))
            Log.d("Date",params.toString())
            ApiRequest.put(context,"${API.UPDATE_FIXED_TO_AUCTION}/${product_json_object.optString("id")}/auctions",message,header, params,
                    object : ApiRequest.URLCallback{
                        override fun didURLResponse(response: String) {
                            Log.d("fixed to auction",response)
                            val json = JSONObject(response)
                            val auction_id = json.optJSONObject("auction_product").optString("id")
                            val mActivity = activity as MainActivity
                            mActivity.fm.popBackStackImmediate()
                        }
                    },
                    object : ApiRequest.ErrorCallback{
                        override fun didURLError(error: VolleyError) {
                            Log.d("params",params.toString())
                            DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                        }
                    })
        }

        //UPDATING AUCTION
        if(product_type == "auction" && edit_product_selling_method.checkedRadioButtonId == R.id.edit_product_radio_button_auction){

            if(edit_product_product_min_price.text.toString().trim() == ""){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Indicate minimum price")
                return
            }
            if(edit_product_product_max_price.text.toString().trim() == ""){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Indicate starting price")
                return
            }

            if(edit_product_product_min_price.text.toString().toDouble() >= edit_product_product_max_price.text.toString().toDouble()){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"End price must be lower than starting price")
                return
            }
            if(edit_product_product_max_price.text.toString().toDouble() <= edit_product_product_min_price.text.toString().toDouble()){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Starting price must be higher than end price")
                return
            }

            if(TextUtils.isEmpty(start_date_string.trim()) || TextUtils.isEmpty(end_date_string.trim())){
                DialogUtil.showErrorDialog(activity!!.supportFragmentManager,1500,"Please provide complete auction schedule details")
                return
            }

            params.put("end",end_date_string+":00")
            params.put("start",start_date_string+":00")
            params.put("stock",product_json_object.optJSONObject("product").optInt("stock").toString())
            params.put("product_type_id",product_json_object.optJSONObject("product").optJSONObject("product_type").optString("id"))
            params.put("min_price",edit_product_product_min_price.text.toString())
            params.put("max_price",edit_product_product_max_price.text.toString())
            Log.d("Date",params.toString())
            ApiRequest.put(context,"${API.UPDATE_PRODUCT_AUCTION}/$product_id",message,header,params,
                    object : ApiRequest.URLCallback{
                        override fun didURLResponse(response: String) {
                            Log.d("Update_auction",response)
                            val json = JSONObject(response)
                            val product_id = json.optJSONObject("result_auction").optString("id")
                            val mActivity = activity as MainActivity
                            mActivity.fm.popBackStackImmediate()
                            val mFragment = mActivity.supportFragmentManager.findFragmentById(R.id.bingwit_navigation_activity) as ViewProductFragment
                            mFragment.fetchProductAuctionById()
                        }
                    },
                    object : ApiRequest.ErrorCallback{
                        override fun didURLError(error: VolleyError) {
                            Log.d("Huh","ERRRO")
                            DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                        }
                    })
        }

        //UPDATE AUCTION TO FIXED
        if(product_type == "auction" && edit_product_selling_method.checkedRadioButtonId == R.id.edit_product_radio_button_fixed){
            params.put("price_per_kilo",edit_product_product_price.text.toString().trim())

            ApiRequest.put(context,"${API.UPDATE_AUCTION_TO_FIXED}/${product_json_object.optJSONObject("Product").optString("id")}/auctions/${product_json_object.optString("id")}",message,header,params,
                    object : ApiRequest.URLCallback{
                        override fun didURLResponse(response: String) {
                            Log.d("auction to fixed",response)
                            val json = JSONObject(response)
                            val product_id = json.optJSONObject("product").optString("id")
                            val mActivity = activity as MainActivity
                            mActivity.fm.popBackStackImmediate()
                            //mActivity.fm.popBackStackImmediate()
                            //mActivity.fragmentAddBackStack(ViewProductFragment.newInstance(product_id,"fixed"),"view_product_fragment")
                            //mActivity.displaySelectedId(R.id.nav_view_product, hashMapOf("product_id" to product_id, "product_type" to "fixed"))
                        }
                    },
                    object : ApiRequest.ErrorCallback{
                        override fun didURLError(error: VolleyError) {
                            DialogUtil.showVolleyErrorDialog(activity!!.supportFragmentManager,error)
                        }
                    })

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
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val PRODUCT_ID = "product_id"
        private val PRODUCT_TYPE = "product_type"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProductFragment.
         */
        fun newInstance(product_id: String, product_type : String): EditProductFragment {
            val fragment = EditProductFragment()
            val args = Bundle()
            args.putString(PRODUCT_ID, product_id)
            args.putString(PRODUCT_TYPE, product_type)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
