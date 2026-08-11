package com.nisr.sauservices.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        containerColor = AppBackground
    ){ pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ){
            HeroBanner(navController)
            
            Spacer(Modifier.height(16.dp))
            SearchBarUI(navController)

            Spacer(Modifier.height(24.dp))
            QuickServicesRow(navController)

            Spacer(Modifier.height(28.dp))
            CategoriesGrid(navController)

            Spacer(Modifier.height(28.dp))
            PopularServicesSection(navController)

            Spacer(Modifier.height(28.dp))
            HowItWorks()

            Spacer(Modifier.height(28.dp))
            ValuePropositionsRow()

            Spacer(Modifier.height(32.dp))
        }
    }
}
