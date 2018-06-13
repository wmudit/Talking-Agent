package co.pucho.pucho;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LogHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "Logs.db";
    public static final String TABLE_NAME = "conversation";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SPEAKER = "Speaker";
    public static final String COLUMN_SPEECH = "Speech";

    public LogHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, NAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
                COLUMN_SPEAKER + " TEXT" + ", " +
                COLUMN_SPEECH + " TEXT" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Adding new row to database
    public void add(DataModel dataModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SPEAKER, dataModel.getSpeaker());
        values.put(COLUMN_SPEECH, dataModel.getSpeech() );
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //Clear Table
    public void clear() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Get values from database
    public List<String> getValues() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE 1";

        Cursor cursor = db.rawQuery(query, null) ;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(cursor.getColumnIndex(COLUMN_SPEAKER)) != null) {
                //Log.d("Handler", cursor.getString(cursor.getColumnIndex(COLUMN_SPEECH)));
                list.add(cursor.getString(cursor.getColumnIndex(COLUMN_SPEECH)));
            }
            cursor.moveToNext();
        }
        db.close();
        return list;
    }

}
