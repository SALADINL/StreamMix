package com.intel.libgdxmisslecommand.streammix;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RequestServeur {

    private static String URL = "http://192.168.42.228";
    public static String PORT_RECO = ":3211";
    public static String PORT_CMD = ":3012";
    private String URL_PORT = "";
    private Context context;

    public RequestServeur(Context context, String server) {
        this.context = context;
        URL_PORT = URL+server;
    }

    public void sendHttpsRequest(final HashMap<String, String> param) {
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("REPONSE =>> "+ response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*TODO*/
                System.out.println("REPONSE =>> "+ error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }
}
