package gab.cdi.bingwitproducer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class EditProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
