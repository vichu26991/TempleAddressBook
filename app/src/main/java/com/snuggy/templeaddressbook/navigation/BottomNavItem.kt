package com.snuggy.templeaddressbook.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    CONTACTS("contacts", "Contacts", Icons.Outlined.PersonOutline),
    GROUPS("groups", "Groups", Icons.Outlined.Groups),
    MESSAGES("messages", "Messages", Icons.Outlined.ChatBubbleOutline),
    DONATIONS("donations", "Donations", Icons.Outlined.VolunteerActivism);

    companion object {
        fun fromRoute(route: String): BottomNavItem {
            return entries.firstOrNull { it.route == route } ?: CONTACTS
        }
    }
}
