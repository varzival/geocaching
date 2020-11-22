package com.vadim.geocachingapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiConnector {

    private RequestQueue queue;
    private String url;
    private Context ctx;

    public ApiConnector(Context ctx) {
        this.queue = Volley.newRequestQueue(ctx);
        this.url = ctx.getString(R.string.api_url);
        this.ctx = ctx;
    }

    public interface VolleyCallback{
        void onSuccess(String response);
        void onError(VolleyError error);
    }

    public void requestGame(final VolleyCallback callback, final String code) {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeoGame.class, new GamesDeserializer())
                .registerTypeAdapter(GeoGame.class, new GamesSerializer())
                .create();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"game/"+code,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        GeoGame game = gson.fromJson(response, GeoGame.class);
                        GameIO.writeGame(ctx, code, game);

                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
