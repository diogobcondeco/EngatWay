package com.engatway;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.engatway.Volley.AppController;
import com.engatway.Volley.SessionManager;
import com.engatway.db.BackgroundWorker;
import com.engatway.db.SQLiteHandler;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.engatway.helpers.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class LoginActivity extends AppCompatActivity {


    //iniciar as views
    private NestedScrollView nestedScrollView;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private Button btnLogin;

    //remember me
    private CheckBox checkBoxR;
    //sharedPref
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    //duas variaveis que vao guardar os valores do utilizador
    private String saveEmail, savePass;

    //connect
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide(); //esconder a action bar
        initViews();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

//         verifica se esta logado
        if (session.isLoggedIn()) {
            // se ja estiver logado vai para a home
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void initViews() {

        nestedScrollView = findViewById(R.id.nestedScrollView);

        //EDITS
        textInputEditTextEmail = findViewById(R.id.txtEmail);
        textInputEditTextPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        //guardar os dados do login
        checkBoxR = findViewById(R.id.checkBoxR);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        //o defvalue padrao e false
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        //if para verificar se o savelogin esta marcado
        if (saveLogin == true) {
            //o padrao defValue e pasado em branco
            textInputEditTextEmail.setText(loginPreferences.getString("username", ""));
            textInputEditTextPassword.setText(loginPreferences.getString("password", ""));
            checkBoxR.setChecked(true);
        }

        //evento listener do login
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String user_email = textInputEditTextEmail.getText().toString().trim();
                String user_password = textInputEditTextPassword.getText().toString().trim();

                // guardar o login
                if(view == btnLogin){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textInputEditTextEmail.getWindowToken(), 0);

                    saveEmail = textInputEditTextEmail.getText().toString();
                    savePass = textInputEditTextPassword.getText().toString();
                }
                if (checkBoxR.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", saveEmail);
                    loginPrefsEditor.putString("password", savePass);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }

                // verificar os campos
                if (!user_email.isEmpty() && !user_password.isEmpty()) {
                    // verificar o login
                    checkLogin(user_email, user_password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Preencha todos os dados!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });
    }

    /**
     * verificar na base de dados
     * */
    private void checkLogin(final String user_email, final String user_password) {
        // Tag para cancelar o pedido
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Resposta do login: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // verifica de a algum erro no json
                    if (!error) {
                        // user logado
                        // entao cria uma sessao
                        session.setLogin(true);

                        // agora guardar os dados no SQLite
                        JSONObject user = jObj.getJSONObject("user");
                        String id = user.getString("id");
                        String email = user.getString("email");
                        String password = user.getString("password");
                        String name = user.getString("name");
                        String birthday = user.getString("birthday");
                        String gender = user.getString("gender");


//                        String id, String email, String password, String name, String birthday, String gender
                        // Inserting row in users table
                        db.addUser(id, email, password, name, birthday, gender);

                        // iniciar a home
                        Intent intent = new Intent(LoginActivity.this,
                                HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // se falhar
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_email", user_email);
                params.put("user_password", user_password);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void goToRegister(View  view){

        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }




}
