package com.engatway.db;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.engatway.HomeActivity;
import com.engatway.LoginActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class BackgroundWorker extends AsyncTask<String, Void, String> {
    Context context;
    AlertDialog alertDialog;

    public BackgroundWorker(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... voids) {
        String type = voids[0];
        String login_url = "http://engatway.diogobcondeco.com/login.php";
        String register_url = "http://engatway.diogobcondeco.com/register.php";
        String novaIntriga_url = "http://engatway.diogobcondeco.com/criarIntriga.php";
        if(type.equals("login")){
            try {
                String user_email = voids[1];
                String user_password = voids[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_email", "UTF-8")+"="+URLEncoder.encode(user_email,"UTF-8")+"&"
                        +URLEncoder.encode("user_password", "UTF-8")+"="+URLEncoder.encode(user_password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                };
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(type.equals("register")) {
            try {
                String user_email = voids[1];
                String user_password = voids[2];
                String user_name = voids[3];
                String user_age = voids[4];
                String user_gender = voids[5];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_email", "UTF-8")+"="+URLEncoder.encode(user_email,"UTF-8")+"&"
                        +URLEncoder.encode("user_password", "UTF-8")+"="+URLEncoder.encode(user_password,"UTF-8")+"&"
                        +URLEncoder.encode("user_name", "UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                        +URLEncoder.encode("user_birthday", "UTF-8")+"="+URLEncoder.encode(user_age,"UTF-8")+"&"
                        +URLEncoder.encode("user_gender", "UTF-8")+"="+URLEncoder.encode(user_gender,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                };
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("novaIntriga")) {
            try {
                String user_id_sujeito = voids[1];
                String user_sujeito = voids[2];
                String user_mensagem = voids[3];
                String user_id_alvo = voids[4];
                String user_alvo = voids[5];
                String user_descricao = voids[6];
                String user_id_userLoggedIn = voids[7];
                URL url = new URL(novaIntriga_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_id_sujeito", "UTF-8")+"="+URLEncoder.encode(user_id_sujeito,"UTF-8")+"&"
                        +URLEncoder.encode("user_sujeito", "UTF-8")+"="+URLEncoder.encode(user_sujeito,"UTF-8")+"&"
                        +URLEncoder.encode("user_mensagem", "UTF-8")+"="+URLEncoder.encode(user_mensagem,"UTF-8")+"&"
                        +URLEncoder.encode("user_id_alvo", "UTF-8")+"="+URLEncoder.encode(user_id_alvo,"UTF-8")+"&"
                        +URLEncoder.encode("user_alvo", "UTF-8")+"="+URLEncoder.encode(user_alvo,"UTF-8")+"&"
                        +URLEncoder.encode("user_descricao", "UTF-8")+"="+URLEncoder.encode(user_descricao,"UTF-8")+"&"
                        +URLEncoder.encode("user_id_userLoggedIn", "UTF-8")+"="+URLEncoder.encode(user_id_userLoggedIn,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String intrigaCriada = "";
                String line;
                while((line = bufferedReader.readLine())!= null) {
                    intrigaCriada += line;
                };
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return intrigaCriada;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {

        if (result != null) {
            if (result.contains("true")) {
                context.startActivity(new Intent(context, HomeActivity.class));
            } else if(result.contains("false")) {
                alertDialog.setMessage("Dados inválidos");
                alertDialog.show();
            }
        } else {
            alertDialog.setMessage(result);
            alertDialog.show();

        }
    }




    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}