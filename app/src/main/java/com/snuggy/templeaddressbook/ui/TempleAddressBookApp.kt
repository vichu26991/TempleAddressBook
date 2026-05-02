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
import com.snuggy.templeaddressbook.ui.tags.ManageTagsScreen
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
    var initialManageTagName by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en
    val drawerItems = listOf(
        Triple("manage_tags", "Manage Tags", "குறிச்சொற்கள் நிர்வாகம்"),
        Triple("manage_templates", "Manage Templates", "வார்ப்புருக்கள் நிர்வாகம்"),
        Triple("smart_groups", "Smart Groups", "ஸ்மார்ட் குழுக்கள்"),
        Triple("address_printing", "Address Printing", "முகவரி அச்சிடல்"),
        Triple("import_contacts", "Import Contacts", "தொடர்புகளை இறக்குமதி செய்"),
        Triple("manage_donation_types", "Manage Donation Types", "நன்கொடை வகைகள் நிர்வாகம்"),
        Triple("manage_donation_items", "Manage Donation Items", "நன்கொடை பொருட்கள் நிர்வாகம்"),
        Triple("backup", "Backup", "காப்புப்பிரதி"),
        Triple("restore", "Restore", "மீட்டமை"),
        Triple("settings", "Settings", "அமைப்புகள்"),
        Triple("about", "About", "பற்றி")
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
                    text = t("Temple Address Book", "கோவில் முகவரி புத்தகம்"),
                    color = OrangePrimary,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
                drawerItems.forEach { item ->
                    val label = if (selectedLanguage == "TA") item.third else item.second
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(label) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            when (item.first) {
                                "about" -> {
                                    utilityScreen = "about"
                                    showBottomBar = false
                                }
                                "manage_tags" -> {
                                    initialManageTagName = null
                                    utilityScreen = "manage_tags"
                                    showBottomBar = false
                                }
                                else -> Toast.makeText(context, t("${item.second} will be completed in the next phases.", "${label} அடுத்த கட்டங்களில் முடிக்கப்படும்."), Toast.LENGTH_SHORT).show()
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

                    "manage_tags" -> ManageTagsScreen(
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = { selectedLanguage = it },
                        onBack = {
                            utilityScreen = null
                            showBottomBar = true
                        },
                        modifier = Modifier.fillMaxSize(),
                        initialTagName = initialManageTagName,
                        onInitialTagConsumed = { initialManageTagName = null }
                    )

                    else -> when (selectedRoute) {
                        BottomNavItem.CONTACTS.route -> ContactsRoot(
                            selectedLanguage = selectedLanguage,
                            onLanguageChange = { selectedLanguage = it },
                            onBottomBarVisibilityChange = { showBottomBar = it },
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onOpenTagDetail = { tagName ->
                                initialManageTagName = tagName
                                utilityScreen = "manage_tags"
                                showBottomBar = false
                            }
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
