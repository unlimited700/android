package com.apnavaidya;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

/**
 * Created by neeraj on 26/2/16.
 */
public class apiDebugginng extends Activity {

    private Exception exception;
    private ProgressBar progressBar;
    private TextView responseView;
    private HashMap<String, List<String>> expandableListDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.api_debug);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        responseView=(TextView)findViewById(R.id.textView22);
        new RetrieveFeedTask().execute();
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {



        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("neeraj");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here
            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject jsonObject = null;
            InputStream inStream = null;
            DataOutputStream printout = null;
            OutputStream os=null;
            int status;
            try {
                try {
                    url = new URL("http://171.49.126.80:8080/v1/recommend");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.connect();
                    String arraystring = "throat";
                   // String arraystring[] = {};
                    //arraystring.add("throat") ;

                    JSONArray jsonArray=new JSONArray();
                    jsonArray.put(0,"throat");

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("problems",jsonArray);
                   // printout = new DataOutputStream(urlConnection.getOutputStream());
                    //printout.write(jsonParam.toString().getBytes("UTF-8"));
                    ///printout.flush();
                    //os=urlConnection.getOutputStream();
                    //os.write(jsonParam.toString().getBytes("UTF-8"));
                    //os.close();
                    BufferedWriter out =
                            new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                    out.write(jsonParam.toString());
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
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }


        }

        protected void onPostExecute(String response) {
            JSONObject jsonObject=null;
            List recommendedYoga = new ArrayList();
            List recommendedFood = new ArrayList();
            List recommendedRemedies = new ArrayList();
            JSONArray recommendedYogaJson=null,recommendedFoodJson=null,recommendedRemediesJson=null;
            try {
                 jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String ng = null;
            try {
                recommendedYogaJson = jsonObject.getJSONArray("recommendedYoga");
                recommendedFoodJson = jsonObject.getJSONArray("recommendedFood");
                recommendedRemediesJson=jsonObject.getJSONArray("recommendedRemedies");
            } catch (Exception e) {
                ng = "bhai nhi  mila";
                Log.w("why", e);
            }
            try {
                recommendedFood.add(recommendedFoodJson.optJSONObject(0).get("solution").toString());
                recommendedRemedies.add(recommendedRemediesJson.optJSONObject(0).get("solution").toString());
                recommendedYoga.add(recommendedYogaJson.getJSONObject(0).get("solution").toString());

            }
            catch (Exception e)
            {

            }

            expandableListDetail.put("recommendedFood",recommendedFood);
            expandableListDetail.put("recommendedRemedies",recommendedRemedies);
            expandableListDetail.put("recommendedYoga",recommendedYoga);

            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);
        }
    }

}
