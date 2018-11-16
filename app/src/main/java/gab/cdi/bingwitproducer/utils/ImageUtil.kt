package gab.cdi.bingwitproducer.utils

import gab.cdi.bingwitproducer.R

object ImageUtil {
    object product_category_names {
        val FISH = "FISH"
        val MOLLUSCS = "MOLLUSCS"
        val OTHER_AQUATIC_ANIMALS = "OTHER_AQUATIC_ANIMALS"
        val CRUSTACEANS = "CRUSTACEANS"
        val MICROPHYTES = "MICROPHYTES"
    }


    fun placeholder(product_category_name : String) : Int{
        when(product_category_name.toUpperCase()){

            product_category_names.FISH -> return R.drawable.ic_fish

            product_category_names.MICROPHYTES -> return R.drawable.ic_seaweeds

            product_category_names.MOLLUSCS -> return R.drawable.ic_shell

            product_category_names.OTHER_AQUATIC_ANIMALS -> return R.drawable.ic_others

            product_category_names.CRUSTACEANS -> return R.drawable.ic_shellfish

            else -> return R.drawable.ic_others
        }
    }
}