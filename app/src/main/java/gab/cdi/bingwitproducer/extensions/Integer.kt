package gab.cdi.bingwitproducer.extensions

fun Int.isEven() : Boolean {
    if(this%2==0) return true
    return false
}