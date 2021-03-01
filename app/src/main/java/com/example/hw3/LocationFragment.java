package com.example.hw3;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationFragment extends Fragment {

    public LocationFragment(){
        //set data here
        super(R.layout.loc_frag);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://rickandmortyapi.com/api/location";
        RecyclerView RV = view.findViewById(R.id.recycler);
        JsonObjectRequest Req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            Log.d("res", results.toString());
                            Adapter a = new Adapter(results);
                            RV.setLayoutManager(new LinearLayoutManager(getView().getContext()));
                            RV.setAdapter(a);
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
        queue.add(Req);
    }
}
