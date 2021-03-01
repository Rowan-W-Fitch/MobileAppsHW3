package com.example.hw3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();
    }


    public void setListeners(){
        Button b1 = findViewById(R.id.b1);
        Button b2 = findViewById(R.id.b2);
        Button b3 = findViewById(R.id.b3);

        b1.setText("people");
        b1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              int rand = randomNum(0, 670);
              int page = (int)Math.ceil(rand/20.0);
              charReq(page);
          }
        });
        b2.setText("locations");
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locFrag();
            }
        });
        b3.setText("episodes");
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epiFrag();
            }
        });
    }

    public void epiFrag(){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, EpisodeFragment.class, null)
                .commit();
    }

    public void locFrag(){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, LocationFragment.class, null)
                .commit();
    }

    public int randomNum(int min, int max){
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void charReq(int page){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://rickandmortyapi.com/api/character/" + (page > 0 ? "?page=" + Integer.toString(page) : "");
        JsonObjectRequest Req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray res = (JSONArray) response.get("results");
                            Log.d("res", res.toString());
                            int random = randomNum(0, res.length()-1);
                            Log.d("random", ""+random);
                            JSONObject obj = (JSONObject) res.get(random);
                            addCharFrag(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(Req);
    }

    public void addCharFrag(JSONObject j){
        Bundle bundle = new Bundle();
        try {
            bundle.putString("name", j.getString("name"));
            bundle.putString("status", j.getString("status"));
            bundle.putString("species", j.getString("species"));
            bundle.putString("gender", j.getString("gender"));
            JSONObject org = (JSONObject) j.get("origin");
            JSONObject loc = (JSONObject) j.get("location");
            bundle.putString("origin", org.getString("name"));
            bundle.putString("location", loc.getString("name"));
            bundle.putString("image", j.getString("image"));
            bundle.putString("episodes", j.get("episode").toString());
            CharacterFragment cf = new CharacterFragment();
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, CharacterFragment.class, bundle)
                    .commit();
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}