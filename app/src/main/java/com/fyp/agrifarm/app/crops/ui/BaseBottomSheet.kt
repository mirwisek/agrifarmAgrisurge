package com.fyp.agrifarm.app.crops.ui

import android.app.Dialog
import android.os.Bundle
import com.fyp.agrifarm.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


open class BaseBottomSheet : BottomSheetDialogFragment() {

    lateinit var bottomSheet: BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheet = BottomSheetDialog(requireContext(), theme)

//        //setting Peek at the 16:9 ratio keyline of its parent
        bottomSheet.behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO, true);
//        bottomSheet.behavior.peekHeight = 300
        bottomSheet.setCancelable(false)
        bottomSheet.dismissWithAnimation = true

        return bottomSheet
    }

    override fun getTheme(): Int {
        // Return Custom Style that we made
        return R.style.BottomSheetDialogTheme
    }

}
