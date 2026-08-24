package com.jerries.expense.core.security

/** Container for ciphertext plus the parameters needed to decrypt it. */
data class EncryptedPayload(
    val initializationVector: ByteArray,
    val ciphertext: ByteArray,
) {
    override fun equals(other: Any?): Boolean = other is EncryptedPayload &&
        initializationVector.contentEquals(other.initializationVector) &&
        ciphertext.contentEquals(other.ciphertext)

    override fun hashCode(): Int = 31 * initializationVector.contentHashCode() +
        ciphertext.contentHashCode()
}

/**
 * Symmetric crypto abstraction used to protect sensitive values
 * (e.g. a future app-lock PIN) at rest.
 */
interface CryptoManager {
    fun encrypt(plainText: ByteArray): EncryptedPayload

    fun decrypt(payload: EncryptedPayload): ByteArray
}
