package com.fueians.medicationapp.model.services

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionService(private val context: Context) {

    companion object {
        private const val KEY_SIZE = 256
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
        private const val TAG_LENGTH = 128
    }

    private lateinit var secretKey: SecretKey
    private val secureRandom = SecureRandom()

    fun initialize() {
        // Generate AES key for symmetric encryption
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(KEY_SIZE)
        secretKey = keyGen.generateKey()
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        val iv = ByteArray(IV_SIZE)
        secureRandom.nextBytes(iv)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

        // Prepend IV to encrypted data
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decrypt(encryptedData: String): String {
        val combined = Base64.decode(encryptedData, Base64.NO_WRAP)
        val iv = combined.copyOfRange(0, IV_SIZE)
        val cipherText = combined.copyOfRange(IV_SIZE, combined.size)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decrypted = cipher.doFinal(cipherText)
        return String(decrypted, Charsets.UTF_8)
    }

    fun hashPassword(password: String): String {
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        val md = MessageDigest.getInstance("SHA-512")
        md.update(salt)
        val hash = md.digest(password.toByteArray(Charsets.UTF_8))
        val hashWithSalt = salt + hash
        return Base64.encodeToString(hashWithSalt, Base64.NO_WRAP)
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        val decoded = Base64.decode(hash, Base64.NO_WRAP)
        val salt = decoded.copyOfRange(0, 16)
        val storedHash = decoded.copyOfRange(16, decoded.size)
        val md = MessageDigest.getInstance("SHA-512")
        md.update(salt)
        val computedHash = md.digest(password.toByteArray(Charsets.UTF_8))
        return storedHash.contentEquals(computedHash)
    }

    fun generateSecureToken(): String {
        val tokenBytes = ByteArray(32)
        secureRandom.nextBytes(tokenBytes)
        return Base64.encodeToString(tokenBytes, Base64.NO_WRAP)
    }
}
