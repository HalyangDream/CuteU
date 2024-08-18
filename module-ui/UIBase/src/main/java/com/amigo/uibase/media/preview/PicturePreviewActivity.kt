package com.amigo.uibase.media.preview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.amigo.basic.BaseActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.uibase.R
import com.amigo.uibase.ad.AdPlayService
import com.amigo.uibase.adapter.PicturePreviewAdapter
import com.amigo.uibase.databinding.ActivityPicturePreviewBinding
import com.amigo.uibase.databinding.LayoutTitleBarBinding


class PicturePreviewActivity : BaseActivity<ActivityPicturePreviewBinding>() {

    private lateinit var titleBarBinding: LayoutTitleBarBinding
    private lateinit var data: Array<Any>

    private lateinit var pictureAdapter: PicturePreviewAdapter
    private val pageSnapHelper: PagerSnapHelper = PagerSnapHelper()

    companion object {

        fun startPreview(context: Context, list: Array<String>, index: Int = 0) {
            val intent = Intent(context, PicturePreviewActivity::class.java)
            intent.putExtra("list", list)
            intent.putExtra("index", index)
            context.startActivity(intent)
        }
    }

    override fun initViewBinding(layout: LayoutInflater): ActivityPicturePreviewBinding {

        return ActivityPicturePreviewBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setStatusMode(false, this.window)
        data = intent.getStringArrayExtra("list") as Array<Any>
        val index = intent.getIntExtra("index", 0)
        titleBarBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBarBinding.flTitle, this)
        titleBarBinding.ivNavBack.setImageResource(R.drawable.ic_nav_back_white)
        titleBarBinding.tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
        titleBarBinding.ivNavBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdPlayService.reportPlayAdScenes("view_big_img")
                finish()
            }
        })
        viewBinding.vpImg.apply {
            pictureAdapter = PicturePreviewAdapter(context)
            val manage = LinearLayoutManager(context)
            manage.orientation = LinearLayoutManager.HORIZONTAL
            pageSnapHelper.attachToRecyclerView(this)
            this.layoutManager = manage
            this.adapter = pictureAdapter
            pictureAdapter.setData(data.toMutableList())
            manage.scrollToPosition(index)
        }

        viewBinding.vpImg.addOnScrollListener(object : OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView: View? = pageSnapHelper.findSnapView(recyclerView.layoutManager)
                    if (snapView != null) {
                        val currentPosition = recyclerView.layoutManager!!.getPosition(snapView)
                        titleBarBinding.tvTitle.text =
                            "${currentPosition + 1}/${pictureAdapter.itemCount}"
                    }
                }
            }
        })
        titleBarBinding.tvTitle.text = "${index + 1}/${pictureAdapter.itemCount}"
    }
}