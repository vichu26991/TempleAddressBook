package com.snuggy.templeaddressbook.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snuggy.templeaddressbook.navigation.BottomNavItem
import com.snuggy.templeaddressbook.ui.about.AboutBuildInfoScreen
import com.snuggy.templeaddressbook.ui.contacts.ContactsRoot
import com.snuggy.templeaddressbook.ui.donations.DonationsScreen
import com.snuggy.templeaddressbook.ui.groups.GroupsScreen
import com.snuggy.templeaddressbook.ui.messages.MessagesScreen
import com.snuggy.templeaddressbook.ui.theme.AppBg
import com.snuggy.templeaddressbook.ui.theme.CardWhite
import com.snuggy.templeaddressbook.ui.theme.OrangePrimary
import kotlinx.coroutines.launch

@Composable
fun TempleAddressBookApp() {
    var selectedRoute by rememberSaveable { mutableStateOf(BottomNavItem.CONTACTS.route) }
    var selectedLanguage by rememberSaveable { mutableStateOf("EN") }
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    var utilityScreen by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerItems = listOf(
        "Manage Tags",
        "Manage Templates",
        "Smart Groups",
        "Address Printing",
        "Import Contacts",
        "Manage Donation Types",
        "Manage Donation Items",
        "Backup",
        "Restore",
        "Settings",
        "About"
    )

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    BackHandler(enabled = utilityScreen != null) {
        utilityScreen = null
        showBottomBar = true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardWhite,
                modifier = Modifier.widthIn(max = 320.dp)
            ) {
                Text(
                    text = "Temple Address Book",
                    color = OrangePrimary,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
                drawerItems.forEach { item ->
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(item) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (item == "About") {
                                utilityScreen = "about"
                                showBottomBar = false
                            } else {
                                Toast.makeText(context, "$item will be completed in the next phases.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TempleBottomBar(
                        selectedRoute = selectedRoute,
                        onItemSelected = { item ->
                            selectedRoute = item.route
                            utilityScreen = null
                            showBottomBar = true
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(AppBg)
            ) {
                when (utilityScreen) {
                    "about" -> AboutBuildInfoScreen(
                        onBack = {
                            utilityScreen = null
                            showBottomBar = true
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    else -> when (selectedRoute) {
                        BottomNavItem.CONTACTS.route -> ContactsRoot(
                            selectedLanguage = selectedLanguage,
                            onLanguageChange = { selectedLanguage = it },
                            onBottomBarVisibilityChange = { showBottomBar = it },
                            onOpenMenu = { scope.launch { drawerState.open() } }
                        )

                        BottomNavItem.GROUPS.route -> GroupsScreen(Modifier.fillMaxSize())
                        BottomNavItem.MESSAGES.route -> MessagesScreen(Modifier.fillMaxSize())
                        BottomNavItem.DONATIONS.route -> DonationsScreen(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
private fun TempleBottomBar(selectedRoute: String, onItemSelected: (BottomNavItem) -> Unit) {
    NavigationBar(
        modifier = Modifier.height(60.dp),
        containerColor = CardWhite,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onItemSelected(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangePrimary,
                    selectedTextColor = OrangePrimary,
                    indicatorColor = OrangePrimary.copy(alpha = 0.12f),
                    unselectedIconColor = Color(0xFF7A7A84),
                    unselectedTextColor = Color(0xFF7A7A84)
                ),
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, fontSize = 11.sp) },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
fun TempleAddressBookRoot() {
    TempleAddressBookApp()
}
