package com.apnavaidya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginPage extends Activity {
    private EditText username;
    private EditText password;
    private Button login;
    private TextView loginLockedTV;
    private TextView attemptsLeftTV;
    private TextView numberOfRemainingLoginAttemptsTV;
    int numberOfRemainingLoginAttempts = 3;
    JSONObject siginJsonObject = new JSONObject();
    int respponscode;


    public static String PREF_FILE_NAME;
    SharedPreferences sharedpreferences;
    static Integer userid;
    static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        sharedpreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        //sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        //sharedpreferences = getSharedPreferences("myPrefs", Context.MODE_WORLD_WRITEABLE);
        setupVariables();
    }

    public void authenticateLogin(View view) {
        new SignINTask().execute();

    }

    public void authenticateSignUp(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupVariables() {
        username = (EditText) findViewById(R.id.usernameET);
        password = (EditText) findViewById(R.id.passwordET);
        login = (Button) findViewById(R.id.loginBtn);
    }

    class SignINTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {

        }

        protected void onPostExecute(String response) {
            if (response == null || response.equals("net") || respponscode != 200) {
                Toast.makeText(getApplicationContext(), "wrong password or username", Toast.LENGTH_SHORT).show();
                username.getText().clear();
                password.getText().clear();
                Intent intent = new Intent(getApplicationContext(), NetActivity.class);
                startActivity(intent);
                finish();
            }
            try {
                /*SigInResponse sigInResponse = new ObjectMapper().readValue(response, SigInResponse.class);*/
                SharedPreferences.Editor editor = sharedpreferences.edit();
                JSONObject resjsonObject = (JSONObject) new JSONTokener(response).nextValue();
                if (((int) resjsonObject.get("responseCode")) != 200) {
                    Toast.makeText(getApplicationContext(), "wrong password or username", Toast.LENGTH_SHORT).show();
                    username.getText().clear();
                    password.getText().clear();
                    return;
                }
                Log.i("token beta", (String) resjsonObject.get("token"));
                Log.i("user beta", resjsonObject.get("userId").toString());
                editor.putString("authToken", (String) resjsonObject.get("token"));
                editor.putInt("uid", (Integer) resjsonObject.get("userId"));
                editor.commit();
                userid = (Integer) resjsonObject.get("userId");
                token = (String) resjsonObject.get("token");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // intent.putExtra("token",sigInResponse.getToken());
                // intent.putExtra("userid",sigInResponse.getUserId());
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("erroror", e.getMessage(), e);

            }
        }

        @Override
        protected String doInBackground(Void... params) {
            // Do some validation here
            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject jsonObject = null;
            InputStream inStream = null;
            DataOutputStream printout = null;
            OutputStream os = null;
            ConnectivityManager cm =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnected() && activeNetwork.isAvailable();
            if (!isConnected) {
                Log.i("net", "false");
                return "net";
            }
            Log.i("net", "true");

            int status;
            try {
                try {
                    url = new URL("http://52.35.152.91:8080/v1/login");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setRequestProperty("authtoken", "apnavadiya!@#");
                    urlConnection.connect();
                    SignINRequest signINRequest = new SignINRequest();
                    feeddata(signINRequest);
                    String signInrequest = new ObjectMapper().writeValueAsString(signINRequest);
                    BufferedWriter out =
                            new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                    out.write(siginJsonObject.toString());
                    out.close();
                    respponscode = urlConnection.getResponseCode();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    Log.i("sigin", stringBuilder.toString());
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }

            } catch (Throwable e) {

                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        void feeddata(SignINRequest signInRequest) {
            try {
                signInRequest.setUserName(username.getText().toString());
                signInRequest.setPassword(password.getText().toString());
                siginJsonObject.put("userName", username.getText().toString());
                siginJsonObject.put("password", password.getText().toString());
            } catch (Exception e) {
                Log.i("error", "unable to enter data");
            }

        }
    }


}