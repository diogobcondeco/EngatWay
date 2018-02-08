package com.engatway;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.engatway.Volley.AppController;
import com.engatway.Volley.SessionManager;
import com.engatway.db.BackgroundWorker;
import com.engatway.db.SQLiteHandler;
import com.engatway.helpers.AppConfig;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private final AppCompatActivity activity = RegisterActivity.this;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    //iniciar as os objs
    private NestedScrollView nestedScrollView;

    //edits
    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;

    private Button btnRegister;

    // radio button
    private RadioGroup txtRadioGroup;
    private RadioButton txtRadioButton;
    private String genderEscolhido;

    //data
    private EditText txtDataNasc;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //iniciar os componentes
        initViews();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

//        // verifica se a sessao ja estiver iniciada ele passa logo para a home
        if (session.isLoggedIn()) {
            // se ja estiver logado
            Intent intent = new Intent(RegisterActivity.this,
                    HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        //edits
        textInputEditTextName =  findViewById(R.id.textInputEditTextName);
        textInputEditTextEmail =  findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword =  findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword =  findViewById(R.id.textInputEditTextConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        //radio group
        txtRadioGroup = (RadioGroup) findViewById(R.id.genero);

        //data
        txtDataNasc = findViewById(R.id.txtMostrarData);
        txtDataNasc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cria uma nova instancia do tipo calendar
                final Calendar myCalendar = Calendar.getInstance();
                //add o evento listener do objeto data
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd MM yyyy"; // formato da data
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                        //muda o txt do campo com a data escolhida
                        txtDataNasc.setText(sdf.format(myCalendar.getTime()));

                    }
                };
                new DatePickerDialog(RegisterActivity.this , date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String user_email = textInputEditTextEmail.getText().toString().trim();
                String user_password = textInputEditTextPassword.getText().toString().trim();
                String user_name = textInputEditTextName.getText().toString().trim();
                String user_birthday = txtDataNasc.getText().toString();
                String user_gender = genderEscolhido;


                if (!user_email.isEmpty() && !user_password.isEmpty() && !user_name.isEmpty() && !user_birthday.isEmpty() && !user_gender.isEmpty()) {
                    registerUser(user_email, user_password, user_name, user_birthday, user_gender);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });




    }
    private void registerUser(final String user_email, final String user_password,
                              final String user_name, final String user_birthday, final String user_gender) {
        // tag para os logs e para cancelar o pedido no request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // guarda os dados no oj user para o SQLite
                        JSONObject user = jObj.getJSONObject("user");
                        String id = user.getString("id");
                        String email = user.getString("email");
                        String password = user.getString("password");
                        String name = user.getString("name");
                        String birthday = user.getString("birthday");
                        String gender = user.getString("gender");

                        // insere os dados no sqlite
                        db.addUser(id, email, password, name, birthday, gender);

                        Toast.makeText(getApplicationContext(), "Conta criada com sucesso. Faça login agora!", Toast.LENGTH_LONG).show();

                        // redireciona para o login
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // trata se ocorrer algum erro
                        // mensagem
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // passa os parametros para o url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_email", user_email);
                params.put("user_password", user_password);
                params.put("user_name", user_name);
                params.put("user_birthday", user_birthday);
                params.put("user_gender", user_gender);



                return params;
            }

        };

        // adiciona o pedido a fila do request
       AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void goToLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    //escrever o valor do radio button em uma string
    public String rbClick (View v){

        //receber o id
        int radioButtonId = txtRadioGroup.getCheckedRadioButtonId();

        txtRadioButton = (RadioButton) findViewById(radioButtonId);

         genderEscolhido = txtRadioButton.getText().toString();
//        Toast.makeText(activity, ""+genderEscolhido, Toast.LENGTH_SHORT).show();

        return genderEscolhido;
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

