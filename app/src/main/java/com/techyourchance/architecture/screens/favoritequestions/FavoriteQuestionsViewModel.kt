package com.techyourchance.architecture.screens.favoritequestions

import androidx.lifecycle.ViewModel
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.question.FavoriteQuestion
import com.techyourchance.architecture.question.ObserveFavoriteQuestionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class FavoriteQuestionsViewModel(
    favoriteQuestionDao: FavoriteQuestionDao,
): ViewModel() {

    private val observeFavoriteQuestionsUseCase = ObserveFavoriteQuestionsUseCase(favoriteQuestionDao)

    val favoriteQuestions = observeFavoriteQuestionsUseCase.observeFavoriteQuestions()
}