package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StorageRepository {
    private val storage = SupabaseClient.client.storage

    suspend fun uploadImage(bucketName: String, path: String, byteArray: ByteArray): Result<String> = try {
        withContext(Dispatchers.IO) {
            val bucket = storage.from(bucketName)
            bucket.upload(path, byteArray, upsert = true)
            val url = bucket.publicUrl(path)
            Result.success(url)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getImageUrl(bucketName: String, path: String): String {
        return storage.from(bucketName).publicUrl(path)
    }
}
