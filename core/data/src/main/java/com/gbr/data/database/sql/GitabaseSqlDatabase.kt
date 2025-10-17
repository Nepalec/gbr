package com.gbr.data.database.sql

import android.database.sqlite.SQLiteDatabase
import com.gbr.data.database.dao.SqlBookDao
import java.io.File

/**
 * Wrapper for SQLiteDatabase that provides a Room-like interface.
 * Used for reading gitabase files with native SQLite instead of Room.
 */
class GitabaseSqlDatabase(
    private val sqliteDatabase: SQLiteDatabase,
    private val file: File
) {
    
    private val bookDao = SqlBookDao(sqliteDatabase)
    
    /**
     * Returns the SqlBookDao for querying books.
     * 
     * @return SqlBookDao implementation using raw SQLite
     */
    fun bookDao(): SqlBookDao = bookDao
    
    /**
     * Closes the underlying SQLiteDatabase.
     * Should be called when the database is no longer needed.
     */
    fun close() {
        if (sqliteDatabase.isOpen) {
            sqliteDatabase.close()
        }
    }
    
    /**
     * Checks if the database is currently open.
     * 
     * @return true if database is open, false otherwise
     */
    fun isOpen(): Boolean = sqliteDatabase.isOpen
    
    /**
     * Gets the file path of the database.
     * 
     * @return the absolute path to the database file
     */
    fun getFilePath(): String = file.absolutePath
    
    companion object {
        /**
         * Opens a SQLiteDatabase from a file path.
         * Opens in read-only mode for safety.
         * 
         * @param filePath The absolute path to the .db file
         * @return GitabaseSqlDatabase instance
         * @throws IllegalArgumentException if file doesn't exist or can't be read
         */
        fun open(filePath: String): GitabaseSqlDatabase {
            val file = File(filePath)
            
            if (!file.exists()) {
                throw IllegalArgumentException("Database file does not exist: $filePath")
            }
            
            if (!file.canRead()) {
                throw IllegalArgumentException("Cannot read database file: $filePath")
            }
            
            val sqliteDatabase = SQLiteDatabase.openDatabase(
                filePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            
            return GitabaseSqlDatabase(sqliteDatabase, file)
        }
    }
}
