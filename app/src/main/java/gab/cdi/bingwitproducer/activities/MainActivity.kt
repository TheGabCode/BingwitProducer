package gab.cdi.bingwitproducer.activities


import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.replace
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.firebase.iid.FirebaseInstanceId

import com.instacart.library.truetime.InvalidNtpServerResponseException
import com.instacart.library.truetime.TrueTime
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.*
import gab.cdi.bingwitproducer.R.id.*
import gab.cdi.bingwitproducer.dependency_modules.GlideApp
import gab.cdi.bingwitproducer.fragments.*
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.ProducerProduct
import gab.cdi.bingwitproducer.utils.DialogUtil
import gab.cdi.bingwitproducer.utils.SocketUtil
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_view_products.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, BingwitFragment {
    var drawer_toggle = 0
    var toggle : ActionBarDrawerToggle? = null
    lateinit var mSession : Session
    lateinit var mSocket : Socket
    var mFragment : Fragment? = null
    lateinit var fm : FragmentManager
    var showToastUpdates = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fm = supportFragmentManager
        //instantiate session
        instantiateSession()
        loadUser()
        //try to instantiate socket
        instantiateSocketIO()
        authenticateSocketIO()
        setEventListeners()


        //fab on click listener
        fab.setOnClickListener { view ->
            displaySelectedId(R.id.nav_add_product, HashMap())
        }

        //drawer listener
        toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle!!)
        toggle?.syncState()

        //display first fragment
        displaySelectedId(R.id.nav_view_products, hashMapOf("tab_position" to 0))
        nav_view.setNavigationItemSelectedListener(this)

        instantiateTrueTime(3)
        val token : String?
        if(mSession.firebaseToken() != null && mSession.firebaseToken() != ""){
            token = mSession.firebaseToken()
            Log.d("firebase_token_stored",token)
        }
        else{
            token = FirebaseInstanceId.getInstance().getToken()
            mSession.storeFirebaseToken(token)
            Log.d("firebase_token_new",token)
        }
        storeTokenForSubscription(mSession.firebaseToken()!!)
    }


    override fun onBackPressed() {
        Log.d("backstack_count",fm.backStackEntryCount.toString())

        val mFragment = fm.findFragmentById(R.id.bingwit_navigation_activity)

        if(mFragment is ViewProductFragment){
            fab.show()
            fm.popBackStackImmediate()
            return
        }

        if(mFragment is AddProductFragment){
            fab.show()
            fm.popBackStackImmediate()
            return
        }

        if(mFragment is EditProductFragment){
            fm.popBackStackImmediate()
            return
        }


        if(mFragment is ViewTransactionFragment){
            fm.popBackStackImmediate()
            return
        }

        if(mFragment is TransactionStatusLogsFragment){
            fm.popBackStackImmediate()
            return
        }


        if (drawer_layout.isDrawerOpen(GravityCompat.START) && drawer_toggle == 0) {
            finish()
        } else if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            drawer_toggle = 1
            return
        }


        if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_toggle = 0
            drawer_layout.openDrawer(GravityCompat.START)
            return
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        val params : HashMap<String,Any> = HashMap()
        if(id == R.id.nav_transaction_history){
            params.put("position",0)
        }

        displaySelectedId(id, params)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun displaySelectedId(id : Int,params : HashMap<String,Any>){
        mFragment = fm.findFragmentById(R.id.bingwit_navigation_activity)
        fab.hide()
        var fragment : Fragment? = null
        var tag : String? = null
        when(id){
            R.id.nav_profile -> {
                if(mFragment is ProfileFragment || mFragment is EditProfileFragment) return
                tag = "profile_fragment"
                fragment = ProfileFragment()
                fragmentReplaceBackStack(fragment,tag)
                return
            }
            R.id.nav_dashboard -> {
                if(mFragment is ViewProductsFragment || mFragment is ViewProductFragment || mFragment is AddProductFragment) return
                fab.show()
                tag = "view_products_fragment"
                fragment = ViewProductsFragment.newInstance(0)
                fragmentReplaceBackStack(fragment,tag)
                return
            }
            R.id.nav_transaction_history -> {
                if(mFragment is TransactionHistoryFragment) return
                tag = "transaction_history_fragment"
                fragment = TransactionHistoryFragment.newInstance(params["position"] as Int)
                fragmentReplaceBackStack(fragment,tag)
                return
            }
            R.id.nav_sign_out -> {
                signOut()
            }
            R.id.nav_edit_profile -> {
                tag = "edit_profile_fragment"
                fragment = EditProfileFragment()
                fragmentAddBackStack(fragment,tag)
                return
            }
            R.id.nav_add_product -> {
                tag = "add_product_fragment"
                fragment = AddProductFragment()
                fragmentAddBackStack(fragment,tag)
                return
            }
            R.id.nav_view_products -> {
                if(mFragment is ViewProductsFragment || mFragment is ViewProductFragment || mFragment is AddProductFragment) return
                fab.show()
                tag = "view_products_fragment"
                fragment = ViewProductsFragment.newInstance(params["tab_position"] as Int)
            }
            R.id.nav_edit_product -> {
                tag = "edit_product_fragment"
                fragment = EditProductFragment.newInstance(params["product_id"] as String,params["product_type"] as String)
                fragmentAddBackStack(fragment,tag)
                return
            }
            R.id.nav_view_transaction -> {
                tag = "view_transaction_product"
                fragment = ViewTransactionFragment.newInstance(params["position"] as Int,params["transaction_id"] as String)
                fragmentAddBackStack(fragment,tag)
                return
            }
        }
        if(fragment != null){
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.bingwit_navigation_activity,fragment,tag)
            ft.commit()
        }
    }

    fun fragmentReplaceBackStack(fragment: Fragment, tag: String){
        fm.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            replace(R.id.bingwit_navigation_activity, fragment, tag)
        }.commit()
    }

    fun fragmentAddBackStack(fragment: Fragment, tag: String){
        fm.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(tag)
            add(R.id.bingwit_navigation_activity, fragment, tag)
        }.commit()
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.nav_profile -> {
                fab.hide()
                mFragment = fm.findFragmentById(R.id.bingwit_navigation_activity)
                if(mFragment is ProfileFragment) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return
                }
                val tag = "profile_fragment"
                val fragment = ProfileFragment()
                fragmentReplaceBackStack(fragment,tag)
                drawer_layout.closeDrawer(GravityCompat.START)
                return
            }

            R.id.user_edit -> {
                fab.hide()
                mFragment = fm.findFragmentById(R.id.bingwit_navigation_activity)
                if(mFragment is EditProfileFragment) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return
                }
                fragmentReplaceBackStack(EditProfileFragment(),"edit_profile_fragment")
            }
        }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    fun setToolbar(isShowBackButton:Boolean){
        val anim: ValueAnimator? = if (isShowBackButton){
            ValueAnimator.ofFloat(0F, 1F)
        }else{
            ValueAnimator.ofFloat(1F, 0F)
        }

        anim?.addUpdateListener { valueAnimator ->
            val slideOffset = valueAnimator.animatedValue as Float
            toggle?.onDrawerSlide(drawer_layout, slideOffset)
        }
        anim?.interpolator = DecelerateInterpolator()
        anim?.duration = 400
        anim?.start()
        setDrawerState(!isShowBackButton)
    }

    fun instantiateSocketIO() {
        try{
            mSocket = IO.socket(API.SOCKET)

            Log.d("socket_io","instantiating socket")

        }
        catch (e : SocketIOException){
            e.printStackTrace()
            Toast.makeText(this,"IOException",Toast.LENGTH_LONG).show()
        }
        catch (e : Exception){
            e.printStackTrace()
            Toast.makeText(this,"Exception",Toast.LENGTH_LONG).show()
        }
    }

    fun authenticateSocketIO () {
        mSocket.on("authenticated",onAuthenticated())
        mSocket.emit("authenticate",mSession.token())
        Log.d("socket_io",mSession.token())

    }

    fun onAuthenticated() = object : Emitter.Listener {
        override fun call(vararg args: Any?) {
            Log.d("socket_io",args[0].toString())
        }
    }
    fun instantiateTrueTime(max_tries : Int) {
        var count = 0
        AsyncTask.execute(object : Runnable{
            override fun run() {
                while(count < max_tries){
                    try{
                        Log.d("woops","trying to init in main activity")
                        TrueTime.build().initialize()
                        break
                    }
                    catch (e :InvalidNtpServerResponseException){
                        Log.d("woops","invalidntpserverresponseexception")
                        count++
                    }
                    catch (e : Exception){
                        Log.d("woops","failed dammit")
                        count++
                    }
                }
            }
        })
    }

    fun setEventListeners() {
        mSocket.connect()
        mSocket.on("auction products",onAuctionProductEvent())
        mSocket.on("products",onProductEvent())
        mSocket.on("transactions",onTransactionEvent())


    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.off()
        mSocket.disconnect()
    }

    fun onDisconnect() = object : Emitter.Listener{
        override fun call(vararg args: Any?) {
            Log.d("socket_io","Disconnected")
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(!!mSocket.connected()) mSocket.connect()
    }



    fun onProductEvent() = object : Emitter.Listener{
        override fun call(vararg args: Any?) {

            if(!mSocket.connected()) {
                Log.d("socket_event","disconnected")
                instantiateSocketIO()
                authenticateSocketIO()
                mSocket.connect()
            }
            Log.d("socket_io",args[0].toString())
            runOnUiThread(object : Runnable{
                override fun run() {
                        val socket_json_response = args[0] as JSONObject
                        val method = socket_json_response.getString("method")
                        val product_id = socket_json_response.optJSONObject("product").optString("id")
                        when(method){
                                "add"-> {
                                    SocketUtil.addProductSocket(this@MainActivity,product_id)
                                    return
                                }
                                "edit" -> {
                                    SocketUtil.editProductSocket(this@MainActivity,product_id)
                                    return
                                }
                                "delete" -> {
                                    SocketUtil.deleteProductSocket(this@MainActivity,product_id)
                                    return
                                }
                            }
                }
            })
        }
    }
    fun onAuctionProductEvent() = object : Emitter.Listener{
        override fun call(vararg args: Any?) {
            if(!mSocket.connected()){
                Log.d("socket_event","disconnected")
                instantiateSocketIO()
                authenticateSocketIO()
                mSocket.connect()

            }
            Log.d("socket_io",args[0].toString())
            runOnUiThread(object : Runnable{
                override fun run() {
                    val socket_json_response = args[0] as JSONObject
                    val method = socket_json_response.getString("method")
                    val product_id = socket_json_response.optJSONObject("product_auction").optString("id")
                    when(method){
                        "add"-> {
                            SocketUtil.addAuctionProductSocket(this@MainActivity,product_id)
                            return
                        }
                        "edit" -> {
                            SocketUtil.editAuctionProductSocket(this@MainActivity,product_id)
                            return
                        }
                        "delete" -> {
                            SocketUtil.deleteAuctionProductSocket(this@MainActivity,product_id)
                            return
                        }
                    }
                }
            })
        }
    }

    fun onTransactionEvent() = object : Emitter.Listener{
        override fun call(vararg args: Any?) {
            Log.d("socket_io",args[0].toString())
            runOnUiThread(object : Runnable{
                override fun run() {
                    val socket_json_response = args[0] as JSONObject
                    val method = socket_json_response.getString("method")
                    val transaction_id = socket_json_response.optJSONObject("transaction").optString("id")
                    Log.d("transaction_event",args[0].toString())
                    when(method){
                        "add"-> {
                            SocketUtil.addTransactionSocket(this@MainActivity,transaction_id)
                            return
                        }
                        "edit" -> {
                            SocketUtil.editTransactionSocket(this@MainActivity,transaction_id)
                            return
                        }
                    }
                }
            })
        }
    }

    fun instantiateSession(){
        mSession = Session(this)
    }

    private fun setDrawerState(isEnabled: Boolean) {
        if (isEnabled) {
            toggle?.isDrawerIndicatorEnabled = isEnabled
            toggle?.syncState()
            drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            toolbar?.setNavigationOnClickListener {
                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                } else {
                    drawer_layout.openDrawer(GravityCompat.START)
                }
            }
        } else {
            drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toolbar?.setNavigationOnClickListener{ onBackPressed() }
        }
    }

    fun signOut(){
        val params : HashMap<String,String> = HashMap()
        val headers : HashMap<String,String> = HashMap()
        val message = "Signing out"
        ApiRequest.post(this, API.SIGN_OUT,params,headers,message,
            object : ApiRequest.URLCallback{
                override fun didURLResponse(response: String) {
                    mSocket.close()
                    mSocket.disconnect()
                    Log.d("Sign out",response)
                    val json = JSONObject(response)
                    if(json.getBoolean("success") == true){
                        mSession.deauthorize()
                        finish()
                        val intent = Intent(this@MainActivity,LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            },
            object : ApiRequest.ErrorCallback{
                override fun didURLError(error: VolleyError) {
                    CustomAlertDialogFragment.newInstance("Failed to log out, try again",2000).show(supportFragmentManager,"failed_sign_out")
                }
            })
    }

    override fun onPause() {
        super.onPause()
        showToastUpdates = false
    }


    override fun onResume() {
        super.onResume()
        showToastUpdates = true
    }


    fun loadUser(){
        val headers = HashMap<String,String>()
        val authorization = "Bearer ${mSession.token()}"
        headers.put("Authorization",authorization)
        ApiRequest.get(this,"${API.GET_USER}/${mSession.id()}",headers, HashMap(),
                object : ApiRequest.URLCallback{
                    override fun didURLResponse(response: String) {
                        Log.d("user",response)
                        val json_user = JSONObject(response).optJSONObject("user")
                        user_username_textview?.text = json_user.optString("full_name")
                        if(user_profile_image != null){
                            GlideApp.with(this@MainActivity)
                                    .load(json_user.optString("image_url"))
                                    .circleCrop()
                                    .into(user_profile_image)
                        }

                    }
                },
                object : ApiRequest.ErrorCallback{
                    override fun didURLError(error: VolleyError) {
                    }
                })
    }

    fun storeTokenForSubscription(firebaseToken : String){
        val headers = HashMap<String,String>()
        val authorization = "Bearer ${mSession.token()}"
        headers.put("Authorization",authorization)
        headers.put("Content-Type","application/x-www-form-urlencoded")
        val params = HashMap<String,String>()
        params.put("token",firebaseToken)
        ApiRequest.post(this,API.SUBSCRIBE,headers,params,null,
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("notifs",response)
                    }
                },
                object : ApiRequest.ErrorCallback {
                    override fun didURLError(error: VolleyError) {

                    }

                })
    }
}
