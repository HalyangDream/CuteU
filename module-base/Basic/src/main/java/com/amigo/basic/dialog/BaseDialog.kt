package com.amigo.basic.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * author : mac
 * date   : 2021/9/1
 *
 * 不允许在构造方法内，传递任何参数
 * 传递参数通过Bundle传递，在使用 @see{showDialog()}
 */
abstract class BaseDialog : AppCompatDialogFragment() {

    /**
     * 页面停留时长
     * 根据onResume 和 onPause进行计算
     */
    var stayDuration: Long = 0
    private var visitStartTime: Long = 0 //访问开始时间

    /**
     * 页面存在时长
     * 根据OnCreate 和 OnDestroy进行计算
     */
    var pageExistTime: Long = 0
    private var pageCreateTime: Long = 0 //页面创建时间


    private var listener: DialogInterface.OnDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog?.window?.apply {
            // 设置状态栏和导航栏颜色
            setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        parseBundle(arguments)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val attributes = window?.attributes

        val hRate = setDialogHeightRate()
        val wRate = setDialogWidthRate()
        val height = when (hRate) {
            -1f -> WindowManager.LayoutParams.MATCH_PARENT
            -2f -> WindowManager.LayoutParams.WRAP_CONTENT
            else -> context?.let { dip2px(it, hRate) } ?: 1280
        }


        val width = when (wRate) {
            -1f -> WindowManager.LayoutParams.MATCH_PARENT
            -2f -> WindowManager.LayoutParams.WRAP_CONTENT
            else -> {
                val screen = context?.let { getScreenWidth(it) } ?: 720
                (screen * wRate).toInt()
            }
        }
        attributes?.width = width
        attributes?.height = height
        window?.attributes = attributes
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(DialogFragment.STYLE_NO_TITLE)
        val view = getRootView(inflater, container)
        initView(view)
        initData()
        return view
    }

    override fun onResume() {
        super.onResume()
        stayDuration = 0
        visitStartTime = System.currentTimeMillis() / 1000
        pageCreateTime = System.currentTimeMillis() / 1000
    }

    override fun onPause() {
        super.onPause()
        val visitEndTime = System.currentTimeMillis() / 1000
        val visitTime = visitEndTime - visitStartTime
        stayDuration += visitTime
        pageExistTime += visitEndTime - pageCreateTime
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss(dialog)
    }


    abstract fun parseBundle(bundle: Bundle?)

    /**
     * 返回一个view
     */
    abstract fun getRootView(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): View?

    /**
     *  -1 = MATCH_PARENT
     *  -2 = WRAP_CONTENT
     *  宽度是比例，在0-1之间
     */
    abstract fun setDialogWidthRate(): Float

    /**
     *  -1 = MATCH_PARENT
     *  -2 = WRAP_CONTENT
     *  高度是dp值
     */
    abstract fun setDialogHeightRate(): Float

    /**
     * 初始化View控件
     */
    abstract fun initView(view: View?)

    /**
     * 初始化数据
     */
    abstract fun initData()

    fun isShowing(): Boolean {
        if (dialog == null) return false
        return dialog!!.isShowing
    }

    fun setDialogCancelable(cancelable: Boolean) {
        dialog?.setCancelable(cancelable)
    }

    fun setDialogCanceledOnTouchOutside(cancelable: Boolean) {
        dialog?.setCanceledOnTouchOutside(cancelable)
    }

    fun setDialogDismissListener(listener: DialogInterface.OnDismissListener) {
        this.listener = listener
    }

    open fun showDialog(context: Context?, bundle: Bundle?) {
        val manager = if (context is FragmentActivity?) context?.supportFragmentManager
        else if (context is Fragment?) context?.childFragmentManager
        else null
        try {
            if (manager != null) {
                val tag = javaClass.simpleName
                val ft = manager.beginTransaction()
                if (bundle != null) {
                    arguments = bundle
                }
                if (isAdded) {
                    ft.remove(this)
                }
                ft.add(this, tag)
                ft.commitAllowingStateLoss()
                manager.executePendingTransactions()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    open fun dismissDialog() {
        try {
            dismissAllowingStateLoss()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }


    fun getScreenWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels
    }
}

