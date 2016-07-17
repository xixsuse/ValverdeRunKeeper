package com.example.valverde.valverderunkeeper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.valverde.valverderunkeeper.running.processing_result.RunResult;
import java.util.ArrayList;

public class DatabaseRunResultsHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "valverdeRunKeeper.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "results";
    private static final String ID_COL = "ID";
    private static final String RESULT_ID_COL = "RESULT_ID";
    private static final String TIME_COL = "TIME";
    private static final String DISTANCE_COL = "DISTANCE";
    private static final String CALORIES_COL = "CALORIES";
    private static final String DATE_COL = "DATE";
    public static final String SQL_CREATE_QUERY = "CREATE TABLE "+TABLE_NAME+
            " ("+ID_COL+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            RESULT_ID_COL+" INTEGER, "+
            TIME_COL+" INTEGER,"+
            DISTANCE_COL+" REAL,"+
            CALORIES_COL +" INTEGER, "+
            DATE_COL+" INTEGER)";
    public static final String SQL_DROP_QUERY = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DatabaseRunResultsHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP_QUERY);
        onCreate(sqLiteDatabase);
    }

    public boolean insertResult(RunResult result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESULT_ID_COL, result.getResultId());
        contentValues.put(TIME_COL, result.getTime());
        contentValues.put(DISTANCE_COL, result.getDistance());
        contentValues.put(CALORIES_COL, result.getCalories());
        contentValues.put(DATE_COL, result.getDate());
        long res = db.insert(TABLE_NAME, null, contentValues);
        if (res == -1) return false;
        else return true;
    }

    public ArrayList<RunResult> getAllResults() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { ID_COL, RESULT_ID_COL, TIME_COL, DISTANCE_COL, CALORIES_COL, DATE_COL };
        Cursor c = db.query(TABLE_NAME, projection, null, null, null, null, null);

        ArrayList<RunResult> results = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                long resultId = c.getLong(1);
                long time = c.getLong(2);
                double distance = c.getDouble(3);
                int calories = c.getInt(4);
                long date = c.getLong(5);
                RunResult result = new RunResult(time, distance, calories);
                result.setDate(date);
                result.setResultId(resultId);
                results.add(result);
            } while (c.moveToNext());
        }
        return results;
    }

    public long getMaxResultId() {
        SQLiteDatabase db = this.getWritableDatabase();
        String SQL = "select * from "+TABLE_NAME+
                " where "+RESULT_ID_COL+"=" +
                "(select max("+RESULT_ID_COL+") from "+TABLE_NAME+")" +
                "order by id desc "+
                "limit 1";
        Cursor c = db.rawQuery(SQL, null);
        long id = -1;
        if (c.getCount() > 0){
            if (c.moveToFirst()) {
                do {
                    id = c.getLong(1);
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return id;
    }

    public void removeResult(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String SQL = "delete from "+TABLE_NAME+
                " where "+RESULT_ID_COL+"="+id;
        db.execSQL(SQL);
    }
}