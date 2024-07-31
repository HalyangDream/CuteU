package com.cute.pay.annotation

import androidx.annotation.IntDef
import com.cute.pay.*

/**
 * author : mac
 * date   : 2022/4/20
 * 
 */
//@IntDef(PAY_MODE_GOOGLE, PAY_MODE_PAYTM, PAY_MODE_UPI, PAY_MODE_WEB)
@IntDef(PAY_MODE_GOOGLE, PAY_MODE_WEB)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class PayMode()
