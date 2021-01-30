package com.example.studentmarkscalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class StudentRecordsDbAdapter {
   
    public static final String STUDENT_ID = "_id";
    public static final String STUDENT_FIRSTNAME = "firstname";
    public static final String STUDENT_LASTNAME = "lastname";
    public static final String MARK_STUDENT_ID = "_id";
    public static final String MARK_LAB = "labmark";
    public static final String MARK_MIDTERM = "midtermmark";
    public static final String MARK_FINAL_EXAM = "finalexammark";
    public static final String AVG_MARK_LAB = "avg("+MARK_LAB+")";
    public static final String AVG_MARK_MIDTERM = "avg("+MARK_MIDTERM+")";
    public static final String AVG_MARK_FINAL_EXAM = "avg("+MARK_FINAL_EXAM+")";
    private static final String TAG = "StudentRecordsDbAdapter";

    private DatabaseHelper mDbHelper;

    private SQLiteDatabase mDb;

  
    private static final String DATABASE_NAME = "studentrecords";

 
    private static final String SQLITE_STUDENT_TABLE = "student";

    private static final String SQLITE_MARKS_TABLE = "marks";

    private static final int DATABASE_VERSION = 6;

    private final Context mCtx;

    private static final String CREATE_STUDENT_TABLE =
            "CREATE TABLE if not exists " + SQLITE_STUDENT_TABLE + " ("
                    + STUDENT_ID + " integer PRIMARY KEY NOT NULL,"
                    + STUDENT_FIRSTNAME + " NOT NULL,"
                    + STUDENT_LASTNAME + " NOT NULL);"
            ;

    private static final String CREATE_MARKS_TABLE =
            "CREATE TABLE if not exists " + SQLITE_MARKS_TABLE + " ("
                    + STUDENT_ID + " integer PRIMARY KEY NOT NULL,"
                    + MARK_LAB + " real,"
                    + MARK_MIDTERM + " real,"
                    + MARK_FINAL_EXAM + " real,"
                    + "CONSTRAINT fk_student FOREIGN KEY (" + MARK_STUDENT_ID + ")"
                    + " REFERENCES " + SQLITE_STUDENT_TABLE + "(" + STUDENT_ID + ")"
                    + ");"
            
  

 
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STUDENT_TABLE);
            db.execSQL(CREATE_MARKS_TABLE);
            db.execSQL(INSERT_SAMPLE_STUDENTS);
            db.execSQL(INSERT_SAMPLE_MARKS);
        }

      
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_STUDENT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_MARKS_TABLE);
            onCreate(db);
        }
    }

    public StudentRecordsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public StudentRecordsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

   
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long insertStudent(int studentNumber, String firstname, String lastname) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(STUDENT_ID, studentNumber);
        initialValues.put(STUDENT_FIRSTNAME, firstname);
        initialValues.put(STUDENT_LASTNAME, lastname);

        return mDb.insertOrThrow(SQLITE_STUDENT_TABLE, null, initialValues);
    }


    public long insertMarks(int studentId, Double labMark, Double midtermMark, Double finalExamMark) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(STUDENT_ID, studentId);
        initialValues.put(MARK_LAB, labMark);
        initialValues.put(MARK_MIDTERM, midtermMark);
        initialValues.put(MARK_FINAL_EXAM, finalExamMark);

        return mDb.insertOrThrow(SQLITE_MARKS_TABLE, null, initialValues);
    }

   
    public Cursor fetchAllStudents() {
        Cursor mCursor = mDb.query(SQLITE_STUDENT_TABLE, new String[] {STUDENT_ID, STUDENT_FIRSTNAME, STUDENT_LASTNAME},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

  
    public Cursor fetchStudentById(Long id) {
        Log.w(TAG, id.toString());
        Cursor mCursor = null;
        if (id == null  ||  id.toString().length () == 0)  {
            mCursor = mDb.query(SQLITE_STUDENT_TABLE, new String[] {STUDENT_ID,
                            STUDENT_FIRSTNAME, STUDENT_LASTNAME},
                    null, null, null, null, null);

        }
        else {
            mCursor = mDb.query(true, SQLITE_STUDENT_TABLE, new String[] {STUDENT_ID,
                            STUDENT_FIRSTNAME, STUDENT_LASTNAME},
                    STUDENT_ID + " = " + id, null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

  
    public Cursor fetchMarksByStudentId(long studentId) {
        Cursor mCursor = mDb.query(SQLITE_MARKS_TABLE,
                new String[] {MARK_LAB, MARK_MIDTERM, MARK_FINAL_EXAM},
                STUDENT_ID + " = ?",
                new String[] {Long.toString(studentId)},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

  
    public Cursor fetchAverageMarks() {
        Cursor mCursor = mDb.query(SQLITE_MARKS_TABLE,
                new String[] {AVG_MARK_LAB, AVG_MARK_MIDTERM, AVG_MARK_FINAL_EXAM},
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

   
    public int updateMarks(long studentId, Double labMark, Double midtermMark, Double finalExamMark) {
        ContentValues newValues = new ContentValues();
        newValues.put(MARK_LAB, labMark);
        newValues.put(MARK_MIDTERM, midtermMark);
        newValues.put(MARK_FINAL_EXAM, finalExamMark);
        return mDb.update(
                SQLITE_MARKS_TABLE,
                newValues,
                STUDENT_ID + " = ?",
                new String[] {Long.toString(studentId)}
        );
    }

 
    public boolean deleteStudentAndMarksById(long id) {
        int doneDelete = 0;
        doneDelete += mDb.delete(
                SQLITE_STUDENT_TABLE,
                STUDENT_ID + " = ?",
                new String[] {Long.toString(id)}
        );
        doneDelete += mDb.delete(
                SQLITE_MARKS_TABLE,
                STUDENT_ID + " = ?",
                new String[] {Long.toString(id)}
        );
        return doneDelete == 2;
    }

   
    public boolean deleteAllStudents() {
        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_STUDENT_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    public boolean deleteAllMarks() {
        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_MARKS_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }
}
