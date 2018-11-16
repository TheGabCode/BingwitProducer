package gab.cdi.bingwitproducer.activities


import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.android.volley.VolleyError
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.*
import gab.cdi.bingwitproducer.fragments.*
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        TransactionHistoryFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnFragmentInteractionListener,
        AddProductFragment.OnFragmentInteractionListener,
        EditProductFragment.OnFragmentInteractionListener,
        ViewProductsFragment.OnFragmentInteractionListener,
        ViewProductFragment.OnFragmentInteractionListener,
        ViewTransactionsFragment.OnFragmentInteractionListener,
        ViewTransactionFragment.OnFragmentInteractionListener,
        RemoveProductDialogFragment.OnFragmentInteractionListener,
        TimePickerDialogFragment.OnFragmentInteractionListener,
        RatingsFragment.OnFragmentInteractionListener,
        ForgotPasswordDialogFragment.OnFragmentInteractionListener,
        ChangePasswordDialogFragment.OnFragmentInteractionListener,
        CustomAlertDialogFragment.OnFragmentInteractionListener,
        UploadImageOptionsDialogFragment.OnFragmentInteractionListener,
        ViewProductsTabFragment.OnFragmentInteractionListener,
        ConfirmTransactionStatusDialogFragment.OnFragmentInteractionListener{
    var drawer_toggle = 0
    var toggle : ActionBarDrawerToggle? = null
    private lateinit var mSession : Session
    private lateinit var fragment : Fragment

    lateinit var fm : FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSession = Session(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fm = supportFragmentManager
        fab.setOnClickListener { view ->
            displaySelectedId(R.id.nav_add_product, HashMap())
            //TimePickerDialogFragment().show(supportFragmentManager,"time_picker")
        }

        toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle!!)
        toggle?.syncState()
        displaySelectedId(R.id.nav_view_products, hashMapOf("tab_position" to 0))
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        Log.d("backstack_count",fm.backStackEntryCount.toString())

        var mFragment = fm.findFragmentById(R.id.bingwit_navigation_activity)

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


        if(mFragment is EditProfileFragment){
            fm.popBackStackImmediate()
            return
        }

        if(mFragment is ViewTransactionFragment){
            fm.popBackStackImmediate()
//            val position = mFragment.mPosition
//            mFragment = supportFragmentManager.findFragmentById(R.id.bingwit_navigation_activity) as TransactionHistoryFragment
//            mFragment.mPosition = position
//            mFragment.initUI()

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
        val mFragment = fm.findFragmentById(R.id.bingwit_navigation_activity)
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
                if(mFragment is ViewProductsFragment || mFragment is ViewProductFragment || mFragment is AddProductFragment){
                    return
                }

                fab.show()
                tag = "view_products_fragment"
                fragment = ViewProductsFragment.newInstance(0)
                fragmentReplaceBackStack(fragment,tag)
                return
            }
            R.id.nav_transaction_history -> {
                if(mFragment is TransactionHistoryFragment){
                    return
                }
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
                if(mFragment is ViewProductsFragment || mFragment is ViewProductFragment || mFragment is AddProductFragment){
                    return
                }
                fab.show()
                tag = "view_products_fragment"
                fragment = ViewProductsFragment.newInstance(params["tab_position"] as Int)
            }
//            R.id.nav_view_product -> {
//                tag = "view_product_fragment"
//                fragment = ViewProductFragment.newInstance(params["product_id"] as String, params["product_type"] as String)
//                fragmentAddBackStack(fragment,tag)
//                return
//            }
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
}
