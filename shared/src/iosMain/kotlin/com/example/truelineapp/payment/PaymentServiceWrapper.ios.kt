package com.example.truelineapp.payment

actual class PaymentServiceWrapper actual constructor() {
    actual fun startCheckout(
        activity: Any,
        orderId: String,
        paymentSessionId: String,
        onSuccess: (orderId: String) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        onFailure("Cashfree checkout is not configured for iOS yet.")
    }
}
