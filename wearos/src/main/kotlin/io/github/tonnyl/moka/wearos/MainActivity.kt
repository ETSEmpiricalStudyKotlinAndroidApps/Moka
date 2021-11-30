package io.github.tonnyl.moka.wearos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import io.github.tonnyl.moka.wearos.ui.timeline.TimelineScreen

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalWearMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberSwipeDismissableNavController()

                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = Screen.Timeline.route
                    ) {
                        composable(route = Screen.Timeline.route) {
                            TimelineScreen()
                        }
                    }
                }
            }
        }
    }
}