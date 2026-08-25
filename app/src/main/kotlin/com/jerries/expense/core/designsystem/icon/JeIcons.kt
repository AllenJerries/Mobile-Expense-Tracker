package com.jerries.expense.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.jerries.expense.domain.model.AccountType

object JeIcons {

    val Fallback: ImageVector = Icons.Filled.Category

    private val categoryIcons: Map<String, ImageVector> = mapOf(
        "shopping_cart" to Icons.Filled.ShoppingCart,
        "restaurant" to Icons.Filled.Restaurant,
        "directions_bus" to Icons.Filled.DirectionsBus,
        "home" to Icons.Filled.Home,
        "bolt" to Icons.Filled.Bolt,
        "movie" to Icons.Filled.Movie,
        "health_and_safety" to Icons.Filled.HealthAndSafety,
        "storefront" to Icons.Filled.Storefront,
        "payments" to Icons.Filled.Payments,
        "work" to Icons.Filled.Work,
        "redeem" to Icons.Filled.Redeem,
        "attach_money" to Icons.Filled.AttachMoney,
    )

    fun category(key: String?): ImageVector =
        key?.let { categoryIcons[it] } ?: Fallback

    fun account(type: AccountType): ImageVector = when (type) {
        AccountType.CASH -> Icons.Filled.Payments
        AccountType.BANK -> Icons.Filled.AccountBalance
        AccountType.UPI -> Icons.Filled.QrCode
        AccountType.CREDIT_CARD -> Icons.Filled.CreditCard
        AccountType.DEBIT_CARD -> Icons.Filled.Receipt
        AccountType.SAVINGS -> Icons.Filled.Savings
        AccountType.OTHER -> Icons.Filled.Wallet
    }
}
