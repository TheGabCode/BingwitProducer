package gab.cdi.bingwitproducer.dependency_modules

import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseInstanceId : FirebaseInstanceIdService(){
    override fun onTokenRefresh() {
        super.onTokenRefresh()
    }
}