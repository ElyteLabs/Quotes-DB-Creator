package com.elytelabs.quotescreator.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Helper {

    public static String getClipboardData(Context context) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        return item.getText().toString();

    }
}
