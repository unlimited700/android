package com.apnavaidya;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;

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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import co.hkm.soltag.ColorFactory;
import co.hkm.soltag.TagContainerLayout;
import co.hkm.soltag.TagView;


public class MainActivity extends Activity {

    public HashMap yogaHAshMap, foodHashMap, remedyHashmap;
    //  private ProgressBar progressBar;
    private AutoCompleteTextView editText;
    String boyPart = null;
    ArrayList<String> problemList = null;
    String[] body_parts = {"throat"};
    String yoga = null, step = null, days = null, soltype = null, duration = null;
    Set<String> bodyProblems = new TreeSet<>();
    Button solutionButton = null;
    List<Tag> problemtaglist = null;
    Button b;

    SharedPreferences sharedpreferences;
    int responsecode = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //sharedpreferences = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        //sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        sharedpreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);


        ProblemList problemListClass = new ProblemList();
        problemList = problemListClass.getProblemList();
        if (problemList != null)
            body_parts = problemList.toArray(new String[problemList.size()]);
        editText = (AutoCompleteTextView) findViewById(R.id.autoText);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, body_parts);
        editText.setAdapter(adapter);
        editText.setThreshold(3);
        //   problemList= (ArrayList)Arrays.asList(body_parts);
        ActiveTools();


    }


    public void logout(View v) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
        startActivity(intent);
        finish();

    }

    void ActiveTools() {
        final ImageButton button = (ImageButton) findViewById(R.id.button);
        solutionButton = (Button) findViewById(R.id.button3);
       final TagCloudLinkView tagCloudLinkView = (TagCloudLinkView) findViewById(R.id.cloudlinked);
      //  final TagContainerLayout mTagContainerLayout = (TagContainerLayout) findViewById(R.id.tagcontainerLayout);
    //    mTagContainerLayout.setTheme(ColorFactory.PURE_CYAN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText.getText() != null) {
                    boyPart = editText.getText().toString();
                } else {
                    return;
                }
                if (!problemList.contains(boyPart)) {
                    editText.setText("");
                    Toast.makeText(getApplicationContext(),
                            "sory,problem does not exist",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    editText.setText("");
                    bodyProblems.add(boyPart);
                    List<Tag> bodyList = tagCloudLinkView.getTags();
                    int length=tagCloudLinkView.getTags().size();
                    if (!bodyList.contains(boyPart)) {
                        tagCloudLinkView.add(new Tag(length+1,boyPart));

                    }
                }
            }
        });

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TagCloudLinkView tagCloudLinkView = (TagCloudLinkView) findViewById(R.id.cloudlinked);

                String selected = (String) parent.getItemAtPosition(position);
                if (selected != null) {
                    boyPart = selected;
                } else {
                    return;
                }
                if (!problemList.contains(boyPart)) {
                    editText.setText("");
                    Toast.makeText(getApplicationContext(),
                            "sory,problem does not exist",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    editText.setText("");
                    bodyProblems.add(boyPart);
                    List<Tag> bodyList = tagCloudLinkView.getTags();
                    int length=tagCloudLinkView.getTags().size();
                    if (!bodyList.contains(boyPart)) {
                        tagCloudLinkView.add(new Tag(length + 1, boyPart));
                        Log.i("tagSize: " , Integer.toString(tagCloudLinkView.getTags().size())) ;
                        tagCloudLinkView.drawTags();
                        Log.i("cloud","checking");

                    }
                }

            }
        });





        tagCloudLinkView.setOnTagDeleteListener(new TagCloudLinkView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(Tag tag, int i) {
            //   tagCloudLinkView.remove(tagCloudLinkView.getTags().indexOf(tag));
            }
        });

        solutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problemtaglist = tagCloudLinkView.getTags();
                if (problemtaglist == null || problemtaglist.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "no problem added",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //     progressBar.setVisibility(View.VISIBLE);
                new RetrieveFeedTask().execute();

            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {

        }

        protected void onPostExecute(String response) {
            yogaHAshMap = new HashMap();
            foodHashMap = new HashMap();
            remedyHashmap = new HashMap();
            if (response == null || responsecode != 200) {
                //   progressBar.setVisibility(View.GONE);
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


            JSONArray recommendedYogaJson = null, recommendedFoodJson = null, recommendedRemediesJson = null;
            try {
                jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String ng = null;
            try {
                recommendedYogaJson = jsonObject.getJSONArray("recommendedYoga");
                recommendedFoodJson = jsonObject.getJSONArray("recommendedFood");
                recommendedRemediesJson = jsonObject.getJSONArray("recommendedRemedies");
            } catch (Exception e) {
                Log.w("error", e);
                return;
            }
            try {
                if (recommendedFoodJson != null) {
                    for (int i = 0; i < recommendedFoodJson.length(); i++) {
                        List recommendedFood = new ArrayList();
                        yoga = recommendedFoodJson.getJSONObject(i).get("solution").toString();
                        duration = recommendedFoodJson.getJSONObject(i).get("duration").toString();
                        step = recommendedFoodJson.getJSONObject(i).get("step").toString();
                        recommendedFood.add(step);
                        days = recommendedFoodJson.getJSONObject(i).get("days").toString();
                        //recommendedFood.add("daily " + duration + " min for " + days + " days");
                        foodHashMap.put(yoga, recommendedFood);

                    }
                }
                if (recommendedRemediesJson != null) {
                    for (int i = 0; i < recommendedRemediesJson.length(); i++) {
                        List recommendedRemedies = new ArrayList();
                        yoga = recommendedRemediesJson.getJSONObject(i).get("solution").toString();
                        duration = recommendedRemediesJson.getJSONObject(i).get("duration").toString();
                        step = recommendedRemediesJson.getJSONObject(i).get("step").toString();
                        recommendedRemedies.add(step);
                        days = recommendedRemediesJson.getJSONObject(i).get("days").toString();
                       // recommendedRemedies.add("daily " + duration + " min for " + days + " days");
                        remedyHashmap.put(yoga, recommendedRemedies);

                    }
                }

                if (recommendedYogaJson != null) {
                    for (int i = 0; i < recommendedYogaJson.length(); i++) {
                        List recommendedYoga = new ArrayList();
                        yoga = recommendedYogaJson.getJSONObject(i).get("solution").toString();
                        soltype = recommendedYogaJson.getJSONObject(i).get("solType").toString();
                        duration = recommendedYogaJson.getJSONObject(i).get("duration").toString();
                        step = recommendedYogaJson.getJSONObject(i).get("step").toString();
                        recommendedYoga.add(step);
                        days = recommendedYogaJson.getJSONObject(i).get("days").toString();
                        recommendedYoga.add("daily " + duration + " min for " + days + " days");
                        yogaHAshMap.put(yoga, recommendedYoga);

                    }
                }
            } catch (Exception e) {
                Log.e("eror", "empty json array");
            }
            feedFragments();
            //     progressBar.setVisibility(View.GONE);
            //responseView.setText("");
            Log.i("INFO", response);
            Intent intent = new Intent(getApplicationContext(), Accordion.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        void feedFragments() {
            YogaFragment yogaFragment = new YogaFragment();
            FoodFragment foodFragment = new FoodFragment();
            RemediesFragment remediesFragment = new RemediesFragment();
            BodyFragment bodyFragment = new BodyFragment();
            DiseaseFragment diseaseFragment = new DiseaseFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("food", foodHashMap);
            bundle.putSerializable("yoga", yogaHAshMap);
            bundle.putSerializable("remedy", remedyHashmap);
            Log.i("info", "got bundle and insert");
            bodyFragment.setArgumentss(bundle);
            diseaseFragment.setArgumentss(bundle);
            yogaFragment.setArgumentss(bundle);
            remediesFragment.setArgumentss(bundle);
            foodFragment.setArgumentss(bundle);
            int count = 0;
            ProblemList.fragmentMap.clear();
            if (foodHashMap.size() != 0) {
                bundle.putSerializable("food", foodHashMap);
                ProblemList.fragmentMap.put(++count, "food");
            }
            if (yogaHAshMap.size() != 0) {
                ProblemList.fragmentMap.put(++count, "yoga");
            }
            if (remedyHashmap.size() != 0) {
                ProblemList.fragmentMap.put(++count, "remedy");
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
                    Log.i("hekkoioo", "ss");
                    url = new URL("http://52.35.152.91:8080/v1/recommend");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    Log.i("token", sharedpreferences.getString("authToken", null));
                    Log.i("userid", Integer.toString(sharedpreferences.getInt("uid", 0)));
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    if (null != sharedpreferences.getString("authToken", null) && 0 != sharedpreferences.getInt("uid", 0)) {
                        urlConnection.setRequestProperty("uid", Integer.toString(sharedpreferences.getInt("uid", 0)));
                        urlConnection.setRequestProperty("authtoken", sharedpreferences.getString("authToken", null));
                    }
                    //urlConnection.setRequestProperty("uid", Integer.toString(LoginPage.userid));
                    //urlConnection.setRequestProperty("authtoken", LoginPage.token);

                    urlConnection.connect();
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < problemtaglist.size(); i++) {
                        jsonArray.put(i, problemtaglist.get(i).getText());
                    }
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("problems", jsonArray);
                    BufferedWriter out =
                            new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                    out.write(jsonParam.toString());
                    out.close();
                    responsecode = urlConnection.getResponseCode();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    Log.i("recommmm response", stringBuilder.toString());
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
