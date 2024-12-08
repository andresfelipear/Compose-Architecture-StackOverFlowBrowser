package com.techyourchance.architecture.screens.favoritequestions

import androidx.compose.runtime.collectAsState
import androidx.room.Room
import com.techyourchance.architecture.common.database.FavoriteQuestionDao
import com.techyourchance.architecture.common.database.MyRoomDatabase
import com.techyourchance.architecture.question.QuestionSchema
import kotlinx.coroutines.flow.MutableStateFlow

class FavoriteQuestionsPresenter(
    private val favoriteQuestionDao: FavoriteQuestionDao,
) {

    val favoriteQuestions = favoriteQuestionDao.observe()

}