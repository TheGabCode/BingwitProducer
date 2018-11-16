package gab.cdi.bingwitproducer.extensions

/**
 * Created by Default on 25/10/2018.
 */
fun Double.convertToCurrencyDecimalFormat() : String{
    return "%.2f".format(this)
}