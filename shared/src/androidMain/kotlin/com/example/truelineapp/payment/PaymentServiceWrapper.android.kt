package com.example.truelineapp.payment

import android.app.Activity
import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.core.api.CFSession
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.cashfree.pg.ui.api.CFDropCheckoutPayment

actual class PaymentServiceWrapper actual constructor() {

    actual fun startCheckout(
        activity: Any,
        orderId: String,
        paymentSessionId: String,
        onSuccess: (orderId: String) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val act = activity as? Activity ?: return
        try {
            val cfSession = CFSession.CFSessionBuilder()
                .setEnvironment(CFSession.Environment.SANDBOX)
                .setPaymentSessionID(paymentSessionId)
                .setOrderId(orderId)
                .build()

            val cfPaymentGatewayService = CFPaymentGatewayService.getInstance()
            cfPaymentGatewayService.setCheckoutCallback(object : CFCheckoutResponseCallback {
                override fun onPaymentVerify(orderID: String) {
                    onSuccess(orderID)
                }

                override fun onPaymentFailure(cfErrorResponse: CFErrorResponse, orderID: String) {
                    onFailure(cfErrorResponse.message ?: "Payment Failed")
                }
            })

            val cfDropCheckoutPayment = CFDropCheckoutPayment.CFDropCheckoutPaymentBuilder()
                .setSession(cfSession)
                .build()

            cfPaymentGatewayService.doPayment(act, cfDropCheckoutPayment)
        } catch (e: Exception) {
            onFailure(e.message ?: "Failed to initiate payment")
        }
    }
}
