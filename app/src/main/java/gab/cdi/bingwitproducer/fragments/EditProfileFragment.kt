package gab.cdi.bingwitproducer.fragments


import android.Manifest
import android.app.Activity
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
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.VolleyError
import com.bumptech.glide.request.RequestOptions
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.R.id.*
import gab.cdi.bingwitproducer.R.string.town
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.Area
import gab.cdi.bingwitproducer.models.Barangay
import gab.cdi.bingwitproducer.models.Municipality
import gab.cdi.bingwitproducer.models.Province
import gab.cdi.bingwitproducer.utils.DialogUtil
import gab.cdi.bingwitproducer.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class EditProfileFragment : BaseFragment(){

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var image_uri : Uri? = null
    val IMAGE_CHANGE = 1
    val IMAGE_CHANGE_GALLERY = 2
    val IMAGE_CHANGE_CAMERA = 3
    val PERMISSION_READ_EXT_STORAGE = 4
    val PERMISSION_OPEN_CAMERA = 5
    private var current_user_number : String? = null
    private lateinit var mSession : Session
    private lateinit var photo_file : File
    private lateinit var current_file_path : String
    private lateinit var byte_array : ByteArray
    private var upload_image_url : String = ""
    private var areas_dropdown_spinner_arraylist : ArrayList<Area> = ArrayList()
    private var provinces_array_list : ArrayList<Province> = ArrayList()
    private var municipality_array_list : ArrayList<Municipality> = ArrayList()
    private var province_municipality : HashMap<String,MutableList<Municipality>> = HashMap()
    private var municipality_brgy : HashMap<String,MutableList<String>> = HashMap()
    private var town_spinner_adapter : ArrayAdapter<Municipality>? = null
    private var bryg_spinner_adapter : ArrayAdapter<String>? = null

    lateinit var json_address : JSONObject
    lateinit var json_user : JSONObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
        mSession = Session(context)
    }

    override fun setTitle(): String {
        return "Profile"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.edit_profile,menu)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        async { addAddressSpinnerListeners() }
        initUI(view)
        getAreas()
    }

    fun initUI(view : View){
        initiateUserDetails()
        profile_image.setOnClickListener {
            val dialog = UploadImageOptionsDialogFragment()
            dialog.setTargetFragment(this,IMAGE_CHANGE)
            dialog.show(this@EditProfileFragment.activity?.supportFragmentManager,"image_picker_option")
        }
        edit_profile_save.isEnabled = false
        edit_profile_save.setOnClickListener {
            saveUpdates()
        }

    }


    fun saveUpdates(){
        val header : HashMap<String,String> = HashMap()

        val authorization = "Bearer ${mSession.token()}"
        header.put("Authorization",authorization)
        header.put("Content-Type","application/x-www-form-urlencoded")

        val params : HashMap<String,String> = HashMap()

        val json_address_update = JSONObject()
        json_address_update.put("province",(province_spinner.selectedItem as Province).province_name)
        json_address_update.put("municipality",(town_spinner.selectedItem as Municipality).municipality_name)
        json_address_update.put("barangay",(barangay_spinner.selectedItem as String))
        json_address_update.put("street",edit_profile_address_edit_text.text.toString())


        params.put("full_name",edit_profile_fullname_edit_text.text.toString())
        params.put("address",json_address_update.toString())
        params.put("image_url",upload_image_url)
        params.put("area_id",(edit_profile_area_spinner.selectedItem as Area).id)
        if(current_user_number != null && current_user_number != edit_profile_phone_number_edit_text?.text.toString().trim()){
            params.put("contact_number",edit_profile_phone_number_edit_text?.text.toString())
        }

        Log.d("info",header.toString())

        ApiRequest.put(context,"${API.UPDATE_USER}/"+"${mSession.id()}","Updating user info...",header,params,object : ApiRequest.URLCallback {
            override fun didURLResponse(response: String) {
                val mActivity = activity as MainActivity
                mActivity.fragmentReplaceBackStack(ProfileFragment(),"profile_fragment")
            }
        },
        object : ApiRequest.ErrorCallback{
            override fun didURLError(error: VolleyError) {
                Log.d("Error ", error.toString())
                }
        })
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

    suspend fun addAddressSpinnerListeners() = coroutineScope{
        provinces_array_list = doLoadProvinces().await()

        val province_spinner_adapter : ArrayAdapter<Province> = ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,provinces_array_list)
        province_spinner.adapter = province_spinner_adapter
        province_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selected_province = provinces_array_list[position]
                val selected_province_id = selected_province.province_code
                town_spinner_adapter = ArrayAdapter<Municipality>(context,R.layout.support_simple_spinner_dropdown_item,province_municipality[selected_province_id]!!)
                town_spinner_adapter!!.setNotifyOnChange(true)
                town_spinner?.adapter = town_spinner_adapter
                if(::json_address.isInitialized){
                    val town = json_address.getString("municipality")
                    for(i in 0..town_spinner.count-1){
                        val current_town = town_spinner.getItemAtPosition(i) as Municipality
                        if(town.equals(current_town.municipality_name)){
                            town_spinner.setSelection(i,true)
                            break
                        }
                    }
                }


            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        town_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected_municipality = town_spinner.selectedItem as Municipality
                val selected_municipality_id = selected_municipality.municipality_code
                Log.d("tag",selected_municipality.municipality_name)
                bryg_spinner_adapter = ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,municipality_brgy[selected_municipality_id]!!)
                bryg_spinner_adapter!!.setNotifyOnChange(true)
                barangay_spinner?.adapter =  bryg_spinner_adapter

                if(::json_address.isInitialized){
                    val barangay = json_address.getString("barangay")
                    for(i in 0..barangay_spinner.count-1){
                        val current_barangay = barangay_spinner.getItemAtPosition(i) as String
                        if(barangay.equals(current_barangay)){
                            barangay_spinner.setSelection(i,true)
                            edit_profile_save.isEnabled = true
                            break
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        loadMunicipalities()
        loadBaranggays()
    }

    fun doLoadProvinces() = GlobalScope.async{
        loadProvinces1()
    }

    fun loadProvinces1() : ArrayList<Province> {
        val provinces = activity?.application?.assets?.open("refprovince.json")?.bufferedReader().use{
            it?.readText()
        }
        val arraylist_province : ArrayList<Province> = ArrayList()
        val json_provinces : JSONArray = JSONObject(provinces).optJSONArray("RECORDS")
        for(i in 0..json_provinces.length()-1){
            val province_object = Province(json_provinces[i] as JSONObject)
            arraylist_province.add(province_object)
            province_municipality[province_object.province_code] = mutableListOf()
        }
        return arraylist_province
    }

    fun loadMunicipalities() {
        val municipalities = activity?.application?.assets?.open("refcitymun.json")?.bufferedReader().use{
            it?.readText()
        }
        val json_municipality = JSONObject(municipalities).optJSONArray("RECORDS")
        for(i in 0..json_municipality.length()-1){
            val municipality_object = Municipality(json_municipality[i] as JSONObject)
            province_municipality[municipality_object.provincial_code]?.add(municipality_object)
            municipality_brgy[municipality_object.municipality_code] = mutableListOf()
            municipality_array_list.add(municipality_object)
        }
        activity?.runOnUiThread {
            town_spinner_adapter?.notifyDataSetChanged()
            if(::json_address.isInitialized){
                val town_name = json_address.getString("municipality")
                if(town_spinner != null){
                    for(i in 0..town_spinner.count-1){
                        if(town_name == (town_spinner.getItemAtPosition(i) as Municipality).municipality_name){
                            town_spinner.setSelection(i,true)
                            break
                        }
                    }
                }
            }
        }
    }


    fun loadBaranggays() {
        val brgys = activity?.application?.assets?.open("refbrgy.json")?.bufferedReader().use{
            it?.readText()
        }
        val json_brgys = JSONObject(brgys).optJSONArray("RECORDS")
        for(i in 0..json_brgys.length()-1) {
            val brgy_object = Barangay(json_brgys[i] as JSONObject)
            municipality_brgy[brgy_object.municipality_code]?.add(brgy_object.barangay_name)
        }

        activity?.runOnUiThread {
            bryg_spinner_adapter?.notifyDataSetChanged()
            if(::json_address.isInitialized){
                val barangay_name = json_address.getString("barangay")
                if(barangay_spinner != null){
                    for(i in 0..barangay_spinner.count-1){
                        if(barangay_name.equals(barangay_spinner.getItemAtPosition(i) as String))    {
                            barangay_spinner.setSelection(i,true)
                            edit_profile_save.isEnabled = true
                            break
                        }
                    }
                }
            }
        }
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
                    edit_profile_picture_container.setBackgroundColor(Color.parseColor("#ffffff"))
                    edit_profile_change_picture.setPadding(0,0,0,0)
                    GlideApp.with(activity!!).load(image_uri).apply(RequestOptions().circleCrop()).into(edit_profile_change_picture)
                    uploadImage()
                }
            }
            IMAGE_CHANGE_CAMERA -> {
                if(resultCode == Activity.RESULT_OK){
                    val m_photo_file_bitmap : Bitmap = BitmapFactory.decodeFile(photo_file.absolutePath)
                    val stream = ByteArrayOutputStream()
                    m_photo_file_bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream)
                    byte_array = stream.toByteArray()
                    edit_profile_picture_container.setBackgroundColor(Color.parseColor("#ffffff"))
                    edit_profile_change_picture.setPadding(0,0,0,0)
                    GlideApp.with(activity!!).load(m_photo_file_bitmap).apply(RequestOptions().circleCrop()).into(edit_profile_change_picture)
                    uploadImage()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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



    fun sendImageUploadOption(option: Int) {
        if(option == 1){ //start image picker intent
            if (ContextCompat.checkSelfPermission(context!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                val mActivity = context as Activity
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(mActivity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_READ_EXT_STORAGE) }
                else {
                    ActivityCompat.requestPermissions(mActivity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_READ_EXT_STORAGE)
                    }
            } else { openGallery() }
        }
        if(option == 0){
            if (ContextCompat.checkSelfPermission(context!!,Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                val mActivity = context as Activity
                if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity,Manifest.permission.CAMERA)){
                    ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.CAMERA),PERMISSION_OPEN_CAMERA)
                }
                else {
                    ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.CAMERA),PERMISSION_OPEN_CAMERA)
                }
         }
            else {
                Log.d("OpenCamera","error")
                openCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_READ_EXT_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (( grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Permission","AHhHHHH")
                    ToastUtil.bingwitDisplayCustomToastNotification(context!!,"Permission Denied")
                    //openGallery()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            PERMISSION_OPEN_CAMERA -> {
                if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    openCamera()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id){
            R.id.action_change_password -> {
                val fragment = ChangePasswordDialogFragment()
                fragment.show(activity?.supportFragmentManager,"change_password")
            }
        }
        return true
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

    fun initiateUserDetails(){

        val headers : HashMap<String,String> = HashMap()
        val authorization = "Bearer ${mSession.token()}"

        headers.put("Authorization",authorization)

        val params : HashMap<String,String> = HashMap()
        params.put("id",mSession.id()!!)

        ApiRequest.get(this@EditProfileFragment.context, "${API.GET_USER}/${mSession.id()}",headers,params,
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("user",response)
                        val json = JSONObject(response)
                        json_user = json.getJSONObject("user")
                        json_address = JSONObject(json_user.getString("address"))
                        edit_profile_fullname_edit_text?.setText(json_user.getString("full_name"))
                        edit_profile_address_edit_text?.setText(json_address.getString("street"))
                        edit_profile_phone_number_edit_text?.setText(json_user.getString("contact_number"))
                        edit_profile_change_picture?.setPadding(0,0,0,0)
                        edit_profile_picture_container?.setBackgroundColor((Color.parseColor("#ffffff")))
                        if(this@EditProfileFragment.view?.isAttachedToWindow == true){
                            GlideApp.with(this@EditProfileFragment).load(json_user.getString("image_url")).circleCrop().placeholder(R.drawable.ic_user_profile).into(edit_profile_change_picture)
                            for(i in 0..province_spinner.count-1){
                                val current_province = province_spinner.getItemAtPosition(i) as Province
                                if(current_province.province_name.equals(json_address.getString("province"))){
                                    province_spinner.setSelection(i,true)
                                    town_spinner_adapter = ArrayAdapter<Municipality>(context,R.layout.support_simple_spinner_dropdown_item,province_municipality[current_province.province_code])
                                    town_spinner.adapter = town_spinner_adapter
                                    break
                                }
                            }
                        }
                        current_user_number = json_user.getString("contact_number")

                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                        Log.d("Error",error.toString())
                    }
                })
    }

    fun getAreas () {
        ApiRequest.get(context,API.GET_AREAS, HashMap(), HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("Areas",response)
                        val json = JSONObject(response)
                        Log.d("Get areas",response)
                        val areas_json_array = json.getJSONObject("area").getJSONArray("rows")
                        for(i in 0..areas_json_array.length()-1){
                            areas_dropdown_spinner_arraylist.add(Area(areas_json_array[i] as JSONObject))
                        }
                        if(this@EditProfileFragment.view?.isAttachedToWindow == true){
                            val edit_profile_areas_spinner_adapter : ArrayAdapter<Area> = ArrayAdapter(this@EditProfileFragment.context,android.R.layout.simple_spinner_dropdown_item,areas_dropdown_spinner_arraylist)
                            edit_profile_area_spinner?.adapter = edit_profile_areas_spinner_adapter
                            if(::json_user.isInitialized){
                                val user_area_id = json_user.optJSONObject("area").optString("area")
                                for(i in 0..edit_profile_area_spinner.count-1){
                                    val current_area = edit_profile_area_spinner.getItemAtPosition(i) as Area
                                    if(current_area.id == user_area_id) {
                                        edit_profile_area_spinner.setSelection(i,true)
                                        break
                                    }
                                }
                            }
                        }

                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                    }
                })
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        fun newInstance(param1: String, param2: String): EditProfileFragment {
            val fragment = EditProfileFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
