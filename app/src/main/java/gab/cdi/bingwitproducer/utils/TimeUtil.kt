package gab.cdi.bingwitproducer.utils

import android.os.AsyncTask
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.android.gms.common.util.Strings
import com.instacart.library.truetime.TrueTime
import java.net.SocketTimeoutException

import java.sql.Time
import kotlin.coroutines.experimental.coroutineContext


object TimeUtil{
    private const val ISO_DATE_FORMAT   = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val USER_FRIENDLY_FORMAT = "MMM dd yyyy hh:mm a"
    fun convertDateStringToLong(sDate: String): Long {
        val sdf = SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault())
        //sdf.timeZone = TimeZone.getTimeZone("GMT")

        try {
            val mDate = sdf.parse(sDate)
            return mDate.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0L
    }

    fun convertDateStringToLongUTC8(sDate: String): Long {
        val sdf = SimpleDateFormat(ISO_DATE_FORMAT)
        sdf.timeZone = TimeZone.getTimeZone("UTC+8")

        try {
            val mDate = sdf.parse(sDate)
            return mDate.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0L
    }

    fun getCurrentTimeMillis() : Long{
        if(TrueTime.isInitialized()){
            Log.d("woops","perfect")
            return TrueTime.now().time
        }
        else{
            Log.d("woops","error retrieving true time")
            return System.currentTimeMillis()
        }


    }

    fun convertDateStringToLongGMT(sDate: String): Long {
        val sdf = SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT")

        try {
            val mDate = sdf.parse(sDate)
            return mDate.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0L
    }

    fun hmsTimeFormatter(milliSeconds: Long): String {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)))
    }

     fun changeDateFormat(dateString: String): String {
        var result = ""

        val formatterOld = SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault())
        val formatterNew = SimpleDateFormat(USER_FRIENDLY_FORMAT, Locale.getDefault())
        var date: Date? = null
        try {

            date = formatterOld.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (date != null) {
            result = formatterNew.format(date)
        }
       return result
    }

    fun changeDateFormat(dateString : String, oldDateStringFormat : String) : String{
        var result = ""
        val formatterOld = SimpleDateFormat(oldDateStringFormat)
        val formatterNew = SimpleDateFormat(USER_FRIENDLY_FORMAT, Locale.getDefault())
        var date: Date? = null
        try {
            date = formatterOld.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (date != null) {
            result = formatterNew.format(date)
        }
        return result
    }

    fun changeDateFormatToUserFriendlyDate(ourDate : String?) : String {
        var dateResult = ""
        if(ourDate == null) return ""
        try {
            val formatter = SimpleDateFormat(ISO_DATE_FORMAT)
            formatter.timeZone = TimeZone.getTimeZone("UTC+8")
            val value = formatter.parse(ourDate)

            val endFormatter = SimpleDateFormat(USER_FRIENDLY_FORMAT)
            endFormatter.timeZone = TimeZone.getDefault()
            dateResult = endFormatter.format(value)

            //Log.d("ourDate", ourDate);
        } catch (e: Exception) {
            dateResult = "00-00-0000 00:00"
        }

        return dateResult
    }

    fun changeDateFormatToUserFriendlyDateUTC8ToUTC(ourDate : String) : String {
        var dateResult = ""
        try {
            val formatter = SimpleDateFormat(ISO_DATE_FORMAT)
            val value = formatter.parse(ourDate)

            val endFormatter = SimpleDateFormat(USER_FRIENDLY_FORMAT)
            endFormatter.timeZone = TimeZone.getTimeZone("UTC")
            dateResult = endFormatter.format(value)

            //Log.d("ourDate", ourDate);
        } catch (e: Exception) {
            dateResult = "00-00-0000 00:00"
        }

        return dateResult
    }

    fun changeDateFormatToUserFriendlyDateUTC8(ourDate : String) : String {
        var dateResult = ""
        try {
            val formatter = SimpleDateFormat(ISO_DATE_FORMAT)
            val value = formatter.parse(ourDate)

            val dateFormatter = SimpleDateFormat(USER_FRIENDLY_FORMAT) //this format changeable

            dateFormatter.timeZone = TimeZone.getTimeZone("GMT") //from UTC to UTC+8
            dateFormatter.timeZone = TimeZone.getDefault()
            dateResult = dateFormatter.format(value)

            //Log.d("ourDate", ourDate);
        } catch (e: Exception) {
            dateResult = "00-00-0000 00:00"
        }

        return dateResult
    }
}