package com.engatway;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.engatway.Volley.AppController;
import com.engatway.Volley.MySingleton;
import com.engatway.Volley.SessionManager;
import com.engatway.classes.Utilizador;
import com.engatway.db.SQLiteHandler;
import com.engatway.db.SQLiteHandlerAuto;
import com.engatway.helpers.AppConfig;
import com.engatway.helpers.MyClassAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {
    private static final String TAG = PerfilActivity.class.getSimpleName();

    //iniciar os componentes
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtGender;
    private TextView txtBirthday;
    private TextView txtReputacao;

    // AutoComplete Stuff
    AutoCompleteTextView acAmigoIntimo;
    ArrayAdapter<Utilizador> adapter2;
    String idAmigoIntimo;
    List<Utilizador> userList = new ArrayList<Utilizador>();

    // urls
    String url ="http://engatway.diogobcondeco.com/getRep.php";
    String url_atualizarPerfil = "http://engatway.diogobcondeco.com/updatePerfil.php";
    String url_irBuscarNomeAmigoIntimo = "http://engatway.diogobcondeco.com/irBuscarNomeAmigoIntimo.php";
    String url_irBuscarIdAmigoIntimo = "http://engatway.diogobcondeco.com/irBuscarIdAmigoIntimo.php";

    // alertDialog
    AlertDialog.Builder builder;

    String nomeAmigoIntimo;


    private Button btnLogout;
    private Button btnHome;
    private Button btnCriarIntriga;

    private Button btnSubmeterEdit;

    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;

    List<String> responseList = new ArrayList<String>();

    String id_user;

    String repUserGotten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        initViews();
        MostrarNomes();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // SqLite database
        db = new SQLiteHandler(getApplicationContext());

        // sessao
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // recebe os detalhes do sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        String gender = user.get("gender");
        String birthday = user.get("birthday");
        id_user = user.get("id");

        irBuscarIdAmigoIntimo();

        // Reputacao
        getReputacao();

        // mostrar os dados
        txtName.setText(name);
        txtEmail.setText(email);
        txtGender.setText(gender);
        txtBirthday.setText(birthday);


        //desabilitar os edittexts
//        disableEditText(txtName);
//        disableEditText(txtEmail);
//        disableEditText(txtGender);
//        disableEditText(txtBirthday);

        // fazer o logout
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnSubmeterEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!id_user.equals(idAmigoIntimo)) {
                    if(acAmigoIntimo.getText().toString().equals("")){
                        idAmigoIntimo = "0";
                    }
                    atualizarPerfil();
                } else {
                    Toast.makeText(PerfilActivity.this, "Não é possivel adicionar esse utilizador", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder = new AlertDialog.Builder(PerfilActivity.this);
    }

    private void atualizarPerfil() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_atualizarPerfil, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                builder.setTitle("Resposta do servidor");
                builder.setMessage(response);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PerfilActivity.this, "Erro no perfil...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", id_user);
                params.put("idAmigoIntimo", idAmigoIntimo);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(PerfilActivity.this).addToRequestQueue(stringRequest);

    }

    private void initViews() {
        txtName =  findViewById(R.id.txtName);
        txtEmail =  findViewById(R.id.txtEmail);
        txtGender =  findViewById(R.id.txtGender);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtReputacao = (TextView)findViewById(R.id.txtReputacao);
        acAmigoIntimo = (AutoCompleteTextView)findViewById(R.id.acAmigoIntimo);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnSubmeterEdit = (Button) findViewById(R.id.btnSubmeterEdit);

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        btnCriarIntriga = findViewById(R.id.btnIntriga);
        btnCriarIntriga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, CriarIntrigaActivity.class);
                startActivity(intent);
            }
        });




//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, responseList);
//        AutoCompleteTextView textView = (AutoCompleteTextView)
//                findViewById(R.id.autoCompleteTextView);
//        textView.setAdapter(adapter);
    }

    //desabilita os edit text
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setTextColor(Color.parseColor("#393939"));
        editText.setBackgroundColor(100);
    }

    private void logoutUser() {
        session.setLogin(false);

        //limpa o usar da db para que aja sempre e somente 1
        db.deleteUsers();

        // redireciona para o login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void getReputacao() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                repUserGotten = response;
                Log.d("hello", "resposta: " + response);
                Log.d("hello", "resposta2: " + repUserGotten);
                txtReputacao.setText(repUserGotten);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PerfilActivity.this, "Erro...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", id_user);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(PerfilActivity.this).addToRequestQueue(stringRequest);
    }

    private void initAdapters() {
        adapter2 = new MyClassAdapter(this, android.R.layout.simple_dropdown_item_1line, userList);
        acAmigoIntimo = (AutoCompleteTextView) findViewById(R.id.acAmigoIntimo);
        acAmigoIntimo.setAdapter(adapter2);
        acAmigoIntimo.setThreshold(1);

        acAmigoIntimo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof Utilizador){
                    Utilizador utilizador = (Utilizador) item;
                    idAmigoIntimo = utilizador.getId();
                }
            }
        });

        /*final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView()
                                .getHeight() - activityRootView.getHeight();
                        if (heightDiff > 500) { // if more than 100 pixels, its
                            // probably a keyboard...
                            if (acAmigoIntimo.isFocused()) {
                                //keyboard is shown
                            }
                        } else {
                            if (acAmigoIntimo.isFocused()) {
                                //Keyboard is hidden.
                            }
                        }
                    }
                });*/
    }

    private void MostrarNomes() {
        // tag para os logs e para cancelar o pedido no request
        String tag_string_req = "req_register";

//        pDialog.setMessage("Registering ...");
//        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NOMES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);
                    // Log.d("jsonresp", " resp " + response);

                    for (int i = 0; i < jObj.length(); i++) {
                        final JSONObject e = jObj.getJSONObject(i);
                        // Log.d("jsonresp", " e " + e);
                        String idUser = e.getString("id");
                        String nameUser = e.getString("name");
                        Utilizador novoUser = new Utilizador();
                        novoUser.setId(idUser);
                        novoUser.setName(nameUser);
                        userList.add(novoUser);
                    }
                    initAdapters();

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
        });


        // adiciona o pedido a fila do request
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void irBuscarIdAmigoIntimo(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_irBuscarIdAmigoIntimo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resposta ", "AI " + response);
                idAmigoIntimo = response;
                Log.d("id ", "AI " + idAmigoIntimo);
                irBuscarNomeAmigoIntimo();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PerfilActivity.this, "Erro a ir buscar id", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", id_user);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(PerfilActivity.this).addToRequestQueue(stringRequest);
    }

    private void irBuscarNomeAmigoIntimo(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_irBuscarNomeAmigoIntimo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resposta","nome: " + response);
                nomeAmigoIntimo = response;
                acAmigoIntimo.setText(nomeAmigoIntimo);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PerfilActivity.this, "Erro a ir buscar nome", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idAmigoIntimo", idAmigoIntimo);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(PerfilActivity.this).addToRequestQueue(stringRequest);
    }
}
