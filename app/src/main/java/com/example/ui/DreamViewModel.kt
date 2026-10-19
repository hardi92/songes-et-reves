package com.example.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.SpiritualInterpreter
import com.example.audio.AiVoicePlayer
import com.example.audio.VoiceRecognitionManager
import com.example.auth.AuthManager
import com.example.data.db.AppDatabase
import com.example.data.firebase.FirestoreSyncRepository
import com.example.data.model.DreamRecord
import com.example.data.repository.DreamRepository
import com.example.notification.SpiritualNotificationHelper
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterTab {
    ALL,
    DREAMS,
    PROPHECIES,
    FAVORITES
}

class DreamViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = DreamRepository(database.dreamDao())
    val voiceManager = VoiceRecognitionManager(application)
    val aiVoicePlayer = AiVoicePlayer(application)
    val authManager = AuthManager(application)
    private val firestoreSync = FirestoreSyncRepository(application)

    // Auth State
    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser
    val isAuthLoading: StateFlow<Boolean> = authManager.isLoading
    val authError: StateFlow<String?> = authManager.authError

    // AI Voice states
    val isSpeaking: StateFlow<Boolean> = aiVoicePlayer.isPlaying
    val isVoiceGenerating: StateFlow<Boolean> = aiVoicePlayer.isGenerating
    val voiceStatusText: StateFlow<String?> = aiVoicePlayer.statusText

    // Filter & Search
    private val _currentFilter = MutableStateFlow(FilterTab.ALL)
    val currentFilter: StateFlow<FilterTab> = _currentFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // UI Dialogs & Navigation
    private val _isRecordSheetOpen = MutableStateFlow(false)
    val isRecordSheetOpen: StateFlow<Boolean> = _isRecordSheetOpen.asStateFlow()

    private val _selectedRecord = MutableStateFlow<DreamRecord?>(null)
    val selectedRecord: StateFlow<DreamRecord?> = _selectedRecord.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isAuthDialogOpen = MutableStateFlow(false)
    val isAuthDialogOpen: StateFlow<Boolean> = _isAuthDialogOpen.asStateFlow()

    // AI Interpretation Loading State
    private val _isInterpreting = MutableStateFlow(false)
    val isInterpreting: StateFlow<Boolean> = _isInterpreting.asStateFlow()

    private val _interpretationProgressText = MutableStateFlow("Écoute de l'Esprit et discernement des symboles…")
    val interpretationProgressText: StateFlow<String> = _interpretationProgressText.asStateFlow()

    // Notification Reminder state
    private val _isDailyReminderEnabled = MutableStateFlow(true)
    val isDailyReminderEnabled: StateFlow<Boolean> = _isDailyReminderEnabled.asStateFlow()

    // Filtered records flow
    val records: StateFlow<List<DreamRecord>> = combine(
        repository.allRecords,
        _currentFilter,
        _searchQuery
    ) { all, filter, query ->
        val filtered = when (filter) {
            FilterTab.ALL -> all
            FilterTab.DREAMS -> all.filter { it.category == "REVE" }
            FilterTab.PROPHECIES -> all.filter { it.category == "PROPHETIE" }
            FilterTab.FAVORITES -> all.filter { it.isFavorite }
        }
        if (query.isBlank()) {
            filtered
        } else {
            filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.rawAudioText.contains(query, ignoreCase = true) ||
                it.keySymbols.contains(query, ignoreCase = true) ||
                it.biblicalInterpretation.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed initial data if DB empty
        viewModelScope.launch {
            val existing = repository.allRecords.first()
            if (existing.isEmpty()) {
                repository.checkAndSeedInitialData()
            }
        }

        // Listen for user changes and sync with cloud
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    val localList = repository.allRecords.first()
                    for (rec in localList) {
                        try {
                            firestoreSync.uploadRecord(user.uid, rec)
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    fun setFilter(filter: FilterTab) {
        _currentFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openRecordSheet() {
        _isRecordSheetOpen.value = true
        voiceManager.startListening()
    }

    fun closeRecordSheet() {
        voiceManager.stopListening()
        _isRecordSheetOpen.value = false
    }

    fun selectRecord(record: DreamRecord?) {
        _selectedRecord.value = record
        stopSpeaking()
    }

    fun openSettings(open: Boolean) {
        _isSettingsOpen.value = open
    }

    fun openAuthDialog(open: Boolean) {
        _isAuthDialogOpen.value = open
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle(activity)
            if (result.isSuccess) {
                _isAuthDialogOpen.value = false
            }
        }
    }

    fun signOut() {
        authManager.signOut()
    }

    fun toggleDailyReminder(context: Context, enabled: Boolean) {
        _isDailyReminderEnabled.value = enabled
        if (enabled) {
            SpiritualNotificationHelper.sendMorningDreamPromptNotification(context)
        }
    }

    fun testReminderNotification(context: Context) {
        SpiritualNotificationHelper.sendMorningDreamPromptNotification(context)
    }

    fun toggleFavorite(record: DreamRecord) {
        viewModelScope.launch {
            repository.toggleFavorite(record)
            val updated = record.copy(isFavorite = !record.isFavorite)
            if (_selectedRecord.value?.id == record.id) {
                _selectedRecord.value = updated
            }
            currentUser.value?.let { user ->
                firestoreSync.uploadRecord(user.uid, updated)
            }
        }
    }

    fun deleteRecord(record: DreamRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
            if (_selectedRecord.value?.id == record.id) {
                _selectedRecord.value = null
            }
            currentUser.value?.let { user ->
                firestoreSync.deleteRemoteRecord(user.uid, record.id)
            }
        }
    }

    private var progressJob: Job? = null

    fun interpretSpiritualSpeech(manualTextOverride: String? = null) {
        val textToProcess = (manualTextOverride ?: voiceManager.transcribedText.value).trim()
        if (textToProcess.isBlank()) return

        voiceManager.stopListening()
        _isInterpreting.value = true

        progressJob = viewModelScope.launch {
            val stages = listOf(
                "Discernement spirituel et analyse des symboles…",
                "Consultation des écritures bibliques et théologie…",
                "Éclairage de la sagesse et cosmologie africaine…",
                "Génération des conseils personnalisés et prières d'autorité…"
            )
            var index = 0
            while (_isInterpreting.value) {
                _interpretationProgressText.value = stages[index % stages.size]
                delay(1800)
                index++
            }
        }

        viewModelScope.launch {
            try {
                val result = SpiritualInterpreter.interpret(textToProcess)
                val newId = repository.saveInterpretation(textToProcess, result, "SYNCED_CLOUD_SECURE")
                _isInterpreting.value = false
                progressJob?.cancel()
                _isRecordSheetOpen.value = false

                delay(300)
                val record = repository.getRecordById(newId).first()
                if (record != null) {
                    _selectedRecord.value = record
                    currentUser.value?.let { user ->
                        firestoreSync.uploadRecord(user.uid, record)
                    }
                }
            } catch (e: Exception) {
                _isInterpreting.value = false
                progressJob?.cancel()
                val fallback = SpiritualInterpreter.fallbackSpiritualInterpretation(textToProcess)
                val newId = repository.saveInterpretation(textToProcess, fallback, "OFFLINE_SAVED")
                _isRecordSheetOpen.value = false
                val record = repository.getRecordById(newId).first()
                if (record != null) {
                    _selectedRecord.value = record
                    currentUser.value?.let { user ->
                        firestoreSync.uploadRecord(user.uid, record)
                    }
                }
            }
        }
    }

    fun speakWithHumanAiVoice(text: String) {
        viewModelScope.launch {
            aiVoicePlayer.speak(text, "Kore")
        }
    }

    fun stopSpeaking() {
        aiVoicePlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
        aiVoicePlayer.release()
    }
}
