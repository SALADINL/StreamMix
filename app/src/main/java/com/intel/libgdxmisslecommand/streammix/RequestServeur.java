package com.intel.libgdxmisslecommand.streammix;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RequestServeur {

    private static String URL = "http://192.168.42.194";
    public static String PORT_RECO = ":3211";
    public static String PORT_CMD = ":3212";
    private String URL_PORT = "";
    private Context context;
    private CallbackServer callbackServer;

    public RequestServeur(Context context, CallbackServer callbackServer) {
        this.context = context;
        this.callbackServer = callbackServer;
    }

    public void sendHttpsRequest(final HashMap<String, String> param, String server) {

        int idServer = -1;
        if(server.equals(":3211")) idServer = 0; else if (server.equals(":3212")) idServer = 1;
        URL_PORT = URL+server;

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        int finalIdServer = idServer;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PORT,
                response -> {
                    System.out.println("REPONSE =>> "+ response);

                    callbackServer.executeAfterResponseServer(response, finalIdServer);

                }, error -> {
                    callbackServer.exercuceAfterErrorServer("Je n'ai pas compris ce que vous voulez");
                    System.out.println("REPONSE =>> "+ error.toString());
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }
}
