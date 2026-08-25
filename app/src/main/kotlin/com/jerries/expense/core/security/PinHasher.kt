package com.jerries.expense.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PinHasher {

    private const val SALT_LENGTH = 16
    private val FIXED_SALT: ByteArray = "JeExpensePinSalt2024".toByteArray()

    fun hash(pin: String): String {
        val salt = generateSalt()
        val hash = computeHash(pin, salt)
        return "${Base64.getEncoder().encodeToString(salt)}:${Base64.getEncoder().encodeToString(hash)}"
    }

    fun verify(pin: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false
        val salt = Base64.getDecoder().decode(parts[0])
        val expectedHash = Base64.getDecoder().decode(parts[1])
        val actualHash = computeHash(pin, salt)
        return expectedHash.contentEquals(actualHash)
    }

    private fun computeHash(pin: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        return digest.digest(pin.toByteArray(Charsets.UTF_8))
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }
}
