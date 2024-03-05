package com.example.noram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

public class ShareHelper {
    public static boolean saveFileToCacheHidden(Bitmap bmp) {

        Uri uri = null;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        return false;
    }
}
