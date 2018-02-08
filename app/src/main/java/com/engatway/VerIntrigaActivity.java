package com.engatway;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.engatway.Volley.MySingleton;
import com.engatway.Volley.SessionManager;
import com.engatway.db.SQLiteHandler;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class VerIntrigaActivity extends AppCompatActivity {

    TextView subject, message, target, description;
    Button btn_concordo;
    String intriga_id;
    // int id_intriga;
    String idUserCriou;
    String idUserSelecionado;

    //ids sujeito e alvo
    String idDoSujeito;
    String idDoAlvo;

    String idUserCriouAIntriga;

    //ids dos amigos intimos
    String idDoAmigoIntimoDoSujeito;
    String idDoAmigoIntimoDoAlvo;

    // urls
    String url = "http://engatway.diogobcondeco.com/save.php";
    String url_getValorConcordo = "http://engatway.diogobcondeco.com/getValorConcordo.php";
    String url_getIdUserCriou = "http://engatway.diogobcondeco.com/getIdUserCriou.php";
    String url_selectIdUserCriou = "http://engatway.diogobcondeco.com/selectIdUserCriou.php";
    String url_setReputacaoPlusTen = "http://engatway.diogobcondeco.com/setReputacaoPlusTen.php";
    String url_setReputacaoPlusTwenty = "http://engatway.diogobcondeco.com/setReputacaoPlusTwenty.php";
    String url_setReputacaoPlusThirty = "http://engatway.diogobcondeco.com/setReputacaoPlusThirty.php";
    String url_addClickToDB = "http://engatway.diogobcondeco.com/addClickToDB.php";
    String url_checkIfClicked = "http://engatway.diogobcondeco.com/checkIfClicked.php";
    String url_getIdAmigoIntimoSujeito = "http://engatway.diogobcondeco.com/getIdAmigoIntimoSujeito.php";
    String url_getIdAmigoIntimoAlvo = "http://engatway.diogobcondeco.com/getIdAmigoIntimoAlvo.php";
    String url_irBuscarIdUserCriouIntriga = "http://engatway.diogobcondeco.com/irBuscarIdUserCriouIntriga.php";

    // alertDialog
    AlertDialog.Builder builder;

    // ir buscar user logged in
    private SQLiteHandler db;
    private SessionManager session;

    String id_user;

    //btns
    private Button btnHome;
    private Button btnCriarIntriga;
    private Button btnPerfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_intriga);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        id_user = user.get("id");

        Intent i = getIntent();

        // i.putExtra("id", intriga.getId_intriga());
        intriga_id = i.getStringExtra("id");
        Log.d("String", "intriga_id: " + intriga_id);
        // id_intriga = Integer.parseInt(intriga_id);
        // Log.d("int", "id_intriga: " + id_intriga);

        // definicao dos ids do sujeito e do alvo
        idDoSujeito = i.getStringExtra("id_sujeito");
        idDoAlvo = i.getStringExtra("id_alvo");

        subject = (TextView)findViewById(R.id.textViewSubject);
        subject.setText(i.getStringExtra("sujeito"));

        message = (TextView)findViewById(R.id.textViewMessage);
        message.setText(i.getStringExtra("mensagem"));

        target = (TextView)findViewById(R.id.textViewTarget);
        target.setText(i.getStringExtra("alvo"));

        description = (TextView)findViewById(R.id.textViewDescription);
        description.setText(i.getStringExtra("descricao"));

        getIdAmigoIntimoSujeito();
        getIdAmigoIntimoAlvo();
        irBuscarIdUserCriouIntriga(intriga_id);

        btn_concordo = (Button)findViewById(R.id.btn_concordo);
        btn_concordo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfClicked();
            }
        });

        builder = new AlertDialog.Builder(VerIntrigaActivity.this);

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerIntrigaActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        btnCriarIntriga = findViewById(R.id.btnIntriga);
        btnCriarIntriga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerIntrigaActivity.this, CriarIntrigaActivity.class);
                startActivity(intent);
            }
        });
        btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerIntrigaActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

    }

    //Fazer
    private void getIdAmigoIntimoSujeito () {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getIdAmigoIntimoSujeito, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //receber id do amigo  intimo do sujeito
                idDoAmigoIntimoDoSujeito = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no concordo...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // receber idSujeito
                params.put("id_sujeito", idDoSujeito);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    }

    private void getIdAmigoIntimoAlvo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getIdAmigoIntimoAlvo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //receber id do amigo  intimo do alvo
                idDoAmigoIntimoDoAlvo = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no concordo...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // receber idAlvo
                params.put("id_alvo", idDoAlvo);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    }

    private void irBuscarIdUserCriouIntriga (final String resposta){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_irBuscarIdUserCriouIntriga, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.d("problema - ", "3: " + response);
                idUserCriouAIntriga = response;
                // Log.d("problema - ", "4: " + idUserCriou);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro a ir buscar...", Toast.LENGTH_SHORT).show();
                // Log.d("problema - ", "errorTop: " + error);
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // params.put("idUserCriou", resposta);
                params.put("intriga_id", resposta);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    }

    // Logica
    private void checkIfClicked(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_checkIfClicked, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.d("problemaHUMM - ", "10: " + response);
                // se der 1
                if (response.equals("1")){
                    Toast.makeText(VerIntrigaActivity.this, "Já concordou", Toast.LENGTH_LONG).show();
                } else { //se nao der 1 vem para aqui
                    addClickToBD();
                    concordo();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro a dar check...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", id_user);
                params.put("intriga_id", intriga_id);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    }

    private void addClickToBD() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_addClickToDB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("true")){
                    Log.d("click ", "adicionado a bd!");
                } else {
                    Log.d("click ", "falhado em add a bd!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro a adicionar...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", id_user);
                params.put("intriga_id", intriga_id);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    }

    private void concordo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                builder.setTitle("Resposta do servidor");
                builder.setMessage(response);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                getValorConcordo(intriga_id);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no concordo...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("intriga_id", intriga_id);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    }

    public void getValorConcordo(final String resposta){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getValorConcordo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int valor_concordo = Integer.parseInt(response);
                // Log.d("problema - ", "1: " + valor_concordo);

                // fazer logica aqui
                /*if (idDoAmigoIntimoDoSujeito == idUserCriouAIntriga && idDoAmigoIntimoDoAlvo == idUserCriouAIntriga){
                    if (valor_concordo == 1){
                        getIdUserCriou(intriga_id);
                    }
                } else if (idDoAmigoIntimoDoSujeito == idUserCriouAIntriga || idDoAmigoIntimoDoAlvo == idUserCriouAIntriga){
                    if (valor_concordo == 2){
                        getIdUserCriou(intriga_id);
                    }
                } else {
                    if (valor_concordo == 3){
                        getIdUserCriou(intriga_id);
                    }
                }
*/





                if (valor_concordo == 3){
                    getIdUserCriou(intriga_id);
                    // Log.d("problemaAmazing - ", "2: " + resposta);
                    // Log.d("problemaAmazing - ", "3: " + valor_concordo);
                }
                // Log.d("problema - ", "4: " + resposta);
                // Log.d("problema - ", "5: " + valor_concordo);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no getValor...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("intriga_id", resposta);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    };

    public void getIdUserCriou(final String resposta){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getIdUserCriou, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.d("problema - ", "3: " + response);
                idUserCriou = response;
                // Log.d("problema - ", "4: " + idUserCriou);
                selectIdUserCriou(idUserCriou);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no getIdUserCriou...", Toast.LENGTH_SHORT).show();
                // Log.d("problema - ", "errorTop: " + error);
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // params.put("idUserCriou", idUserCriou);
                params.put("intriga_id", resposta);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    };

    public void selectIdUserCriou(final String resposta){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_selectIdUserCriou, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                idUserSelecionado = response;
                // Log.d("problema - ", "5: " + idUserSelecionado);
                // setReputacaoPlusTen(idUserSelecionado);
                String idDoAISujeito = idDoAmigoIntimoDoSujeito;
                String idDoAIAlvo = idDoAmigoIntimoDoAlvo;

                // fazer logica aqui
                if (idDoAISujeito.equals(idUserSelecionado) && idDoAIAlvo.equals(idUserSelecionado)){
                    setReputacaoPlusThirty(idUserSelecionado);
                } else if (idDoAISujeito.equals(idUserSelecionado) || idDoAIAlvo.equals(idUserSelecionado)){
                    setReputacaoPlusTwenty(idUserSelecionado);
                } else {
                    setReputacaoPlusTen(idUserSelecionado);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no selectId...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUserCriou", resposta);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    };

    public void setReputacaoPlusThirty(final String resposta){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_setReputacaoPlusThirty, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                builder.setTitle("Resposta do servidor");
                builder.setMessage(response);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                // Log.d("problema - ", "6: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no setRep...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUserSelecionado", resposta);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    };

    public void setReputacaoPlusTwenty(final String resposta){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_setReputacaoPlusTwenty, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                builder.setTitle("Resposta do servidor");
                builder.setMessage(response);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                // Log.d("problema - ", "6: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no setRep...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUserSelecionado", resposta);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    };

    public void setReputacaoPlusTen(final String resposta){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_setReputacaoPlusTen, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                builder.setTitle("Resposta do servidor");
                builder.setMessage(response);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                // Log.d("problema - ", "6: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerIntrigaActivity.this, "Erro no setRep...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            // Define qual o campo a passar
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUserSelecionado", resposta);
                return params;
            }
        };

        // mysingleton here
        MySingleton.getInstance(VerIntrigaActivity.this).addToRequestQueue(stringRequest);
    };

}