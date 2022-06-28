package id.hikmah.binar.challenge4.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "id_user") val idUser: Int,
    @ColumnInfo(name = "title_note") val titleNote: String,
    @ColumnInfo(name = "note") val note: String
)
