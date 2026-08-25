package com.jerries.expense.feature.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.backup.BackupManager
import com.jerries.expense.core.common.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class BackupInfo(
    val accounts: Int,
    val categories: Int,
    val transactions: Int,
    val date: String,
)

data class BackupUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val shouldExport: Boolean = false,
    val shouldImport: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val lastBackupInfo: BackupInfo? = null,
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun onExportClicked() {
        _uiState.update { it.copy(shouldExport = true) }
    }

    fun onImportClicked() {
        _uiState.update { it.copy(shouldImport = true) }
    }

    fun onExportTriggered() {
        _uiState.update { it.copy(shouldExport = false) }
    }

    fun onImportTriggered() {
        _uiState.update { it.copy(shouldImport = false) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun exportBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, errorMessage = null) }
            try {
                val payload = withContext(ioDispatcher) { backupManager.buildPayload() }
                val json = withContext(ioDispatcher) { backupManager.encode(payload) }

                withContext(ioDispatcher) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(json.toByteArray(Charsets.UTF_8))
                    } ?: throw IllegalStateException("Cannot open output stream")
                }

                val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
                    .withZone(ZoneId.systemDefault())
                val dateStr = formatter.format(Instant.ofEpochMilli(System.currentTimeMillis()))

                _uiState.update {
                    it.copy(
                        isExporting = false,
                        successMessage = "Backup exported successfully",
                        lastBackupInfo = BackupInfo(
                            accounts = payload.metadata.accountCount,
                            categories = payload.metadata.categoryCount,
                            transactions = payload.metadata.transactionCount,
                            date = dateStr,
                        ),
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        errorMessage = e.message ?: "Failed to export backup",
                    )
                }
            }
        }
    }

    fun importBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, errorMessage = null) }
            try {
                val json = withContext(ioDispatcher) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.readBytes().toString(Charsets.UTF_8)
                    } ?: throw IllegalStateException("Cannot open input stream")
                }

                if (json.isBlank()) {
                    throw IllegalStateException("Backup file is empty")
                }

                val decodeResult = withContext(ioDispatcher) { backupManager.decode(json) }

                val payload = decodeResult.getOrElse { e ->
                    throw IllegalStateException(
                        when {
                            e.message?.contains("schema version", ignoreCase = true) == true ->
                                "Backup format is not supported by this version"
                            e.message?.contains("Not a JERRIES EXPENSE", ignoreCase = true) == true ->
                                "This is not a valid backup file"
                            else -> "Backup file is corrupted or unreadable"
                        },
                    )
                }

                withContext(ioDispatcher) { backupManager.restoreFromPayload(payload) }

                val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
                    .withZone(ZoneId.systemDefault())
                val dateStr = formatter.format(Instant.ofEpochMilli(System.currentTimeMillis()))

                _uiState.update {
                    it.copy(
                        isImporting = false,
                        successMessage = "Backup restored successfully",
                        lastBackupInfo = BackupInfo(
                            accounts = payload.metadata.accountCount,
                            categories = payload.metadata.categoryCount,
                            transactions = payload.metadata.transactionCount,
                            date = dateStr,
                        ),
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        errorMessage = e.message ?: "Failed to import backup",
                    )
                }
            }
        }
    }
}
