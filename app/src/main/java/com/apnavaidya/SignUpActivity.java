package com.apnavaidya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SignUpActivity extends Activity {
    private EditText username;
    private EditText password;
    private EditText confirmPas;
    private EditText email;
    private EditText phone;
    private EditText age;
    private Button signup;
    private RadioGroup genderRadiogroup;
    private RadioButton radioSexButton;
    private String genderString;
    JSONObject jsonObject1 = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setupVariables();
    }

    public void authenticateSignupinsign(View view) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

      /*  if (password.length()==0||!password.getText().toString().equals(confirmPas.getText().toString())) {
            Toast.makeText(getApplicationContext(), "confirm password do not match", Toast.LENGTH_SHORT);
            return;
        }
        if (email.length()==0||!email.getText().toString().trim().matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "enter valid email", Toast.LENGTH_SHORT);
            return;
        }*/

        int selectedId = genderRadiogroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) findViewById(selectedId);
        genderString = (String) radioSexButton.getText();
        new SignUpTask().execute();
    }

    public void signin(View view)
    {
        Intent intent=new Intent(getApplicationContext(),LoginPage.class);
        startActivity(intent);
        finish();
    }


    private void setupVariables() {
        username = (EditText) findViewById(R.id.usernameET);
        phone = (EditText) findViewById(R.id.phonET);
        age = (EditText) findViewById(R.id.ageET);
        password = (EditText) findViewById(R.id.passwordET);
        confirmPas = (EditText) findViewById(R.id.confirmpasswordET);
        email = (EditText) findViewById(R.id.emailET);
        genderRadiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        signup = (Button) findViewById(R.id.button4);
    }

    class SignUpTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            // progressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(String response) {
            if (response == null || response.equals("net")) {
                //   progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), NetActivity.class);
                startActivity(intent);
                return;
            }
            try {
                JSONObject resjsonObject = (JSONObject) new JSONTokener(response).nextValue();
                if (((int) resjsonObject.get("responseCode")) != 200) {
                    Toast.makeText(getApplicationContext(), "user already exist,try with other email", Toast.LENGTH_SHORT).show();
                    username.getText().clear();
                    email.findFocus();
                    password.getText().clear();
                    return;
                }
            } catch (Exception e) {
            }

            Intent intent = new Intent(getApplicationContext(), LoginPage.class);

            startActivity(intent);
            Toast.makeText(getApplicationContext(), "signUp complete", Toast.LENGTH_SHORT);
            finish();
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
                    url = new URL("http://52.35.152.91:8080/v1/signup");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);


                    //   urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setRequestProperty("authtoken", "apnavadiya!@#");
                    urlConnection.connect();

                    SignUpRequest signUpRequest = new SignUpRequest();
                    feeddata(signUpRequest);
                    String signUpString = new ObjectMapper().writeValueAsString(signUpRequest);
                    //  jsonObject1.put(signUpString,true);
                    BufferedWriter out =
                            new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));

                    //  OutputStream out = urlConnection.getOutputStream();
                    out.write(jsonObject1.toString());
                    out.close();
                    status = urlConnection.getResponseCode();
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

        void feeddata(SignUpRequest signUpRequest) {
            try {

                signUpRequest.setAge(Integer.parseInt(age.getText().toString()));
                signUpRequest.setEmail(email.getText().toString());
                signUpRequest.setName(username.getText().toString());
                signUpRequest.setPassword(password.getText().toString());
                signUpRequest.setPhone(phone.getText().toString());
                signUpRequest.setSex(genderString);

                jsonObject1.put("age", Integer.parseInt(age.getText().toString()));
                jsonObject1.put("email", email.getText().toString());
                jsonObject1.put("name", username.getText().toString());
                jsonObject1.put("password", password.getText().toString());
                jsonObject1.put("phone", phone.getText().toString());
                jsonObject1.put("sex", genderString);


            } catch (Exception e) {
                Log.i("error", "unable to enter data");
            }

        }

    }
}


