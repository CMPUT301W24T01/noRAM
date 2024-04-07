/*
 * This class is used to generate a default profile picture for an attendee.
 */
package com.example.noram.model;

import android.graphics.Bitmap;
import com.example.noram.MainActivity;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.function.Consumer;

/**
 * A class to generate a default profile picture for an attendee
 */
public class ProfilePhotoGenerator {

    /**
     * A method to change the color of the default profile picture
     * @param src the source bitmap
     * @param color_1_replacement the color to replace the default color with
     * @return the new bitmap with the color replaced
     */
    private Bitmap changeColor(Bitmap src, int color_1_replacement) {
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];

        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        // create result bitmap output
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // color information
        int pixel;

        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                int index = y * width + x;
                pixel = pixels[index];
                // set new pixel color to output bitmap if it is white
                if(pixel == -1) {
                    // change color
                    pixels[index] = color_1_replacement;
                }
            }
        }

        // set pixel array to result
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    /**
     * A method to generate a default profile picture for the attendee. Should be called
     * when the attendee is created so that a photo can immediately be displayed.
     */
    public void generateDefaultProfilePhoto(Attendee attendee) {

        // Ensure we are using the default photo
        if (attendee.getDefaultProfilePhoto()) {

            // Generate a color based on the attendee's name
            String colorIdentifier = attendee.getIdentifier() + attendee.getFirstName();
            StringBuilder builder = new StringBuilder();
            for (char c : colorIdentifier.toCharArray()) {
                builder.append((int)c);
            }
            int numIdentifier = new BigInteger(builder.toString()).intValue();
            int R = (numIdentifier) % 256;
            int G = (numIdentifier * 10) % 256;
            int B = (numIdentifier * 100) % 256;
            int icingColor = R << 16 | G << 8 | B;

            // Download the default photo and change its color
            Consumer<Bitmap> downloadConsumer = bitmap -> {
                Bitmap finalBitmap = changeColor(bitmap, icingColor);

                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
                byte[] data = byteOutputStream.toByteArray();

                MainActivity.db.getStorage().getReference()
                        .child("profile_photos/" + attendee.getIdentifier() + "-default")
                        .putBytes(data);

            };

            // Download the default photo
            MainActivity.db.downloadPhoto("profile_photos/cupcakeCakeDefault.png", downloadConsumer);
        }
    }

    /**
     * A method to generate a default profile picture for the attendee. Should be called
     * when the attendee is created so that a photo can immediately be displayed.
     * In addition, this method will return the generated photo to the calling function.
     * @param callingFunction the function to return the photo to (e.g. a lambda that sets the photo in the UI)
     */
    public void generateAndReturnDefaultProfilePhoto(Attendee attendee, Consumer<Bitmap> callingFunction) {

        // Currently pretty TEMP function to get around the async nature of the download

        // Ensure we are using the default photo
        if (attendee.getDefaultProfilePhoto()) {

            String colorIdentifier = attendee.getIdentifier() + attendee.getFirstName();
            StringBuilder builder = new StringBuilder();
            for (char c : colorIdentifier.toCharArray()) {
                builder.append((int)c);
            }

            int numIdentifier = new BigInteger(builder.toString()).intValue();
            int R = (numIdentifier) % 256;
            int G = (numIdentifier * 10) % 256;
            int B = (numIdentifier * 100) % 256;
            int icingColor = R << 16 | G << 8 | B;

            Consumer<Bitmap> downloadConsumer = bitmap -> {
                Bitmap finalBitmap = changeColor(bitmap, icingColor);

                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
                byte[] data = byteOutputStream.toByteArray();

                MainActivity.db.getStorage().getReference()
                        .child("profile_photos/" + attendee.getIdentifier() + "-default")
                        .putBytes(data);
                callingFunction.accept(finalBitmap);
            };
            MainActivity.db.downloadPhoto("profile_photos/cupcakeCakeDefault.png", downloadConsumer);
        }
    }

}
