package com.amigo.pay.annotation

import androidx.annotation.IntDef
import com.amigo.pay.*

/**
 * author : mac
 * date   : 2022/4/28
 *
 */
@IntDef(PAY_SUCCESS, PAY_FAIL, PAY_CANCEL)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class PayResult()