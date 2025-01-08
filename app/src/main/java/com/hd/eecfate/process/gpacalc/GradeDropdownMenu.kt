package com.hd.eecfate.process.gpacalc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun GradeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    grades: List<String>,
    onGradeSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(Color.White)
        ) {
            grades.forEach { grade ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = grade,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onGradeSelected(grade)
                        onDismissRequest()
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }
}