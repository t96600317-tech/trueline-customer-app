package com.example.truelineapp.payment

expect class PaymentServiceWrapper() {
    fun startCheckout(
        activity: Any,
        orderId: String,
        paymentSessionId: String,
        onSuccess: (orderId: String) -> Unit,
        onFailure: (error: String) -> Unit
    )
}
