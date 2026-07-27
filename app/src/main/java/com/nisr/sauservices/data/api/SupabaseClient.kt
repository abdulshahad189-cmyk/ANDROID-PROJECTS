package com.nisr.sauservices.data.api

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    // Replace these with your actual Supabase URL and Anon Key
    private const val SUPABASE_URL = "https://YOUR_PROJECT_REF.supabase.co"
    private const val SUPABASE_ANON_KEY = "YOUR_PUBLISHABLE_KEY"
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}
