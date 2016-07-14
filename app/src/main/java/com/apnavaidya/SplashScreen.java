package com.apnavaidya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SplashScreen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedpreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        new RetrieveProblems().execute();
    }

    class RetrieveProblems extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                Intent intent = new Intent(getApplicationContext(), NetActivity.class);
                startActivity(intent);
                return;
            }
            if (response.equals("net")) {
                Intent intent = new Intent(getApplicationContext(), NetActivity.class);
                startActivity(intent);
                return;
            }
            JSONObject jsonObject = null;
            ArrayList<String> problemsList = new ArrayList<String>();
            try {
                jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray jsonArray2 = jsonObject.getJSONArray("problems");
                for (int i = 0; i < jsonArray2.length(); i++) {
                    problemsList.add((String) jsonArray2.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("INFO", response);
            Log.i("INFO", "came here");
            Intent intent;
            if (null != sharedpreferences.getString("authToken", null) && 0 != sharedpreferences.getInt("uid", 0)) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), LoginPage.class);
            }

            ProblemList problemListClass = new ProblemList();
            problemListClass.setProblemList(problemsList);
            startActivity(intent);
            finish();
        }

        @Override
        protected String doInBackground(Void... params) {
            // Do some validation here
            HttpURLConnection urlConnection = null;
            URL url = null;
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
                    url = new URL("http://52.35.152.91:8080//v1/problems");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("authtoken", "5713b9d6-dbc6-415e-b8d1-db3be2961793-jpradeep.93@gmail.com-1460293796095");
                    urlConnection.setRequestProperty("uid", "1");
                    urlConnection.connect();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Throwable e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }
    }


}

