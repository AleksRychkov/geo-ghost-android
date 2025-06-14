package dev.aleksrychkov.geoghost.core.designsystem.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.aleksrychkov.geoghost.core.designsystem.theme.Normal
import dev.aleksrychkov.geoghost.core.designsystem.theme.Normal2X

@Composable
fun requestAccess(
    modifier: Modifier,
    title: String,
    subtitle: String,
    btnDenyText: String,
    btnEnableText: String,
    onEnable: () -> Unit,
    onDeny: () -> Unit,
) {
    Column(
        modifier = modifier.padding(Normal)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(Normal)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = subtitle,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(Normal2X)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Normal),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onDeny) {
                Text(btnDenyText)
            }

            Button(onClick = onEnable) {
                Text(btnEnableText)
            }
        }
    }
}
