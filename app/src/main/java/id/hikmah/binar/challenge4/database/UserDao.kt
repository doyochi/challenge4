package id.hikmah.binar.challenge4.database

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Query("SELECT * FROM User WHERE username = :username")
    fun checkUsername(username: String): List<User>

    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    fun checkLogin(username: String, password: String): List<User>

    @Query("SELECT * FROM User WHERE username = :username")
    fun getId(username: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNotes(note: Note): Long

    @Query("SELECT * FROM Note WHERE id_user = :userId")
    fun getAllNote(userId: Int): List<Note>

    @Update
    fun updateNote(note: Note): Int

    @Delete
    fun deleteNote(note: Note): Int

}