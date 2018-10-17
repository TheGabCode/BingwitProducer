package gab.cdi.bingwitproducer.activities


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import gab.cdi.bingwitproducer.*
import gab.cdi.bingwitproducer.R.id.*
import gab.cdi.bingwitproducer.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, TransactionHistoryFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, EditProfileFragment.OnFragmentInteractionListener, AddProductFragment.OnFragmentInteractionListener, EditProductFragment.OnFragmentInteractionListener, ViewProductsFragment.OnFragmentInteractionListener, ViewProductFragment.OnFragmentInteractionListener, ViewTransactionsFragment.OnFragmentInteractionListener, ViewTransactionFragment.OnFragmentInteractionListener, RemoveProductDialogFragment.OnFragmentInteractionListener, DatePickerDialogFragment.OnFragmentInteractionListener, RatingsFragment.OnFragmentInteractionListener, ForgotPasswordDialogFragment.OnFragmentInteractionListener, ChangePasswordDialogFragment.OnFragmentInteractionListener{
    var drawer_toggle = 0
    private lateinit var fragment : Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            displaySelectedId(R.id.nav_add_product, HashMap())
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        displaySelectedId(R.id.nav_view_products, HashMap())
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        var mFragment = supportFragmentManager.findFragmentById(R.id.bingwit_navigation_activity)
        if(mFragment is ViewProductFragment || mFragment is AddProductFragment){
            displaySelectedId(R.id.nav_view_products,HashMap())
            return
        }

        if(mFragment is EditProductFragment){
            var params : HashMap<String,Any> = HashMap()
            params.put("product_id",mFragment.product_id as Any)
            displaySelectedId(R.id.nav_view_product,params)
            return
        }

        if(mFragment is EditProfileFragment){
            displaySelectedId(R.id.nav_profile,HashMap())
            return
        }

        if(mFragment is ViewTransactionFragment){
            var params : HashMap<String,Any> = HashMap()
            params.put("position",mFragment.mPosition as Any)
            displaySelectedId(R.id.nav_transaction_history,params)
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



//        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
//            drawer_layout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
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
        var fragment : Fragment? = null
        var tag : String? = null
        when(id){
            R.id.nav_profile -> {
                tag = "profile_fragment"
                fragment = ProfileFragment()
            }
            R.id.nav_dashboard -> {
                tag = "view_products_fragment"
                fragment = ViewProductsFragment()
            }
            R.id.nav_transaction_history -> {
                tag = "transaction_history_fragment"
                fragment = TransactionHistoryFragment.newInstance(params["position"] as Int)
            }
            R.id.nav_sign_out -> {
                finish()
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_edit_profile -> {
                tag = "edit_profile_fragment"
                fragment = EditProfileFragment()
            }
            R.id.nav_add_product -> {
                tag = "add_product_fragment"
                fragment = AddProductFragment()
            }
            R.id.nav_view_products -> {
                tag = "view_products_fragment"
                fragment = ViewProductsFragment()
            }
            R.id.nav_view_product -> {
                tag = "view_product_fragment"
                fragment = ViewProductFragment.newInstance(params["product_id"] as String)
            }
            R.id.nav_edit_product -> {
                tag = "edit_product_fragment"
                fragment = EditProductFragment.newInstance(params["product_id"] as String)
            }
            R.id.nav_view_transaction -> {
                tag = "view_transaction_product"
                fragment = ViewTransactionFragment.newInstance(params["position"] as Int)
            }
        }

        if(fragment != null){
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.bingwit_navigation_activity,fragment,tag)
            ft.commit()
        }
    }

    override fun onClick(v: View?) {
//        val id = v?.id
//        when(id) {
//            R.id.edit_product_button -> {
//
//            }
//            R.id.remove_product_button -> {
//
//            }
//        }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
