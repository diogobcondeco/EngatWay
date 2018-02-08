package com.engatway;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.engatway.Volley.AppController;
import com.engatway.Volley.SessionManager;
import com.engatway.classes.Utilizador;
import com.engatway.db.BackgroundWorker;
import com.engatway.db.SQLiteHandler;
import com.engatway.helpers.AppConfig;
import com.engatway.helpers.MyClassAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CriarIntrigaActivity extends AppCompatActivity  {
    private static final String TAG = CriarIntrigaActivity.class.getSimpleName();
    // necessário ter id do user logado para enviar ao backgroundworker
    private SQLiteHandler db;
    private SessionManager session;
    String id_user;

    // Variables
    EditText sujeitoValue, alvoValue, descricaoValue;
    private ProgressDialog pDialog;
    // List<String> responseList = new ArrayList<String>();
    List<Utilizador> userList = new ArrayList<Utilizador>();

    // Spinner Variables
    Spinner spinnerValue;
    ArrayAdapter<CharSequence> adapter;

    // AutoComplete
    ArrayAdapter<Utilizador> adapter1;
    AutoCompleteTextView sujeito;
    AutoCompleteTextView alvo;

    String sujeitoId;
    String alvoId;

    private Button btnHome;
    private Button btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_intriga);

        initViews();
        MostrarNomes();

        Log.d("novoUser ", "newuser " + userList);

        //autocomplete
        // ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
        //         android.R.layout.simple_dropdown_item_1line, );


        // Creating Spinner
        spinnerValue = (Spinner)findViewById(R.id.spinnerNovaIntriga);
        adapter = ArrayAdapter.createFromResource(this, R.array.mensagens, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerValue.setAdapter(adapter);
        spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getBaseContext(),parent.getItemAtPosition(position) + " seleccionado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initAdapters() {
        adapter1 = new MyClassAdapter(this, android.R.layout.simple_dropdown_item_1line, userList);
        sujeito = (AutoCompleteTextView)
                findViewById(R.id.editTextCriarSujeito);
        sujeito.setAdapter(adapter1);
        sujeito.setThreshold(1);
        alvo = (AutoCompleteTextView)
                findViewById(R.id.editTextCriarAlvo);
        alvo.setAdapter(adapter1);
        alvo.setThreshold(1);

        sujeito.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof Utilizador){
                    Utilizador utilizador = (Utilizador) item;
                    sujeitoId = utilizador.getId();
                }
            }
        });

        alvo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof Utilizador){
                    Utilizador utilizador = (Utilizador) item;
                    alvoId = utilizador.getId();
                }
            }
        });
    }

    private void initViews() {
        sujeitoValue = findViewById(R.id.editTextCriarSujeito);
        alvoValue = findViewById(R.id.editTextCriarAlvo);
        descricaoValue = (EditText)findViewById(R.id.editTextDescricaoIntriga);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        id_user = user.get("id");

        //btn
        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CriarIntrigaActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CriarIntrigaActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }

    public void onCriarIntriga (View view) {
        String idSujeito = sujeitoId;
        String sujeito = sujeitoValue.getText().toString();
        String mensagem = spinnerValue.getSelectedItem().toString();
        String idAlvo = alvoId;
        String alvo = alvoValue.getText().toString();
        String descricao = descricaoValue.getText().toString();
        String id_userLoggedIn = id_user;
        String type = "novaIntriga";

//        if (sujeito.matches("") || alvo.matches("") || descricao.matches("")) {
//            Toast.makeText(getBaseContext(),"Necessário preencher todos os campos", Toast.LENGTH_LONG).show();
//        } else {
//            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
//            backgroundWorker.execute(type, idSujeito, sujeito, mensagem, idAlvo, alvo, descricao, id_userLoggedIn);
//        }

        if (idSujeito == null || idAlvo == null || descricao.matches("")) {
            Toast.makeText(getBaseContext(),"Necessário preencher todos os campos corretamente", Toast.LENGTH_LONG).show();
        } else {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, idSujeito, sujeito, mensagem, idAlvo, alvo, descricao, id_userLoggedIn);
        }
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}