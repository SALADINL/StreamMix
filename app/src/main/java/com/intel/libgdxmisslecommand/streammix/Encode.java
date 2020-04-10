package com.intel.libgdxmisslecommand.streammix;

import android.util.Base64;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class Encode {

    // TODO A commenter

    public static String getEncode(File file) {
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String encoded = Base64.encodeToString(bytes,0);

        return encoded;
    }
    public static String getEncode(String s) {
        File file = new File(s);

        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encoded = Base64.encodeToString(bytes,0);

        return encoded;
    }
}
