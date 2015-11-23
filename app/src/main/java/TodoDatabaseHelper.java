import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s.srinivas2 on 11/23/15.
 */

public class TodoDatabaseHelper extends SQLiteOpenHelper {
        // Database Info
        private static final String DATABASE_NAME = "todoDatabase";
        private static final int DATABASE_VERSION = 1;

        // Table Names
        private static final String TABLE_TODO = "todo";

        // Todo Table Columns
        private static final String KEY_TODO_ID = "id";
        private static final String KEY_TODO_DESC = "desc";
        private static final String KEY_TODO_STATUS = "status";

        private static TodoDatabaseHelper sInstance;

        public static synchronized TodoDatabaseHelper getInstance(Context context) {
            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            // See this article for more information: http://bit.ly/6LRzfx
            if (sInstance == null) {
                sInstance = new TodoDatabaseHelper(context.getApplicationContext());
            }
            return sInstance;
        }

        /**
         * Constructor should be private to prevent direct instantiation.
         * Make a call to the static method "getInstance()" instead.
         */
        private TodoDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Called when the database connection is being configured.
        // Configure database settings for things like foreign key support, write-ahead logging, etc.
        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            db.setForeignKeyConstraintsEnabled(true);
        }

        // Called when the database is created for the FIRST time.
        // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO +
                    "(" +
                    KEY_TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                    KEY_TODO_DESC + " TEXT," +
                    KEY_TODO_STATUS + " TEXT" +
                    ")";

            db.execSQL(CREATE_TODO_TABLE);
        }

        // Called when the database needs to be upgraded.
        // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
        // but the DATABASE_VERSION is different than the version of the database that exists on disk.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                // Simplest implementation is to drop all old tables and recreate them
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
                onCreate(db);
            }
        }

        // Insert a todo into the database
        public void addTodo(Todo todo) {
            // Create and/or open the database for writing
            SQLiteDatabase db = getWritableDatabase();

            // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
            // consistency of the database.
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put(KEY_TODO_DESC, todo.desc);
                values.put(KEY_TODO_STATUS, todo.status);

                // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
                db.insertOrThrow(TABLE_TODO, null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.d(TAG, "Error while trying to add todo to database");
            } finally {
                db.endTransaction();
            }
        }



        // Get all todos in the database
        public List<Todo> getAllTodos() {
            List<Todo> todos = new ArrayList<>();

            String TODO_SELECT_QUERY =
                    String.format("SELECT * FROM %s",
                            TABLE_TODO);

            // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
            // disk space scenarios)
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(TODO_SELECT_QUERY, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Todo newTodo = new Todo();
                        newTodo.desc = cursor.getString(cursor.getColumnIndex(KEY_TODO_DESC));
                        newTodo.status = cursor.getString(cursor.getColumnIndex(KEY_TODO_STATUS));
                        todos.add(newTodo);
                    } while(cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.d(TAG, "Error while trying to get todos from database");
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            return todos;
        }

    // Get all todos in the database
    // The implementation here needs to be changed to remove the cursor
    public Todo getTodoById(int Id) {
        List<Todo> todos = new ArrayList<>();

        String TODO_SELECT_QUERY =
                String.format("SELECT * FROM %s Where Id=%s",
                        TABLE_TODO, Id);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo();
                    newTodo.desc = cursor.getString(cursor.getColumnIndex(KEY_TODO_DESC));
                    newTodo.status = cursor.getString(cursor.getColumnIndex(KEY_TODO_STATUS));
                    todos.add(newTodo);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get todos from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todos.get(0);
    }


        // Delete all todos and users in the database
        public void deleteAllTodos() {
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                // Order of deletions is important when foreign key relationships exist.
                db.delete(TABLE_TODO, null, null);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.d(TAG, "Error while trying to delete all TODOs");
            } finally {
                db.endTransaction();
            }
        }
    }