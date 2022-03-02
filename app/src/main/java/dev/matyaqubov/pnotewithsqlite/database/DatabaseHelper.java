package dev.matyaqubov.pnotewithsqlite.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import dev.matyaqubov.pnotewithsqlite.database.model.Note;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //
    private static final String DATABASE_NAME = "note_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create note table
        db.execSQL(Note.CREATE_TABLE);
    }

    //upgrading db
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int n) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);

        //create tables again
        onCreate(db);
    }

    public long insertNote(String note) {
        //ruxsat alish
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Note.COLUMN_NOTE, note);
        //insert row
        long id = db.insert(Note.TABLE_NAME, null, values);

        //close
        db.close();

        return id;

    }

    public Note getNote(long id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME, new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        //prepare note

        @SuppressLint("Range")
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))
        );

        cursor.close();
        return note;
    }

    public List<Note> getAllNotes(){
        List<Note> notes=new ArrayList<>();

        //select All Query
        String selectQuery="SELECT * FROM "+ Note.TABLE_NAME + " ORDER BY " + Note.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

//        if (cursor.moveToNext()){
//            do {
//                @SuppressLint("Range")
//                Note note = new Note(
//                        cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
//                        cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
//                        cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))
//                );
//                notes.add(note);
//            } while (cursor.moveToNext());
//        }

        while (cursor.moveToNext()){
            @SuppressLint("Range")
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                        cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))
                );
                notes.add(note);
        }

        db.close();

        return notes;
    }

    public int getNotesCount(){

        String countQuery = "SELECT * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }



    public  int updateNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE,note.getNote());

        return db.update(Note.TABLE_NAME,values,Note.COLUMN_ID + " = ?",
        new String[] {String.valueOf(note.getId())});
    }

    public void deleteNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME,Note.COLUMN_ID+" = ?",new String[]{String.valueOf(note.getId())});
        db.close();
    }

}
