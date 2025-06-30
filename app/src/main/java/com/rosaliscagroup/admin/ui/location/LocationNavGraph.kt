package com.rosaliscagroup.admin.ui.location

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import com.rosaliscagroup.admin.data.entity.Location

fun NavGraphBuilder.locationNavGraph(
    navController: NavController,
    locationsProvider: () -> List<Location>
) {
    composable("AllLocationsScreen") {
        AllLocationsScreen(
            locations = locationsProvider(),
            navController = navController,
            onBack = { navController.popBackStack() }
        )
    }
}
