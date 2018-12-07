package gab.cdi.bingwitproducer.fragments

import android.support.design.widget.NavigationView

interface BingwitFragment :
        TransactionHistoryFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnFragmentInteractionListener,
        AddProductFragment.OnFragmentInteractionListener,
        EditProductFragment.OnFragmentInteractionListener,
        ViewProductsFragment.OnFragmentInteractionListener,
        ViewProductFragment.OnFragmentInteractionListener,
        ViewTransactionsFragment.OnFragmentInteractionListener,
        ViewTransactionFragment.OnFragmentInteractionListener,
        RemoveProductDialogFragment.OnFragmentInteractionListener,
        TimePickerDialogFragment.OnFragmentInteractionListener,
        RatingsFragment.OnFragmentInteractionListener,
        ForgotPasswordDialogFragment.OnFragmentInteractionListener,
        ChangePasswordDialogFragment.OnFragmentInteractionListener,
        CustomAlertDialogFragment.OnFragmentInteractionListener,
        UploadImageOptionsDialogFragment.OnFragmentInteractionListener,
        ViewProductsTabFragment.OnFragmentInteractionListener,
        ConfirmTransactionStatusDialogFragment.OnFragmentInteractionListener,
        TransactionStatusLogsFragment.OnFragmentInteractionListener