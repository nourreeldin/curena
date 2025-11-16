package com.fueians.medicationapp.model.security
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
class CryptoManager(
    private val encryptionAlgorithm: String = "AES",
    private val keySize: Int = 196
) {

    fun encrypt(data: String): String {
        return try {
            val cipher = Cipher.getInstance(encryptionAlgorithm)
            val secretKey = getOrGenerateKey()
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            throw SecurityException("Encryption failed: ${e.message}")
        }
    }

    fun decrypt(encryptedData: String): String {
        return try {
            val cipher = Cipher.getInstance(encryptionAlgorithm)
            val secretKey = getOrGenerateKey()
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw SecurityException("Decryption failed: ${e.message}")
        }
    }

    fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(encryptionAlgorithm)
        keyGenerator.init(keySize)
        return keyGenerator.generateKey()
    }

    private fun getOrGenerateKey(): SecretKey {
        // In a real app, you would retrieve this from secure storage (KeyStore)
        // For now, we'll generate a temporary key
        return generateKey()
    }
}