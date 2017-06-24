package com.cxb.familytree.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

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
}
