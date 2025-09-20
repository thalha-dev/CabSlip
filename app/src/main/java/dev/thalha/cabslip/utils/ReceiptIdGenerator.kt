package dev.thalha.cabslip.utils

import kotlin.random.Random

object ReceiptIdGenerator {
    private const val CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    /**
     * Generates a unique receipt ID in format: {timestamp_ms}-{6_char_uppercase_alphanumeric}
     * Example: 1750680320562-AW0D4V
     */
    fun generateReceiptId(): String {
        val timestamp = System.currentTimeMillis()
        val randomPart = generateRandomAlphaNumeric(6)
        return "$timestamp-$randomPart"
    }

    private fun generateRandomAlphaNumeric(length: Int): String {
        return (1..length)
            .map { CHARS.random() }
            .joinToString("")
    }
}
