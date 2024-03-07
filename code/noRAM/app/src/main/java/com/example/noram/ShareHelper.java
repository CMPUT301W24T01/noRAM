package com.example.noram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Helper class that extends FileProvider to help with sharing images.
 * Typical usage would be as follows:
 * ShareHelper shareHelper = new ShareHelper();
 * Intent shareIntent = shareHelper.generateShareIntent(myBitmap, "filename", getApplicationContext());
 * startActivity(Intent.createChooser(shareIntent, null))
 * @maintainer Cole
 * @author Cole
 */
public class ShareHelper extends FileProvider {
    /**
     * Initialize a ShareHelper instance.
     */
    public ShareHelper() {
        super(R.xml.file_paths);
    }

    /**
     * Generates an intent that will share the passed in bitmap
     * @param bmp bitmap to share
     * @param tempFilename a temporary file to give the bitmap in cache storage
     * @param context the context that is generating the intent - typically pass with getApplicationContext()
     * @return an intent that will open a share UI that shares the bitmap.
     */
    public Intent generateShareIntent(Bitmap bmp, String tempFilename, Context context) {
        // Reference: https://stackoverflow.com/a/50924037, kjs566, "Sharing Bitmap via Android Intent", accessed March 4 2024
        // get images folder from cache, create file
        File imagesFolder = new File(context.getCacheDir(), "images");
        File file = new File(imagesFolder, tempFilename + ".png");
        Uri cacheUri;

        try {
            imagesFolder.mkdirs();
            // compress the bitmap into a file and save, then get the Uri from cache
            FileOutputStream stream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            cacheUri = FileProvider.getUriForFile(context, "com.example.noram.ShareHelper", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create the intent that shares the qr code
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, cacheUri);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setDataAndType(cacheUri, "image/*");
        return shareIntent;
    }
}
