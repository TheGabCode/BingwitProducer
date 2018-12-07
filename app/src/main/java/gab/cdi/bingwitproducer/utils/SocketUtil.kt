package gab.cdi.bingwitproducer.utils

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import gab.cdi.bingwitproducer.R
import gab.cdi.bingwitproducer.activities.MainActivity
import gab.cdi.bingwitproducer.adapters.ProducerAuctionProductAdapter
import gab.cdi.bingwitproducer.fragments.*
import gab.cdi.bingwitproducer.https.API
import gab.cdi.bingwitproducer.https.ApiRequest
import gab.cdi.bingwitproducer.models.ProducerAuctionProduct
import gab.cdi.bingwitproducer.models.ProducerProduct
import gab.cdi.bingwitproducer.models.Transaction
import kotlinx.android.synthetic.main.bingwit_custom_toast_notification.view.*
import kotlinx.android.synthetic.main.fragment_view_products.*
import org.json.JSONObject

object SocketUtil {
    fun addProductSocket(context: Context, product_id: String) {
        val mActivity = context as MainActivity
        val mFragment = mActivity.fm.findFragmentById(R.id.bingwit_navigation_activity)
        ApiRequest.get(context, "${API.GET_PRODUCT_BY_ID}/$product_id", hashMapOf("Authorization" to "Bearer ${mActivity.mSession.token()}"), hashMapOf(),
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        val product_object = ProducerProduct(JSONObject(response).optJSONObject("product"))
                        if (mFragment is ViewProductsFragment || mFragment is AddProductFragment || mFragment is EditProductFragment || mFragment is ViewProductFragment) {
                            val fragment = mActivity.fm.findFragmentByTag("view_products_fragment") as ViewProductsFragment
                            val inner_fragment = fragment.getInnerFragment("fixed")
                            inner_fragment.fixed_products_array_list.add(0, product_object)
                            inner_fragment.fixed_products_adapter?.notifyItemInserted(inner_fragment.fixed_products_array_list.size - 1)
                            inner_fragment.fixed_products_adapter?.notifyDataSetChanged()
                        }
                        val message = "New product added! ${product_object.name}"
                        ToastUtil.bingwitDisplayCustomToastNotification(context, message)
                    }
                },
                object : ApiRequest.ErrorCallback {
                    override fun didURLError(error: VolleyError) {}
                })
    }

    fun editProductSocket(context: Context, product_id: String) {
        val mActivity = context as MainActivity
        val mFragment = mActivity.fm.findFragmentById(R.id.bingwit_navigation_activity)
        ApiRequest.get(context, "${API.GET_PRODUCT_BY_ID}/$product_id", hashMapOf("Authorization" to "Bearer ${mActivity.mSession.token()}"), hashMapOf(),
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        val product_object = ProducerProduct(JSONObject(response).optJSONObject("product"))
                        if (mFragment is ViewProductsFragment || mFragment is AddProductFragment || mFragment is EditProductFragment || mFragment is ViewProductFragment) {
                            val fragment = mActivity.fm.findFragmentByTag("view_products_fragment") as ViewProductsFragment
                            val inner_fragment = fragment.getInnerFragment("fixed")
                            for (i in 0..inner_fragment.fixed_products_array_list.size - 1) {
                                val product_object_iterated = inner_fragment.fixed_products_array_list[i]
                                if (product_id == product_object_iterated.id) {
                                    inner_fragment.fixed_products_array_list.set(i, product_object)
                                    inner_fragment.fixed_products_adapter?.notifyItemChanged(i)
                                    inner_fragment.fixed_products_adapter?.notifyDataSetChanged()
                                    break
                                }
                            }
                        }
                        val message = "${product_object.name} updated!"
                        ToastUtil.bingwitDisplayCustomToastNotification(context, message)
                    }
                },
                object : ApiRequest.ErrorCallback {
                    override fun didURLError(error: VolleyError) {}
                })


    }

    fun deleteProductSocket(context: Context, product_id: String) {
        val mActivity = context as MainActivity
        val mFragment = mActivity.fm.findFragmentById(R.id.bingwit_navigation_activity)
        if (mFragment is ViewProductsFragment || mFragment is AddProductFragment || mFragment is EditProductFragment || mFragment is ViewProductFragment) {
            val fragment = mActivity.fm.findFragmentByTag("view_products_fragment") as ViewProductsFragment
            val inner_fragment = fragment.getInnerFragment("fixed")
            for (i in 0..inner_fragment.fixed_products_array_list.size - 1) {
                val product_object_iterated = inner_fragment.fixed_products_array_list[i]
                if (product_id == product_object_iterated.id) {
                    inner_fragment.fixed_products_array_list.removeAt(i)
                    inner_fragment.fixed_products_adapter?.notifyItemRemoved(i)
                    val message = "${product_object_iterated.name} removed!"
                    ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)
                    break
                }
            }
            return
        } else {
            val message = "A product has been removed!"
            ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)
        }

    }

    fun addAuctionProductSocket(context: Context, product_id: String) {
        val mActivity = context as MainActivity
        val mFragment = mActivity.fm.findFragmentById(R.id.bingwit_navigation_activity)
        ApiRequest.get(context, "${API.GET_PRODUCT_AUCTION_BY_ID}/$product_id", hashMapOf("Authorization" to "Bearer ${mActivity.mSession.token()}"), hashMapOf(),
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        val product_object = ProducerAuctionProduct(JSONObject(response).optJSONObject("product"))
                        if (mFragment is ViewProductsFragment || mFragment is ViewProductFragment || mFragment is AddProductFragment || mFragment is EditProductFragment) {
                            val fragment = mActivity.fm.findFragmentByTag("view_products_fragment") as ViewProductsFragment
                            val inner_fragment = fragment.getInnerFragment("auction")
//                            val v = inner_fragment.product_placement_recyclerview.getChildViewHolder(inner_fragment.product_placement_recyclerview.getChildAt(inner_fragment.auction_products_array_list.size - 1)) as ProducerAuctionProductAdapter.ViewHolder
//                            v.timer_countdown_to_start_bidding?.cancel()
//                            v.timer_start_bidding?.cancel()
//                            v.timer_countdown_ten_minutes?.cancel()
                            inner_fragment.auction_products_array_list.add(product_object)
                            inner_fragment.auction_products_adapter?.notifyItemInserted(inner_fragment.auction_products_array_list.size - 1)
                            inner_fragment.auction_products_adapter?.notifyDataSetChanged()
                        }
                        val message = "New auction product added! ${product_object.name}"
                        ToastUtil.bingwitDisplayCustomToastNotification(context, message)
                    }
                },
                object : ApiRequest.ErrorCallback {
                    override fun didURLError(error: VolleyError) {}
                })
    }


    fun editAuctionProductSocket(context: Context, product_id: String) {
        val mActivity = context as MainActivity
        val mFragment = mActivity.fm.findFragmentById(R.id.bingwit_navigation_activity)
        ApiRequest.get(context, "${API.GET_PRODUCT_AUCTION_BY_ID}/$product_id", hashMapOf("Authorization" to "Bearer ${mActivity.mSession.token()}"), hashMapOf(),
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        val product_object = ProducerAuctionProduct(JSONObject(response).optJSONObject("product"))
                        val message = "Auction product ${product_object.name} updated!"
                        if (mActivity.showToastUpdates) ToastUtil.bingwitDisplayCustomToastNotification(context, message)
                        if (mFragment is ViewProductsFragment || mFragment is AddProductFragment || mFragment is EditProductFragment || mFragment is ViewProductFragment) {
                            val fragment = mActivity.fm.findFragmentByTag("view_products_fragment") as ViewProductsFragment
                            val inner_fragment = fragment.getInnerFragment("auction")
                            for (i in 0..inner_fragment.auction_products_array_list.size - 1) {
                                val product_object_iterated = inner_fragment.auction_products_array_list[i]
                                if (product_id == product_object_iterated.auction_id) {
                                    val v = inner_fragment.product_placement_recyclerview.findViewHolderForAdapterPosition(i) as ProducerAuctionProductAdapter.ViewHolder
                                    //qToast.makeText(context,v.product_name.text.toString(),Toast.LENGTH_LONG).show()
                                    v.timer_countdown_to_start_bidding?.cancel()
                                    v.timer_start_bidding?.cancel()
                                    v.timer_countdown_ten_minutes?.cancel()
                                    inner_fragment.auction_products_array_list.set(i, product_object)
                                    inner_fragment.auction_products_adapter?.notifyItemChanged(i)
                                    break
                                }
                            }
                        }
                    }
                },
                object : ApiRequest.ErrorCallback {
                    override fun didURLError(error: VolleyError) {}
                })

    }

    fun deleteAuctionProductSocket(context: Context, product_id: String) {
        val mActivity = context as MainActivity
        val mFragment = mActivity.fm.findFragmentById(R.id.bingwit_navigation_activity)
        if (mFragment is ViewProductsFragment || mFragment is ViewProductFragment || mFragment is AddProductFragment || mFragment is EditProductFragment) {
            val fragment = mActivity.fm.findFragmentByTag("view_products_fragment") as ViewProductsFragment
            val inner_fragment = fragment.getInnerFragment("auction")
            inner_fragment.initUI()
//            for (i in 0..inner_fragment.auction_products_array_list.size-1) {
//                val product_object_iterated = inner_fragment.auction_products_array_list[i]
//                if (product_id == product_object_iterated.auction_id) {
//                    val v = inner_fragment.product_placement_recyclerview.findViewHolderForAdapterPosition(i) as ProducerAuctionProductAdapter.ViewHolder
//                    v.auction_cancelled_indicator.visibility = View.VISIBLE
//                    v.product_auction_timer.visibility = View.INVISIBLE
//                    v.timer_countdown_to_start_bidding?.cancel()
//                    v.timer_start_bidding?.cancel()
//                    v.timer_countdown_ten_minutes?.cancel()
//                    //inner_fragment.auction_products_array_list.removeAt(i)
////                    inner_fragment.auction_products_adapter?.notifyDataSetChanged()
//                    //  inner_fragment.auction_products_adapter?.notifyItemRemoved(i)
//                    val message = "${inner_fragment.product_placement_recyclerview.childCount} child count!"
//                    ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)
//                    break
//                }
//            }
                return
        }
        val message = "An auction has been removed!"
        ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)
    }

    fun addTransactionSocket(context: Context, transaction_id: String) {
        val mActivity = context as MainActivity
        val mFragment = mActivity.fm.findFragmentById(R.id.bingwit_navigation_activity)
        val headers: HashMap<String, String> = HashMap()
        val authorization = "Bearer ${mActivity.mSession.token()}"
        headers.put("Authorization", authorization)
        headers.put("Content-Type", "application/x-www-form-urlencoded")
        ApiRequest.get(context, "${API.GET_TRANSACTION_PRODUCTS}/${mActivity.mSession.id()}/transactions/$transaction_id", headers, HashMap(),
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("socket_io", response)
                        val transaction_object = Transaction(JSONObject(response).optJSONObject("transaction"))
                        if (mFragment is TransactionHistoryFragment || mFragment is ViewTransactionFragment) {
                            val fragment = mActivity.fm.findFragmentByTag("transaction_history_fragment") as TransactionHistoryFragment
                            val inner_fragment = fragment.getInnerFragment("on-going")
                            inner_fragment.transactions_on_going_arraylist.add(0, transaction_object)
                            inner_fragment.on_going_transaction_adapter?.notifyItemInserted(inner_fragment.transactions_on_going_arraylist.size - 1)
                            inner_fragment.on_going_transaction_adapter?.notifyDataSetChanged()
                        }
                        val message = "An order has been placed with tracking number ${transaction_object.tracking_number}!"
                        ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)
                    }
                },
                object : ApiRequest.ErrorCallback {
                    override fun didURLError(error: VolleyError) {
                    }
                })
    }


    fun editTransactionSocket(context: Context, transaction_id: String) {
        val mActivity = context as MainActivity
        val mFragment = mActivity.fm.findFragmentById(R.id.bingwit_navigation_activity)
        val headers: HashMap<String, String> = HashMap()
        var message : String =""
        val authorization = "Bearer ${mActivity.mSession.token()}"
        headers.put("Authorization", authorization)
        headers.put("Content-Type", "application/x-www-form-urlencoded")
        ApiRequest.get(context, "${API.GET_TRANSACTION_PRODUCTS}/${mActivity.mSession.id()}/transactions/$transaction_id", headers, HashMap(),
                object : ApiRequest.URLCallback {
                    override fun didURLResponse(response: String) {
                        Log.d("socket_io", response)
                        val transaction_object = Transaction(JSONObject(response).optJSONObject("transaction"))
                        val status = transaction_object.status
                        if (mFragment is TransactionHistoryFragment || mFragment is ViewTransactionFragment) {
                            val fragment = mActivity.fm.findFragmentByTag("transaction_history_fragment") as TransactionHistoryFragment
                            var inner_fragment: ViewTransactionsFragment
                            if (status == "order placed" || status == "ready for delivery" || status == "shipped") {
                                inner_fragment = fragment.getInnerFragment("on-going")
                                for(i in 0..inner_fragment.transactions_on_going_arraylist.size-1){
                                    val transaction = inner_fragment.transactions_on_going_arraylist[i]
                                    if (transaction.id == transaction_id) {
                                        inner_fragment.transactions_on_going_arraylist[i] = transaction_object
                                        inner_fragment.on_going_transaction_adapter?.notifyItemChanged(i)
                                        inner_fragment.on_going_transaction_adapter?.notifyDataSetChanged()
                                        break
                                    }
                                }
                                message = "Order ${transaction_object.tracking_number} updated into ${transaction_object.status}!"
                            }
                            else if(status == "cancelled"){
                                inner_fragment = fragment.getInnerFragment("on-going")
                                for(i in 0..inner_fragment.transactions_on_going_arraylist.size-1){
                                    val transaction = inner_fragment.transactions_on_going_arraylist[i]
                                    if (transaction.id == transaction_id) {
                                        inner_fragment.transactions_on_going_arraylist.removeAt(i)
                                        inner_fragment.on_going_transaction_adapter?.notifyItemRemoved(i)
                                        break
                                    }
                                }

                                inner_fragment = fragment.getInnerFragment("cancelled")
                                inner_fragment.transactions_cancelled_arraylist.add(0,transaction_object)
                                inner_fragment.cancelled_transaction_adapter?.notifyItemInserted(0)
                                inner_fragment.cancelled_transaction_adapter?.notifyDataSetChanged()
                                message = "Order ${transaction_object.tracking_number} has been cancelled!"
                                //ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)

                            }
                            else if(status == "returned upon delivery" || status == "delivered"){
                                inner_fragment = fragment.getInnerFragment("on-going")
                                for(i in 0..inner_fragment.transactions_on_going_arraylist.size-1){
                                    val transaction =  inner_fragment.transactions_on_going_arraylist[i]
                                    if(transaction.id == transaction_id){
                                        inner_fragment.transactions_on_going_arraylist.removeAt(i)
                                        inner_fragment.on_going_transaction_adapter?.notifyItemRemoved(i)
                                        break
                                    }
                                }
                                when(status){
                                    "returned upon delivery" -> {
                                        inner_fragment = fragment.getInnerFragment("returned")
                                        inner_fragment.transactions_returned_arraylist.add(0,transaction_object)
                                        inner_fragment.returned_transaction_adapter?.notifyItemInserted(0)
                                        inner_fragment.returned_transaction_adapter?.notifyDataSetChanged()
                                        message = "Order ${transaction_object.tracking_number} has been returned upon delivery!"
                                        //ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)
                                    }

                                    "delivered" -> {
                                        inner_fragment = fragment.getInnerFragment("delivered")
                                        inner_fragment.transactions_delivered_arraylist.add(0,transaction_object)
                                        inner_fragment.delivered_transaction_adapter?.notifyItemInserted(0)
                                        inner_fragment.delivered_transaction_adapter?.notifyDataSetChanged()
                                        message = "Order ${transaction_object.tracking_number} has been successfully delivered!"
                                        //ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)

                                    }
                                }
                            }
                        }
                            ToastUtil.bingwitDisplayCustomToastNotification(mActivity, message)
                    }
                },
                object : ApiRequest.ErrorCallback {
                    override fun didURLError(error: VolleyError) {
                    }
                })
               }
}




