package com.example.noram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareHelper extends FileProvider {
    public ShareHelper() {
        super(R.xml.file_paths);
    }

    public boolean shareHelper(Bitmap bmp, String tempFilename) {
        // Reference: https://stackoverflow.com/a/50924037, kjs566, "Sharing Bitmap via Android Intent", accessed March 4 2024
        File imagesFolder = new File(getContext().getCacheDir(), "share_images");
        File file = new File(imagesFolder, tempFilename);

        Uri cacheUri;
        try {
            FileOutputStream stream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            cacheUri = FileProvider.getUriForFile(getContext(), "com.mydomain.fileprovider", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, cacheUri);
        shareIntent.setType("image/*");
        getContext().startActivity(shareIntent);
        return false;
    }
}
