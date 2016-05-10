
package com.rayerrt.attributionquery;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLData;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "AttributionQuery";

    private static final String NUMBER_COLUMN_NAME = "number";
    private static final String CITY_COLUMN_ID = "city_id";
    private static final String FLAG_COLUMN = "flag";
    private static final String CITY_COLUMN_NAME = "city";

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public String queryAttributionIfNumberEquals(SQLiteDatabase db, String tableName, String number) {
        Cursor cursor = null;
        String value = null;
        try {
            cursor = db.query(tableName, new String[]{
                    CITY_COLUMN_NAME
            }, "number='" + number + "'", null
                    , null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                value = cursor.getString(cursor.getColumnIndex(CITY_COLUMN_NAME));
            }
//        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        Log.d(TAG, "number is " + number + ", value is " + value);
        return value;
    }

    public String getStringValueFromMobile(SQLiteDatabase db, String table, int number, int flag) {
        Cursor cursor = null;
        String val = null;
        try {
            cursor = db.query(table, new String[]{
                            CITY_COLUMN_NAME
                    }, "number='" + number + "' and flag=" + flag,
                    null, null, null, FLAG_COLUMN);
            if (null != cursor && cursor.moveToFirst()) {
                val = cursor.getString(cursor.getColumnIndex(CITY_COLUMN_NAME));
            }
//        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return val;
    }

    public ArrayList<String> queryTableNames(SQLiteDatabase db) {
        ArrayList<String> tableList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("sqlite_master", new String[]{
                    "name"
            }, "type='table' and name like 'Attributions_1%'", null, null, null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    tableList.add(cursor.getString(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return tableList;
    }
}
