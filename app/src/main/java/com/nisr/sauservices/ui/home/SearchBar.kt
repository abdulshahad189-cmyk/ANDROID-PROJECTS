package com.nisr.sauservices.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.theme.Black
import com.nisr.sauservices.ui.theme.GrayText
import com.nisr.sauservices.ui.theme.PrimaryBlue
import com.nisr.sauservices.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarUI(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    
    val elevation by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 2.dp,
        animationSpec = tween(durationMillis = 200)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(elevation, RoundedCornerShape(18.dp))
            .background(White, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
            
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search for \"Electrician, Cleaning, Salon...\"",
                        color = GrayText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isFocused = it.isFocused },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = PrimaryBlue,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            navController.navigate("search_results/$searchQuery")
                        }
                    }
                )
            )
        }
    }
}
