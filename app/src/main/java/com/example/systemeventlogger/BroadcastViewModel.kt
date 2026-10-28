package com.example.systemeventlogger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BroadcastViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BroadcastRepository(application.applicationContext)

    private val _events = MutableStateFlow<List<BroadcastEvent>>(emptyList())
    val events: StateFlow<List<BroadcastEvent>> = _events.asStateFlow()

    init {
        viewModelScope.launch {
            repository.systemEventsFlow.collect { newEvent ->
                _events.value = listOf(newEvent) + _events.value
            }
        }
    }

    fun deleteEvent(event: BroadcastEvent) {
        _events.value = _events.value.filterNot { it.id == event.id }
    }
}