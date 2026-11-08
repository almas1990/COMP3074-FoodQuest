package com.comp3074.foodquest.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.comp3074.foodquest.data.FoodQuestDatabase
import com.comp3074.foodquest.data.Quest
import com.comp3074.foodquest.data.QuestRepository
import com.comp3074.foodquest.data.TeamMember
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuestRepository(FoodQuestDatabase.open(application))
    private val questData = MutableLiveData<List<Quest>>(emptyList())
    private val memberData = MutableLiveData<List<TeamMember>>(emptyList())
    private val busyData = MutableLiveData(false)
    private val errorData = MutableLiveData<String?>(null)

    val quests: LiveData<List<Quest>> = questData
    val members: LiveData<List<TeamMember>> = memberData
    val busy: LiveData<Boolean> = busyData
    val error: LiveData<String?> = errorData

    init {
        refresh()
    }

    fun refresh() = update({ Unit })

    fun save(quest: Quest, onSaved: (Long) -> Unit) =
        update({ repository.save(quest) }, onSaved)

    fun delete(id: Long, onDeleted: () -> Unit) =
        update({ repository.delete(id) }, { onDeleted() })

    fun complete(id: Long) = update({ repository.complete(id) })

    fun rate(id: Long, rating: Int) = update({ repository.rate(id, rating) })

    fun saveMember(member: TeamMember, onSaved: () -> Unit) =
        update({ repository.saveMember(member) }, { onSaved() })

    fun clearError() {
        errorData.value = null
    }

    private fun <T> update(action: () -> T, onSuccess: (T) -> Unit = {}) {
        if (busyData.value == true) return
        busyData.value = true

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { action() }
                val savedData = withContext(Dispatchers.IO) {
                    repository.quests() to repository.members()
                }
                questData.value = savedData.first
                memberData.value = savedData.second
                onSuccess(result)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                errorData.value = "Could not save or load data. Please try again."
                Log.e("QuestViewModel", "Database operation failed", error)
            } finally {
                busyData.value = false
            }
        }
    }

    override fun onCleared() {
        repository.close()
    }
}
