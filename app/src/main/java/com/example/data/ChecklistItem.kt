package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isChecked: Boolean = false,
    val category: String = "Pre-Trade" // "Pre-Trade", "Confirmation", "Risk Management"
)
