package com.km.exchange.notification

import java.util.*
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

interface NotificationService {
    fun sendNotification(notification: Notification)
}

internal class ExchangeNotificationService : NotificationService {
    private val emailService: EmailNotificationService = EmailNotificationService()
    private val pushService: PushNotificationService = PushNotificationService()

    override fun sendNotification(notification: Notification) {
        when (notification) {
            is EmailNotification -> emailService.sendEmail(notification)
            is PushNotification -> pushService.sendPush(notification)
            else -> throw IllegalArgumentException("Unknown notification $notification")
        }
    }
}

private class EmailNotificationService {
    fun sendEmail(emailNotification: EmailNotification) {
        val props = Properties()
        val host = "smtp.gmail.com"
        with(props) {
            put("mail.smtp.host", host)
            put("mail.smtp.port", "587") // for TLS
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }
        val password = "xytwo0-tyshis-meKnuh"
        val smtpEmail = "integrationsmtp@gmail.com"
        val auth = object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(smtpEmail, password)
        }
        val session = Session.getInstance(props, auth)
        val message = MimeMessage(session)
        with(message) {
            setFrom(InternetAddress(smtpEmail))
            addRecipient(Message.RecipientType.TO, InternetAddress(emailNotification.toEmail))
            subject = emailNotification.title
            setText(emailNotification.body)
        }
        val transport = session.getTransport("smtp")
        with(transport) {
            connect(host, smtpEmail, password)
            sendMessage(message, message.allRecipients)
            close()
        }
    }
}

private class PushNotificationService {
    fun sendPush(notification: PushNotification) {
        //TODO implement server push
    }

}

interface Notification

data class EmailNotification(
    val toEmail: String, val title: String,
    val body: String
) : Notification

data class PushNotification(
    val userId: String?,
    val title: String,
    val body: String
) : Notification