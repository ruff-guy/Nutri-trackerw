package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.EmeraldPrimary

@Composable
fun FoodImage(
    imageUri: String?,
    foodName: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val roundedModifier = modifier
        .size(size)
        .clip(RoundedCornerShape(12.dp))

    val context = LocalContext.current
    val presetDrawableRes = getPresetDrawableRes(imageUri, foodName)

    if (presetDrawableRes != null) {
        Image(
            painter = painterResource(id = presetDrawableRes),
            contentDescription = foodName,
            modifier = roundedModifier,
            contentScale = ContentScale.Crop
        )
    } else if (!imageUri.isNullOrBlank() && (imageUri.startsWith("content://") || imageUri.startsWith("file://") || imageUri.startsWith("http"))) {
        AsyncImage(
            model = imageUri,
            contentDescription = foodName,
            modifier = roundedModifier,
            contentScale = ContentScale.Crop
        )
    } else {
        // Fallback icon badge
        val (bgColor, icon) = getPresetImageInfo(imageUri, foodName)
        Box(
            modifier = roundedModifier.background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = foodName,
                tint = Color.White,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

private fun getPresetDrawableRes(imageUri: String?, foodName: String): Int? {
    val identifier = (imageUri ?: "").lowercase()
    val nameLower = foodName.lowercase()

    return when {
        identifier == "preset_avocado_toast" || nameLower.contains("avocado") ->
            R.drawable.food_avocado_toast_1784925724874
        identifier == "preset_oatmeal" || nameLower.contains("oatmeal") ->
            R.drawable.food_oatmeal_bowl_1784925739169
        identifier == "preset_chicken_rice" || (nameLower.contains("chicken") && nameLower.contains("rice")) ->
            R.drawable.food_grilled_chicken_1784925752177
        identifier == "preset_salmon_bowl" || nameLower.contains("salmon") ->
            R.drawable.food_salmon_bowl_1784925765584
        identifier == "preset_yogurt" || nameLower.contains("yogurt") ->
            R.drawable.food_greek_yogurt_1784925780753
        else -> null
    }
}

private fun getPresetImageInfo(presetTag: String?, foodName: String): Pair<Color, ImageVector> {
    val nameLower = (presetTag ?: foodName).lowercase()
    return when {
        nameLower.contains("avocado") || nameLower.contains("toast") ->
            Color(0xFF84CC16) to Icons.Default.Restaurant
        nameLower.contains("oatmeal") || nameLower.contains("cereal") || nameLower.contains("bowl") ->
            Color(0xFFF59E0B) to Icons.Default.RiceBowl
        nameLower.contains("chicken") || nameLower.contains("meat") || nameLower.contains("steak") ->
            Color(0xFFE11D48) to Icons.Default.DinnerDining
        nameLower.contains("salmon") || nameLower.contains("fish") ->
            Color(0xFFF97316) to Icons.Default.SetMeal
        nameLower.contains("yogurt") || nameLower.contains("milk") || nameLower.contains("cheese") ->
            Color(0xFF0EA5E9) to Icons.Default.BakeryDining
        nameLower.contains("shake") || nameLower.contains("protein") || nameLower.contains("drink") ->
            Color(0xFF8B5CF6) to Icons.Default.LocalDrink
        nameLower.contains("apple") || nameLower.contains("fruit") || nameLower.contains("banana") ->
            Color(0xFFEF4444) to Icons.Default.LocalDining
        nameLower.contains("salad") || nameLower.contains("green") || nameLower.contains("veg") ->
            Color(0xFF10B981) to Icons.Default.Eco
        nameLower.contains("coffee") || nameLower.contains("latte") || nameLower.contains("tea") ->
            Color(0xFF78350F) to Icons.Default.Coffee
        else ->
            EmeraldPrimary to Icons.Default.Fastfood
    }
}

