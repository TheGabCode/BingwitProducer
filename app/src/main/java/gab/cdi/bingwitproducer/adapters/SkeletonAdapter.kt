package gab.cdi.bingwitproducer.adapters
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.models.Product
import gab.cdi.bingwitproducer.models.Rating
import gab.cdi.bingwitproducer.models.Transaction
import org.w3c.dom.Text

/**
 * Created by Default on 11/10/2018.
 */


class SkeletonAdapter(skeleton_count : Int, val context : Context?, skeleton_layout_id : Int) : RecyclerView.Adapter<SkeletonAdapter.ViewHolder>() {
    var adapter_skeleton_count = skeleton_count
    var layout_id = skeleton_layout_id
    override fun getItemCount(): Int {
        return adapter_skeleton_count
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout_id, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {





    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    }
}