package gab.cdi.bingwitproducer.dummy

import gab.cdi.bingwitproducer.models.Product
import gab.cdi.bingwitproducer.models.Rating
import gab.cdi.bingwitproducer.models.Transaction
import org.json.JSONObject

/**
 * Created by Default on 09/10/2018.
 */
class Dummy{
    companion object {
        val dummy_products : ArrayList<Product> = ArrayList()
        val dummy_products_hashmap : HashMap<String,Product> = HashMap()

        val dummy_transactions_ongoing : ArrayList<Transaction> = ArrayList()
        val dummy_transactions_delivered : ArrayList<Transaction> = ArrayList()
        val dummy_transactions_returned : ArrayList<Transaction> = ArrayList()
        val dummy_transactions_hashmap : HashMap<String,Transaction> = HashMap()


        val dummy_rating : ArrayList<Rating> = ArrayList()
    }

    fun initDummyProducts() {
        val product_tilapia_fixed = JSONObject()
        product_tilapia_fixed.put("product_name","fresh tilapia tail")
        product_tilapia_fixed.put("product_type","Tilapia")
        product_tilapia_fixed.put("product_id","ftt1")
        product_tilapia_fixed.put("product_weight",5.0)
        product_tilapia_fixed.put("product_selling_method",1)
        product_tilapia_fixed.put("product_price",80.00)

        val product_pusit_auction = JSONObject()
        product_pusit_auction.put("product_name","fresh pusit daing")
        product_pusit_auction.put("product_type","Pusit")
        product_pusit_auction.put("product_id","fpd1")
        product_pusit_auction.put("product_weight",10.0)
        product_pusit_auction.put("product_selling_method",0)
        product_pusit_auction.put("product_price",550.00)
        product_pusit_auction.put("auction_price",750.00)


        dummy_products.add(Product(product_tilapia_fixed))
        dummy_products.add(Product(product_pusit_auction))

        dummy_products_hashmap.put(product_tilapia_fixed.optString("product_id"),Product(product_tilapia_fixed))
        dummy_products_hashmap.put(product_pusit_auction.optString("product_id"),Product(product_pusit_auction))
    }

    fun initDummyTransactionsOnGoing(){
        dummy_transactions_ongoing.clear()
        val product_tilapia_fixed = JSONObject()
        product_tilapia_fixed.put("product_name","fresh tilapia tail")
        product_tilapia_fixed.put("product_type","Tilapia")
        product_tilapia_fixed.put("product_id","ftt1")
        product_tilapia_fixed.put("product_weight",5.0)
        product_tilapia_fixed.put("product_selling_method",1)
        product_tilapia_fixed.put("product_price",80.00)

        val product_pusit_fixed = JSONObject()
        product_pusit_fixed.put("product_name","fresh pusit daing")
        product_pusit_fixed.put("product_type","Pusit")
        product_pusit_fixed.put("product_id","fpd1")
        product_pusit_fixed.put("product_weight",1.0)
        product_pusit_fixed.put("product_selling_method",1)
        product_pusit_fixed.put("product_price",100.00)

        val transaction_tilapia_fixed = JSONObject()
        transaction_tilapia_fixed.put("transaction_id","09876543210")
        transaction_tilapia_fixed.put("consumer_id","cons1")
        transaction_tilapia_fixed.put("producer_id","prod1")
        transaction_tilapia_fixed.put("consumer_name","Joema Nequinto")
        transaction_tilapia_fixed.put("producer_name","Gab")
        transaction_tilapia_fixed.put("transaction_date_ordered","9 Oct 2018 11:50")
        transaction_tilapia_fixed.put("product",product_tilapia_fixed)
        transaction_tilapia_fixed.put("transaction_status","On-going")

        val transaction_pusit_fixed = JSONObject()
        transaction_pusit_fixed.put("transaction_id","09166310529")
        transaction_pusit_fixed.put("consumer_id","cons1")
        transaction_pusit_fixed.put("producer_id","prod1")
        transaction_pusit_fixed.put("consumer_name","Majoe Nequinto")
        transaction_pusit_fixed.put("producer_name","Gab Fisher")
        transaction_pusit_fixed.put("transaction_date_ordered","11 Oct 2018 2:41")
        transaction_pusit_fixed.put("product",product_pusit_fixed)
        transaction_pusit_fixed.put("transaction_status","Ready for delivery")

        dummy_transactions_ongoing.add(Transaction(transaction_tilapia_fixed))
        dummy_transactions_ongoing.add(Transaction(transaction_pusit_fixed))


    }



    fun initDummyTransactionsDelivered(){
        dummy_transactions_delivered.clear()
        val product_tilapia_fixed = JSONObject()
        product_tilapia_fixed.put("product_name","fresh dilis")
        product_tilapia_fixed.put("product_type","Anchovy")
        product_tilapia_fixed.put("product_id","ftt1")
        product_tilapia_fixed.put("product_weight",10.0)
        product_tilapia_fixed.put("product_selling_method",1)
        product_tilapia_fixed.put("product_price",10.00)

        val product_pusit_fixed = JSONObject()
        product_pusit_fixed.put("product_name","fresh crab stick")
        product_pusit_fixed.put("product_type","Crab")
        product_pusit_fixed.put("product_id","fpd1")
        product_pusit_fixed.put("product_weight",10.0)
        product_pusit_fixed.put("product_selling_method",1)
        product_pusit_fixed.put("product_price",1000.00)

        val transaction_tilapia_fixed = JSONObject()
        transaction_tilapia_fixed.put("transaction_id","09876543210")
        transaction_tilapia_fixed.put("consumer_id","cons1")
        transaction_tilapia_fixed.put("producer_id","prod1")
        transaction_tilapia_fixed.put("consumer_name","Joema Nequinto")
        transaction_tilapia_fixed.put("producer_name","Gab")
        transaction_tilapia_fixed.put("transaction_date_ordered","9 Oct 2018 11:50")
        transaction_tilapia_fixed.put("product",product_tilapia_fixed)
        transaction_tilapia_fixed.put("transaction_status","Delivered")
        transaction_tilapia_fixed.put("transaction_rating",4.5)
        transaction_tilapia_fixed.put("transaction_comment","crispee af!!!")
        transaction_tilapia_fixed.put("transaction_address","Bagumbayan Ville")
        transaction_tilapia_fixed.put("product_ordered_quantity",5.0)
        transaction_tilapia_fixed.put("transaction_auction_sold_price",0.0)
        transaction_tilapia_fixed.put("transaction_total_amount",300.0)

        val transaction_pusit_fixed = JSONObject()
        transaction_pusit_fixed.put("transaction_id","09166310529")
        transaction_pusit_fixed.put("consumer_id","cons1")
        transaction_pusit_fixed.put("producer_id","prod1")
        transaction_pusit_fixed.put("consumer_name","Majoe Nequinto")
        transaction_pusit_fixed.put("producer_name","Gab Fisher")
        transaction_pusit_fixed.put("transaction_date_ordered","11 Oct 2018 2:41")
        transaction_pusit_fixed.put("product",product_pusit_fixed)
        transaction_pusit_fixed.put("transaction_status","Delivered")
        transaction_pusit_fixed.put("transaction_rating",1.0)
        transaction_pusit_fixed.put("transaction_comment","i hate pusit")
        transaction_pusit_fixed.put("transaction_address","Santisima Ville")
        transaction_pusit_fixed.put("product_ordered_quantity",10.0)
        transaction_pusit_fixed.put("transaction_auction_sold_price",0.0)
        transaction_pusit_fixed.put("transaction_total_amount",800.0)

        dummy_transactions_delivered.add(Transaction(transaction_tilapia_fixed))
        dummy_transactions_delivered.add(Transaction(transaction_pusit_fixed))


    }


    fun initDummyTransactionsReturned(){
        dummy_transactions_returned.clear()
        val product_tilapia_fixed = JSONObject()
        product_tilapia_fixed.put("product_name","fresh hipon")
        product_tilapia_fixed.put("product_type","Shrimp")
        product_tilapia_fixed.put("product_id","ftt1")
        product_tilapia_fixed.put("product_weight",8.0)
        product_tilapia_fixed.put("product_selling_method",1)
        product_tilapia_fixed.put("product_price",50.00)

        val product_pusit_fixed = JSONObject()
        product_pusit_fixed.put("product_name","fresh tahong")
        product_pusit_fixed.put("product_type","Tahong")
        product_pusit_fixed.put("product_id","fpd1")
        product_pusit_fixed.put("product_weight",5.0)
        product_pusit_fixed.put("product_selling_method",1)
        product_pusit_fixed.put("product_price",120.00)

        val transaction_tilapia_fixed = JSONObject()
        transaction_tilapia_fixed.put("transaction_id","09876543210")
        transaction_tilapia_fixed.put("consumer_id","cons1")
        transaction_tilapia_fixed.put("producer_id","prod1")
        transaction_tilapia_fixed.put("consumer_name","Joema Nequinto")
        transaction_tilapia_fixed.put("producer_name","Gab")
        transaction_tilapia_fixed.put("transaction_date_ordered","9 Oct 2018 11:50")
        transaction_tilapia_fixed.put("product",product_tilapia_fixed)
        transaction_tilapia_fixed.put("transaction_status","Returned")

        val transaction_pusit_fixed = JSONObject()
        transaction_pusit_fixed.put("transaction_id","09166310529")
        transaction_pusit_fixed.put("consumer_id","cons1")
        transaction_pusit_fixed.put("producer_id","prod1")
        transaction_pusit_fixed.put("consumer_name","Majoe Nequinto")
        transaction_pusit_fixed.put("producer_name","Gab Fisher")
        transaction_pusit_fixed.put("transaction_date_ordered","11 Oct 2018 2:41")
        transaction_pusit_fixed.put("product",product_pusit_fixed)
        transaction_pusit_fixed.put("transaction_status","Returned")

        dummy_transactions_returned.add(Transaction(transaction_tilapia_fixed))
        dummy_transactions_returned.add(Transaction(transaction_pusit_fixed))


    }


    fun initDummyRatings(){

    }
}