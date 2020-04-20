package com.intel.libgdxmisslecommand.streammix;

import org.json.JSONObject;

public interface CallbackServer
{
    void executeAfterResponseServer(String response, int idServer);
    void exercuceAfterErrorServer(String error);
}
