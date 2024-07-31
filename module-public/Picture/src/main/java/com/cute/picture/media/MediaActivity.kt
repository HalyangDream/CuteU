package com.cute.picture.media

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cute.picture.R
import kotlinx.coroutines.launch

class MediaActivity : AppCompatActivity() {

    companion object {
        fun startThis(context: Context) {
            val intent = Intent(context, MediaActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var ivBack: ImageView
    private lateinit var tvAlbumName: TextView
    private lateinit var tvPreview: TextView
    private lateinit var btnSure: TextView
    private lateinit var rvAlbum: RecyclerView

    private lateinit var mediaAdapter: MediaAdapter

    private val bucketMap: MutableMap<Long, List<LocalMedia>> = linkedMapOf()
    private val allBucketId: Long = -999999
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        initView()
    }


    private fun initView() {
        ivBack = findViewById(R.id.iv_back)
        tvAlbumName = findViewById(R.id.tv_album_name)
        tvPreview = findViewById(R.id.tv_preview)
        btnSure = findViewById(R.id.tv_sure)
        rvAlbum = findViewById(R.id.rv_album)

        ivBack.setOnClickListener {
            finish()
        }

        rvAlbum.apply {
            layoutManager = GridLayoutManager(this@MediaActivity, 3)
            mediaAdapter = MediaAdapter(this@MediaActivity)
            adapter = mediaAdapter
        }
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val list = MediaDatabase.getAllImages(contentResolver)
            bucketMap[allBucketId] = list
            mediaAdapter.addOriginData(list)
            mediaAdapter.loadAllData()
            val map = list.groupBy { it.bucketId }
            bucketMap.putAll(map)
        }
    }

}