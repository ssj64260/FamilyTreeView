package com.cxb.familytree.utils;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * assets工具
 */

public class AssetsUtil {

    public static String getAssetsTxtByName(Context context, String name) {
        try {
            InputStream is = context.getAssets().open(name);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static InputStream getInputStream(Context context, String fileName) {
        try {
            return context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getObjectByName(Context context, String name, Type type) {
        try {
            InputStream is = context.getAssets().open(name + ".txt");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            Gson gson = new Gson();

            return gson.fromJson(new String(buffer, "UTF-8"), type);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
