package com.example.hw3;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    JSONArray data;

    public Adapter(JSONArray j){
        data = j;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rec_view, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder vh, final int position){
        try{
            //get info
            JSONObject obj = (JSONObject) data.get(position);
            String name = (String) obj.get("name");
            String type = (String) obj.get("type");
            String dimension = (String) obj.get("dimension");
            //set data
            vh.name.setText("Name: " + name);
            vh.type.setText("Type: " + type);
            vh.dimension.setText("Dimension: " + dimension);
        }
        catch(JSONException e){
            Log.e("error", e.toString());
        }

    }


    @Override
    public int getItemCount() {
        return data.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView type;
        TextView dimension;


        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            type = view.findViewById(R.id.type);
            dimension = view.findViewById(R.id.dim);
        }


    }

}
