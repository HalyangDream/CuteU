package com.cute.store

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.cute.analysis.Analysis
import com.cute.basic.BaseActivity
import com.cute.basic.util.StatusUtils
import com.cute.logic.http.response.pay.Payment
import com.cute.logic.http.response.product.Product
import com.cute.store.adapter.PaymentMethodAdapter
import com.cute.store.databinding.ActivitySettlementStoreBinding
import com.cute.tool.Toaster
import com.cute.uibase.ReportBehavior
import com.cute.uibase.databinding.LayoutTitleBarBinding
import com.cute.uibase.setThrottleListener
import com.cute.uibase.userbehavior.UserBehavior

class SettlementStoreActivity : BaseActivity<ActivitySettlementStoreBinding>() {


    private lateinit var titleBarBinding: LayoutTitleBarBinding
    private lateinit var paymentAdapter: PaymentMethodAdapter

    private lateinit var product: Product
    private var popCode: String = ""

    companion object {

        fun startSettlementStoreActivity(
            context: Context,
            product: Product,
            fromPopCode: String,
            payments: ArrayList<Payment>
        ) {
            val intent = Intent(context, SettlementStoreActivity::class.java)
            intent.putExtra("pop_code", fromPopCode)
            intent.putExtra("product", product)
            intent.putParcelableArrayListExtra("payments", payments)
            context.startActivity(intent)
        }
    }


    override fun initViewBinding(layout: LayoutInflater): ActivitySettlementStoreBinding {

        return ActivitySettlementStoreBinding.inflate(layout)
    }

    override fun initView() {
        titleBarBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBarBinding.flTitle, this)
        popCode = intent.getStringExtra("pop_code") ?: ""
        product = intent.getParcelableExtra<Product>("product")!!
        val list = intent.getParcelableArrayListExtra<Payment>("payments")!!
        titleBarBinding.ivNavBack.setOnClickListener {
            finish()
        }
        titleBarBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_pay)
        viewBinding.rvPayment.apply {
            layoutManager = LinearLayoutManager(context)
            paymentAdapter = PaymentMethodAdapter(context)
            adapter = paymentAdapter
            paymentAdapter.submitList(list)
        }
        viewBinding.btnPrice.setThrottleListener {
            startPay()
        }
        viewBinding.tvNameBound.text = if (product.subName.isNullOrEmpty()) {
            "${product.name}${product.bonusDescribe}"
        } else {
            "${product.name}${product.subName}${product.bonusDescribe}"
        }
        viewBinding.tvPrice.text = "Price:${product.unit}${product.displayPrice}"
        viewBinding.btnPrice.text = "Pay:${product.unit}${product.displayPrice}"
        ReportBehavior.reportEvent("payment_method_list", mutableMapOf<String, Any>().apply {
            put("pop_type", "$popCode")
            put("source", UserBehavior.chargeSource)
            put("item_name", product.name)
            put("item_money_amount", product.displayPrice)
        })
    }

    private fun startPay() {
        val payment = paymentAdapter.getSelectItem() ?: return
        PayViewModel.launchPay(this, product, payment) { result, msg ->
            if (result) {
                Toaster.showShort(this, "Pay Success")
                finish()
            } else {
                Toaster.showShort(this, msg)
            }
        }
        ReportBehavior.reportEvent(
            "payment_method_list_continue",
            mutableMapOf<String, Any>().apply {
                put("pop_type", "$popCode")
                put("source", UserBehavior.chargeSource)
                put("item_name", product.name)
                put("item_money_amount", product.displayPrice)
            })
    }
}