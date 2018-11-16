package gab.cdi.bingwitproducer.https

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.*

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import gab.cdi.bingwitproducer.fragments.CustomAlertDialogFragment
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import android.widget.Toast
import org.json.JSONObject
import com.android.volley.Request.Method.POST
import gab.cdi.bingwit.session.Session
import gab.cdi.bingwitproducer.VolleyMultipartRequest
import gab.cdi.bingwitproducer.https.API.BASE_URL


/**
 * Created by Default on 08/08/2018.
 */
object ApiRequest{
    private val retryPolicy: DefaultRetryPolicy
        get() = DefaultRetryPolicy(
                48 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

    /**
     * Callback response from api request use of all API request in this class.
     */
    interface URLCallback {
        fun didURLResponse(response: String)
    }

    interface ErrorCallback {
        fun didURLError(error : VolleyError)
    }

    interface APICallback {
        fun onAPIResponse(statusCode: Int, apiResponse: String?, e: Exception?)
    }


    /**
     * POST with parameter
     * @param context base context
     * @param url string url
     * @param params a hasmap o body parameters
     * @param url_callback calling back the response data from volley request
     */
    fun post(context: Context?, url: String, params: HashMap<String, String>, message : String?, url_callback: URLCallback?, volley_error : ErrorCallback) {
        var progressDialog : ProgressDialog? = null
        if(message != null){
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage(message)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            url_callback?.didURLResponse(response)
            if(message != null){
                progressDialog?.dismiss()
            }


        }, Response.ErrorListener { error ->
            volley_error.didURLError(error)
            showVolleyError(context, error)
            if(message != null){
                progressDialog?.dismiss()
            }

        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): HashMap<String, String> {
                return params
            }
        }
        request.retryPolicy = retryPolicy
        queue.add(request)
    }

    /**
     * POST with header and parameter
     * @param context base context
     * @param url string url
     * @param header a hashmap key value pair including user token
     * @param params a hasmap o body parameters
     * @param url_callback calling back the response data from volley request
     */
    fun post(context: Context?, url: String, header: HashMap<String, String>,  params: HashMap<String, String>, message : String?, url_callback: URLCallback?,volley_error : ErrorCallback) {

        var progressDialog : ProgressDialog? = null
        if(message != null) {
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage(message)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            url_callback?.didURLResponse(response)
            progressDialog?.dismiss()

        }, Response.ErrorListener { error ->
            volley_error.didURLError(error)
            showVolleyError(context, error)
            progressDialog?.dismiss()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                return header
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): HashMap<String, String> {
                return params
            }
        }
        request.retryPolicy = retryPolicy
        queue.add(request)
    }

    /**
     * @param context base context
     * @param error  A volley error received from API request error.
     */
    fun showVolleyError(context: Context?, error: VolleyError?) {
        if (error != null) {
            val body: String
            //get response body and parse with appropriate encoding
            if (error.networkResponse != null) {
                try {
                    body = String(error.networkResponse.data, Charset.forName("UTF-8"))

                    Log.e("showVolleyError", body)
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun get(context: Context?, url: String, header: HashMap<String, String>,  params: HashMap<String, String>, url_callback: URLCallback?, volley_error: ErrorCallback){
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.GET,url,
                Response.Listener { response ->
                    url_callback?.didURLResponse(response.toString())
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    volley_error.didURLError(error)
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        return header
                    }

                    @Throws(AuthFailureError::class)
                    override fun getParams(): HashMap<String, String> {
                        return params
                    }
        }
        request.retryPolicy = retryPolicy
        queue.add(request)
    }

    fun delete(context: Context?, url : String, message: String?, header : HashMap<String,String>, url_callback: URLCallback?, volley_error: ErrorCallback){
        var progressDialog : ProgressDialog? = null
        if(message != null){
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage(message)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.DELETE, url, Response.Listener { response ->
            url_callback?.didURLResponse(response)
            progressDialog?.dismiss()
        },
                Response.ErrorListener { error ->
                    volley_error.didURLError(error)
                    showVolleyError(context,error)
                    progressDialog?.dismiss()
                }){
            override fun getHeaders(): MutableMap<String, String> {
                return header
            }
        }

        request.retryPolicy = retryPolicy
        queue.add(request)


    }


    fun put(context: Context?, url: String, message : String?, header: HashMap<String, String>,  params: HashMap<String, String>, url_callback: URLCallback?,volley_error : ErrorCallback) {
        var progressDialog : ProgressDialog? = null
        if(message != null) {
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage(message)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.PUT, url, Response.Listener { response ->
            url_callback?.didURLResponse(response)
            progressDialog?.dismiss()

        }, Response.ErrorListener { error ->
            volley_error.didURLError(error)
            showVolleyError(context, error)
            progressDialog?.dismiss()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                return header
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): HashMap<String, String> {
                return params
            }
        }
        request.retryPolicy = retryPolicy
        queue.add(request)
    }

    fun multipartPost(context: Context,
                      url: String,
                      message : String?,
                      mimeType: String,
                      fileName: String,
                      byte: ByteArray,
                      url_callback: URLCallback?,
                      volley_error : ErrorCallback) {

        var progress_dialog  : ProgressDialog? = null
        if(message != null){
            progress_dialog = ProgressDialog(context)
            progress_dialog.setMessage(message)
            progress_dialog.setCancelable(false)
            progress_dialog.show()
        }

        val queue = Volley.newRequestQueue(context)
        val request = object : VolleyMultipartRequest(Request.Method.POST, url, Response.Listener { response ->
            if(progress_dialog != null){
                progress_dialog?.dismiss()
            }
            val resultResponse = String(response?.data!!)
            url_callback?.didURLResponse(resultResponse)


        }, Response.ErrorListener { error ->
            if (error?.networkResponse?.data != null) {
                if(progress_dialog != null){
                    progress_dialog?.dismiss()
                }
                volley_error.didURLError(error)
                showVolleyError(context, error)
            }
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: HashMap<String, String>  = HashMap()
                header["Authorization"] = "Bearer ${Session(context).token()}"
                return header
            }

            override fun getByteData(): MutableMap<String, DataPart> {
                val params: HashMap<String, DataPart> = HashMap()
                params["file"] = DataPart(fileName, byte, mimeType)
                return params
            }
        }
        request.retryPolicy = retryPolicy
        queue.add(request)
    }

}