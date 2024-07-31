package com.cute.mine.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.get
import com.cute.basic.dialog.BaseCenterDialog
import com.cute.mine.R
import com.cute.mine.databinding.DialogRatingBinding
import com.cute.tool.Toaster
import com.cute.uibase.setThrottleListener

class RatingDialog : BaseCenterDialog() {

    private lateinit var binding: DialogRatingBinding

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun setDialogWidthRate(): Float {
        return -1f
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.ivClose.setThrottleListener {
            dismissDialog()
        }

        binding.ivRating1.setThrottleListener {
            bindRating(1)
        }
        binding.ivRating2.setThrottleListener {
            bindRating(2)
        }
        binding.ivRating3.setThrottleListener {
            bindRating(3)
        }
        binding.ivRating4.setThrottleListener {
            bindRating(4)
        }
        binding.ivRating5.setThrottleListener {
            bindRating(5)
        }
    }

    override fun initData() {

    }

    private fun bindRating(index: Int) {
        val size = binding.llRatingBar.childCount
        for (i in 0 until size) {
            val view = binding.llRatingBar[i] as ImageView
            if (i + 1 <= index) {
                view.setImageResource(R.drawable.ic_rating_light)
            } else {
                view.setImageResource(R.drawable.ic_rating_grey)
            }
        }

        binding.root.postDelayed({
            if (index != 5) {
                context?.let { Toaster.showShort(it, "Thank you for your rating.") }
                dismissDialog()
            } else {
                ratingNow()
            }
        }, 500)

    }

    private fun ratingNow() {
        try {
            val uri = Uri.parse("market://details?id=${context?.packageName}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.android.vending")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        dismissDialog()
    }
}