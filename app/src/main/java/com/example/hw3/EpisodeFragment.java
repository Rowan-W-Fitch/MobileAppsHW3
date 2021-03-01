package com.example.hw3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;

public class EpisodeFragment extends Fragment {

    public EpisodeFragment(){
        //set data here
        super(R.layout.epi_frag);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        int pg = randomNum(0, 41);
        pg = (int) Math.ceil(pg / 20.0);
        makeReq(pg, view);
    }

    public int randomNum(int min, int max){
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public JSONObject getObj(int i, JSONObject response){
        try {
            return response.getJSONArray("results").getJSONObject(i);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public void getNames(JSONArray chars){
        try {
            int j = Math.min(chars.length(), 3);
            for (int i = 0; i < j; i++) {
                fillNames(i, chars.getString(i));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void fillNames(int i, String url){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest Req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String name = response.getString("name");
                            TextView tv = getView().findViewById(i == 0 ? R.id.char1 : (i == 1 ? R.id.char2 : R.id.char3));
                            tv.setText(name);
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence seq = "channel";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", seq, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setButton(Button b, String name, String num){
        b.setText("More Info");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify1(name, num);
            }
        });
    }

    public void notify1(String name, String num){
        //create intent
        String url = "https://rickandmorty.fandom.com/wiki/"+name.replaceAll(" ", "_");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, i, 0);
        //create notification
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "CHANNEL_ID")
                .setSmallIcon(R.drawable.bell)
                .setContentTitle("More Info")
                .setContentText("Want to Know More?")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("To read more information about Episode " + num + ", please visit: "+url))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, builder.build());
    }

    public void fillInfo(JSONObject obj, View v){
        try {
            String ep = obj.getString("episode");
            String name = obj.getString("name");
            String airDate = obj.getString("air_date");
            //set text
            TextView num = v.findViewById(R.id.num);
            num.setText("Episode: " + ep);
            TextView Name = v.findViewById(R.id.name);
            Name.setText("Name: " + name);
            TextView date = v.findViewById(R.id.date);
            getNames(obj.getJSONArray("characters"));
            date.setText("Air Date: " + airDate);
            Button b = v.findViewById(R.id.button);
            //set button
            setButton(b, name, ep);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }


    public void makeReq(Integer i, View v){
        String url = "https://rickandmortyapi.com/api/episode" + (i == null ? "" : "?page="+i.toString());
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest Req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int total = response.getJSONArray("results").length();
                            int rand = randomNum(0, total-1);
                            JSONObject eps = getObj(rand, response);
                            fillInfo(eps, v);
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
