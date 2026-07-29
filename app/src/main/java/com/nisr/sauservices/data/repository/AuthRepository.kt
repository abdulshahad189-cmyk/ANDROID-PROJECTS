package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo as SupabaseUser

class AuthRepository {
    private val auth = SupabaseClient.client.auth

    suspend fun signUp(email: String, password: String): Result<SupabaseUser?> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(auth.currentUserOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        auth.signOut()
    }

    fun getCurrentUser(): SupabaseUser? {
        return auth.currentUserOrNull()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
