package com.techyourchance.architecture.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.ByteString.Companion.decodeBase64

class ScreensNavigator
{
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    private lateinit var parentNavController: NavHostController
    private lateinit var nestedNavController: NavHostController

    private var nestedNavControllerObserveJob: Job? = null
    private var parentNavControllerObserveJob: Job? = null

    val currentBottomTab = MutableStateFlow<BottomTab?>(null)
    val currentRoute = MutableStateFlow<Route?>(null)
    val isRootRoute = MutableStateFlow(false)

    fun setParentNavController(navController: NavHostController)
    {
        parentNavController = navController

        parentNavControllerObserveJob?.cancel()
        parentNavControllerObserveJob = scope.launch {
            navController.currentBackStackEntryFlow.map { backStackEntry ->
                val bottomTab = when(val routeName = backStackEntry.destination.route){
                    Route.MainTab.routeName -> BottomTab.Main
                    Route.FavoritesTab.routeName -> BottomTab.Favorites
                    null -> null
                    else -> throw RuntimeException("unsupported bottom tab: $routeName")
                }
                Pair(bottomTab, backStackEntry.arguments)
            }.collect { (bottomTab) ->
                currentBottomTab.value = bottomTab
            }
        }
    }

    fun setNestedNavController(navController: NavHostController)
    {
        nestedNavController = navController

        nestedNavControllerObserveJob?.cancel()
        nestedNavControllerObserveJob = scope.launch {
            navController.currentBackStackEntryFlow.map { backStackEntry ->
                val route = when(val routeName = backStackEntry.destination.route) {
                    Route.MainTab.routeName -> Route.MainTab
                    Route.FavoritesTab.routeName -> Route.FavoritesTab
                    Route.QuestionsListScreen.routeName -> Route.QuestionsListScreen
                    Route.QuestionDetailsScreen().routeName -> {
                        val args = backStackEntry.arguments
                        Route.QuestionDetailsScreen(
                            args?.getString("questionId")!!,
                            args.getString("questionTitle")!!
                        )
                    }
                    Route.FavoriteQuestionsScreen.routeName -> Route.FavoriteQuestionsScreen
                    null -> null
                    else -> throw RuntimeException("unsupported route $routeName")
                }
                Pair(route, backStackEntry.arguments)
            }.collect { (route) ->
                currentRoute.value = route
                isRootRoute.value = route == Route.QuestionsListScreen
                println("Updated currentRoute: $route, isRootRoute: ${isRootRoute.value}")
            }
        }
    }

    fun navigateBack()
    {
        println("navigateBack")
        if(!nestedNavController.popBackStack()) {
            parentNavController.popBackStack()
        }
    }

    fun toTab(bottomTab: BottomTab)
    {
        val route = when(bottomTab) {
            BottomTab.Favorites -> Route.FavoritesTab
            BottomTab.Main -> Route.MainTab
        }

        println("to tab: ${route.routeName}")

        val destinationExists = parentNavController.graph.findNode(route.routeName) != null

        if(destinationExists)
        {
            parentNavController.navigate(route.routeName) {
                parentNavController.graph.startDestinationRoute?.let { startRoute ->
                    println("startRoute: $startRoute")
                    popUpTo(startRoute) {
                        println("popUpTo: $startRoute")
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }else{
            Log.e("Navigation", "Destination route does not exist: ${route.routeName}")
        }
    }

    fun toRoute(route: Route)
    {
        nestedNavController.navigate(route.navCommand)
    }

    companion object
    {
        val BOTTOM_TABS = listOf(BottomTab.Main, BottomTab.Favorites)
    }
}