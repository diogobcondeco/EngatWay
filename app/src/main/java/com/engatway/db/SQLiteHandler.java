package com.engatway.db;

/**
 * Created by 160173003 on 25/01/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    //logCat
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // iniciar as variaveis
    // definir a versao da DB
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "android_db_Interna";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NAME = "name";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_GENDER = "gender";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Criar tabelas
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " TEXT," + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT," + KEY_NAME + " TEXT," + KEY_BIRTHDAY + " TEXT,"
                + KEY_GENDER + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "BD criada");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // apaga a tabela antiga
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // cria outra vez
        onCreate(db);
    }

    /**
     * inserir os dados no sqlite
     * */
    public void addUser(String id, String email, String password, String name, String birthday, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // id
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PASSWORD, password); // pass
        values.put(KEY_NAME, name); // Name
        values.put(KEY_BIRTHDAY, birthday); // birthday
        values.put(KEY_GENDER, gender); // gender


        // inserir a linha
        db.insert(TABLE_USER, null, values);
        db.close(); // fecha a connecção

        Log.d(TAG, "novo user inserido no sqlite: " + id);
    }

    /**
     * fazer get
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // vai para a primeira possição
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(cursor.getColumnIndex("id")));
            user.put("email", cursor.getString(cursor.getColumnIndex("email")));
            user.put("password", cursor.getString(cursor.getColumnIndex("password")));
            user.put("name", cursor.getString(cursor.getColumnIndex("name")));
            user.put("birthday", cursor.getString(cursor.getColumnIndex("birthday")));
            user.put("gender", cursor.getString(cursor.getColumnIndex("gender")));
        }
        cursor.close();
        db.close();
        // retorna o user
        Log.d(TAG, "recebeu os dados Sqlite: " + user.toString());

        return user;
    }
    /**
     * resetar a tabela
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Todos os dados da tabela foram deletados");
    }

}
