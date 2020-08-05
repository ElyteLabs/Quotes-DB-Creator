package com.elytelabs.quotescreator.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Helper {

    public static String getClipboardData(Context context) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        return item.getText().toString();

    }

    public static void setAutoPasteEnabled(Context context, boolean isEnabled) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("auto_paste", isEnabled);
        editor.apply();
    }

    public static boolean isAutoPasteEnabled(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean("auto_paste", true);
    }
}
