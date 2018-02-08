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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Esta classe sqlite recebe os dados do servidor com todos os nomes registados e guarda em um tabela para
 * ser inserido em um array e usado pelo autocomplete
 */

public class SQLiteHandlerAuto extends SQLiteOpenHelper {

    //logCat
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // iniciar as variaveis
    // definir a versao da DB
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_db_amigo_intimo";

    // Login table name
    private static final String TABLE_AMIGO_INTIMO = "amigo_intimo";

    // Login Table Columns names

    private static final String KEY_IDD = "idd";
    private static final String KEY_ID = "id";
    private static final String KEY_AMIGO_INTIMO = "amigo_Intimo";


    public SQLiteHandlerAuto(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Criar tabelas
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_AMIGO_INTIMO + "("
                + KEY_IDD + " integer primary key autoincrement," +  KEY_ID + " integer," +KEY_AMIGO_INTIMO + " TEXT";

        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "BD criada");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // apaga a tabela antiga
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AMIGO_INTIMO);

        // cria outra vez
        onCreate(db);
    }

    /**
     * inserir os dados no sqlite
     * */
    public void addAmigoIntimo(String id, String amigoIntimo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // id
        values.put(KEY_AMIGO_INTIMO, amigoIntimo); // amigo intimo



        // inserir a linha
        db.insert(TABLE_AMIGO_INTIMO, null, values);
        db.close(); // fecha a connecção

        Log.d(TAG, "novo user inserido no sqlite: " + id);
    }

    /**
     * fazer get
     * */
    public HashMap<String, String> getAmigoIntimo() {
        HashMap<String, String> user = new HashMap<String, String>();

        String selectQuery = "SELECT  * FROM " + TABLE_AMIGO_INTIMO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // vai para a primeira possição
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(cursor.getColumnIndex("id")));
            user.put("amigo_Intimo", cursor.getString(cursor.getColumnIndex("amigo_Intimo")));

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
        db.delete(TABLE_AMIGO_INTIMO, null, null);
        db.close();

        Log.d(TAG, "Todos os dados da tabela foram deletados");
    }

}
