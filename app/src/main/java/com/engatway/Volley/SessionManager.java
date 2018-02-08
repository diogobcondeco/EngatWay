package com.engatway.Volley;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaCas;
import android.util.Log;

/**
 * Created by 160173003 on 25/01/2018.
 */
/**
 * Esta classe armazena os dados da sessao com sharedpreferences
 */
public class SessionManager {
    //logCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    //Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    //SharedPref em modo privado
    int PRIVATE_MODE = 0;

    //nome do sharedpreference
    private static final String PREF_NAME = "EngatWayLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    //Construtor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //marcar a sessao iniciada
    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit oara guardar
        editor.commit();

        //log para teste
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
