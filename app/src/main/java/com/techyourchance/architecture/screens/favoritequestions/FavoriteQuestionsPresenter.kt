package com.techyourchance.architecture.screens.favoritequestions

import com.techyourchance.architecture.common.database.FavoriteQuestionDao

class FavoriteQuestionsPresenter(
    favoriteQuestionDao: FavoriteQuestionDao,
) {

    val favoriteQuestions = favoriteQuestionDao.observe()

}