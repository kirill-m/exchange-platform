package com.km.exchange.notification

import org.junit.Test

class ExchangeEmailNotificationServiceTest {
    private val exchangeNotificationService = ExchangeNotificationService()

    @Test
    fun testEmailNotification() {
        exchangeNotificationService.sendNotification(EmailNotification("exxx12312312@gmail.com",
            "Some title", "Some text"))
    }
}