package gab.cdi.bingwitproducer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ViewProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
