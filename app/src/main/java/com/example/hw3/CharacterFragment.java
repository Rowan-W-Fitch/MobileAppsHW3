package com.example.hw3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class CharacterFragment extends Fragment {

    String name;
    String status;
    String species;
    String gender;
    String origin;
    String location;
    String imageSrc;
    JSONArray episodes;

    public CharacterFragment(){
        //set data here
        super(R.layout.char_frag);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        name = requireArguments().getString("name");
        status = requireArguments().getString("status");
        species = requireArguments().getString("species");
        gender = requireArguments().getString("gender");
        origin = requireArguments().getString("origin");
        location = requireArguments().getString("location");
        imageSrc = requireArguments().getString("image");
        try {
            episodes = new JSONArray(requireArguments().getString("episodes"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        populate(imageSrc);
    }

    //populate the view w data
    public void populate(String url){
        TextView Name = getView().findViewById(R.id.name);
        Name.setText("name: " + name);
        TextView Status = getView().findViewById(R.id.status);
        Status.setText("status: " + status);
        TextView Species = getView().findViewById(R.id.species);
        Species.setText("species: " + species);
        TextView Gender = getView().findViewById(R.id.gender);
        Gender.setText("gender: " + gender);
        TextView Origin = getView().findViewById(R.id.origin);
        Origin.setText("from: " + origin);
        TextView Location = getView().findViewById(R.id.location);
        Location.setText("location: " + location);
        //set Img
        new DownloadImageTask(getView().findViewById(R.id.pic))
                .execute(url);
        //set episodes
        setEps(episodes);
    }

    public String addEP(JSONArray eps, int i, String ep, String extra){
        try {
            String url = eps.getString(i);
            String[] splt = url.split("/");
            int id = Integer.parseInt(splt[splt.length - 1]);
            int season = -1;
            int epi = -1;
            if (id <= 11) {
                season = 1;
                epi = id;
            } else if (id > 11 && id <= 21) {
                season = 2;
                epi = id - 11;
            } else if (id > 21 && id <= 31) {
                season = 3;
                epi = id - 21;
            } else {
                season = 4;
                epi = id - 31;
            }
            Log.d("season", Integer.toString(season));
            String verbose = "Season " + Integer.toString(season) + " Episode " + Integer.toString(epi);
            ep += verbose;
            if(extra != null) ep += extra;
        }catch(JSONException j){
            j.printStackTrace();
        }
        return ep;
    }

    public void setEps(JSONArray eps){
        String ep = "";
        for (int i = 0; i < eps.length()-1; i++) {
            ep = addEP(eps, i, ep, " - ");
        }
        ep = addEP(eps, eps.length()-1, ep, null);
        TextView e = getView().findViewById(R.id.episodes);
        e.setText("appears in: " + ep);
        e.setMovementMethod(new ScrollingMovementMethod());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
