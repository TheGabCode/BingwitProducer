package gab.cdi.bingwitproducer.https

import gab.cdi.bingwitproducer.R.string.verify

/**
 * Created by Default on 15/10/2018.
 */
object API {
//    192.168.0.126:3000
    //18.224.2.191
    val BASE_URL = "http://18.224.2.191/api/v1/"
    val SIGN_UP = "$BASE_URL/users/register"
    val SIGN_OUT = "$BASE_URL/users/logout"
    val VERIFY = "$BASE_URL/users/verify"
    val RESEND_VERIFICATION = "$BASE_URL/users/resendVerificationCode"
    val SIGN_IN = "$BASE_URL/users/login"
    val CHANGE_PASSWORD = "$BASE_URL/users/changePassword"
    val SEND_RESET_PASSWORD = "$BASE_URL/users/sendPasswordResetCode"
    val ENTER_PASSWORD_RESET = "$BASE_URL/users/enterPasswordResetCode"
    val GET_USER = "$BASE_URL"+"users"
    val RESET_PASSWORD = "$BASE_URL"+"users/resetPassword"
    val CHECK_USER = "$BASE_URL/users/getUserType"
    val UPLOAD_IMAGE = "$BASE_URL/storage/upload"
    val UPDATE_USER = "$BASE_URL"+"users"
    val ADD_PRODUCT = "$BASE_URL"
    val GET_PRODUCTS = "$BASE_URL"+"users"
    val GET_PRODUCT_TYPES = "$BASE_URL"+"product_types"
    val GET_PRODUCT_BY_ID = "$BASE_URL"+"products"
    val DELETE_PRODUCT_BY_ID = "$BASE_URL"
    val UPDATE_PRODUCT = "$BASE_URL"
    val GET_TRANSACTIONS = BASE_URL+"users"
    val GET_PRODUCT_AUCTION_BY_ID = "${BASE_URL}products/auctions"
    val UPDATE_FIXED_TO_AUCTION = "${BASE_URL}products"
    val UPDATE_PRODUCT_AUCTION = "${BASE_URL}products/auctions"
    val UPDATE_AUCTION_TO_FIXED = "${BASE_URL}products"
    val DELETE_AUCTION = "${BASE_URL}products/auctions"
    val GET_AREAS = "${BASE_URL}area"
    val GET_TRANSACTION_BY_ID = "${BASE_URL}users"
    val UPDATE_TRANSACTION_STAGE = "${BASE_URL}transactions"
    val GET_TRANSACTION_PRODUCTS = "${BASE_URL}users"
    val GET_PRODUCT_CATEGORY = "${BASE_URL}product_category_all"
}