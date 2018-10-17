package gab.cdi.bingwitproducer.https

import gab.cdi.bingwitproducer.R.string.verify

/**
 * Created by Default on 15/10/2018.
 */
object API {
    val BASE_URL = "http://192.168.0.126:3000/api/v1/"
    val SIGN_UP = "$BASE_URL/users/register"
    val VERIFY = "$BASE_URL/users/verify"
    val RESEND_VERIFICATION = "$BASE_URL/users/resendVerificationCode"
    val SIGN_IN = "$BASE_URL/users/login"
    val CHANGE_PASSWORD = "$BASE_URL/users/changePassword"
    val SEND_RESET_PASSWORD = "$BASE_URL/users/sendPasswordResetCode"
    val ENTER_PASSWORD_RESET = "$BASE_URL/users/enterPasswordResetCode"
    val GET_USER = "$BASE_URL"+"users"
    val RESET_PASSWORD = "$BASE_URL"+"users/resetPassword"
    val CHECK_USER = "$BASE_URL/users/getUserType"
}