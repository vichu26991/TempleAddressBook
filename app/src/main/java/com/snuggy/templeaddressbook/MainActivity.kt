package com.snuggy.templeaddressbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.snuggy.templeaddressbook.ui.TempleAddressBookRoot
import com.snuggy.templeaddressbook.ui.theme.TempleAddressBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TempleAddressBookTheme {
                TempleAddressBookRoot()
            }
        }
    }
}