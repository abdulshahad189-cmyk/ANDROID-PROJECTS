package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.essentials.HomeEssentialsSheetContent

import com.nisr.sauservices.ui.theme.AppBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            HomeEssentialsSheetContent(navController) {
                showSheet = false
            }
        }
    }

    Scaffold(
        topBar = { TopAppBarUI(navController, sessionManager) },
        bottomBar = { BottomNavBar(navController) },
        containerColor = AppBackground // Premium neutral background
    ){ pad ->

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(250, easing = FastOutSlowInEasing)) + 
                    slideInVertically(
                        initialOffsetY = { 40 }, // Approx 16dp
                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                    )
        ) {
            Column(
                Modifier
                    .padding(pad)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ){

                HeroBanner(navController)
                
                Spacer(Modifier.height(18.dp))
                SearchBarUI(navController)

                Spacer(Modifier.height(24.dp))
                QuickServicesRow(navController)

                Spacer(Modifier.height(32.dp))
                CategoriesGrid(navController, onHomeEssentialsClick = {
                    showSheet = true
                })

                Spacer(Modifier.height(24.dp))
                ValuePropositionsRow() // Trust Banner

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
