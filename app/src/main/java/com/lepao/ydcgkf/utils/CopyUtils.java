package com.lepao.ydcgkf.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class CopyUtils {
    public static void copy(String content, Context context)
    {
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("keyword", content);
        if(cmb != null) {
            cmb.setPrimaryClip(clipData);
            ToastUtils.showShort(context,"已复制");
        }
    }
}
