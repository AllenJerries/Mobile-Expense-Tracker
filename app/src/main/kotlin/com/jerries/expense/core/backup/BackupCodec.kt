package com.jerries.expense.core.backup

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Encodes/decodes [BackupPayload] documents. Kept dependency-free and
 * side-effect-free so the Phase 2 backup repository and worker can reuse it.
 */
class BackupCodec(private val json: Json = defaultJson()) {

    fun encode(payload: BackupPayload): String = json.encodeToString(payload)

    fun decode(raw: String): Result<BackupPayload> = runCatching {
        val payload = json.decodeFromString<BackupPayload>(raw)
        require(payload.metadata.schemaVersion <= BackupSchema.SCHEMA_VERSION) {
            "Unsupported backup schema version: ${payload.metadata.schemaVersion}"
        }
        require(payload.metadata.appId == BackupSchema.APP_ID) { "Not a JERRIES EXPENSE backup" }
        payload
    }

    companion object {
        fun defaultJson(): Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = false
        }

        /** e.g. jerries-expense-20260824-181500.jebak */
        fun fileNameFor(epochMillis: Long): String {
            val timestamp = DateTimeFormatter
                .ofPattern("yyyyMMdd-HHmmss")
                .withZone(ZoneOffset.UTC)
                .format(Instant.ofEpochMilli(epochMillis))
            return "jerries-expense-$timestamp.${BackupSchema.FILE_EXTENSION}"
        }
    }
}
