/*
This file is used to represent a QR code. It contains the encoded data, the associated event, and the type of the QR code.
Outstanding Issues:
- None
 */

package com.example.noram.model;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.noram.MainActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent a QR code
 * @maintainer Cole
 * @author Cole
 */
public class QRCode {
    private String hashId;
    private String encodedData;
    private String associatedEvent;
    private QRType qrCodeType;
    private Bitmap bitmap;

    // Constructors
    /**
     * Empty constructor, to create an object that will be populated from the database.
     */
    public QRCode() {}

    /**
     * Default Constructor for a QR code. Generates a unique id.
     * @param data event id string associated with the QR code
     * @param type type of the QR code.
     * @param eventId event id string associated with the QR code
     */
    public QRCode(String data, String eventId, QRType type) {
        hashId = HashHelper.hashSHA256(data);
        qrCodeType = type;
        associatedEvent = eventId;
        encodedData = data;
        updateBitmap();
    }

    /**
     * Get the hash Id for the QR code. This is used for the document ID in firebase.
     * @return hash id.
     */
    public String getHashId() {
        return hashId;
    }

    /**
     * Get the encoded data of the QR code
     * @return string of encoded data
     */
    public String getEncodedData() {
        return encodedData;
    }

    /**
     * Update the encoded data of the QR code
     * @param encodedData new encoded data
     */
    public void setEncodedData(String encodedData) {
        this.encodedData = encodedData;
        hashId = HashHelper.hashSHA256(encodedData);
        updateBitmap();
    }

    /**
     * Get the associated event id for the QR Code
     * @return string event id
     */
    public String getAssociatedEvent() {
        return associatedEvent;
    }

    /**
     * Set the associated event for the QR code
     * @param associatedEvent new event id
     */
    public void setAssociatedEvent(String associatedEvent) {
        this.associatedEvent = associatedEvent;
    }

    /**
     * Get the QR code type associated with this qr code
     * @return QRType for the code.
     */
    public QRType getQrCodeType() {
        return qrCodeType;
    }

    /**
     * Update the QR Code type for the qr code
     * @param qrCodeType new QR code type
     */
    public void setQrCodeType(QRType qrCodeType) {
        this.qrCodeType = qrCodeType;
    }

    /**
     * Returns the bitmap for the QR Code.
     * @return bitmap for the QR code.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Updates the QR code in the database.
     */
    public void updateDBQRCode() {
        Map<String, Object> data = new HashMap<>();
        data.put("hashID", hashId);
        data.put("encodedData", encodedData);
        data.put("event", associatedEvent);
        data.put("type", qrCodeType.toString());
        MainActivity.db.getQrRef().document(hashId).set(data);
    }

    /**
     * Updates the QRCode object with a hashmap, likely from the database
     * @param map map containing data for the QR code
     */
    public void updateWithMap(Map<String, Object> map) {
        this.encodedData = (String) map.get("encodedData");
        this.hashId = (String) map.get("hashID");
        this.associatedEvent = (String) map.get("event");
        String type = (String) map.get("type");
        this.qrCodeType = type.equals("SIGN_IN") ? QRType.SIGN_IN : QRType.PROMOTIONAL;
        updateBitmap();
    }

    /**
     * Updates the Bitmap to contain the encoded string.
     */
    public void updateBitmap() {
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix qrBitMatrix;
        try {
            // encode QR code and convert the encoded code to a bitmap
            qrBitMatrix = qrWriter.encode(this.encodedData, BarcodeFormat.QR_CODE, 400, 400);
            bitmap = Bitmap.createBitmap(qrBitMatrix.getWidth(), qrBitMatrix.getHeight(), Bitmap.Config.RGB_565);
            for (int x = 0; x < qrBitMatrix.getWidth(); x++) {
                for (int y = 0; y < qrBitMatrix.getHeight(); y++) {
                    bitmap.setPixel(x, y, qrBitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validate that a bitmap has a QR Code in it.
     * @param bmp bitmap to validate
     * @return a null result if a QR code couldn't be found, else, the result, containing the encoded data
     */
    public static Result checkImageForQRCode(Bitmap bmp) {
        // convert bitmap to a BinaryBitmap so QRCodeReader can use it
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        LuminanceSource luminanceSource = new RGBLuminanceSource(bmp.getWidth(), bmp.getHeight(), pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));

        // read the bitmap with a qr code reader
        Result result;
        QRCodeReader reader = new QRCodeReader();
        try {
            result = reader.decode(binaryBitmap);
        } catch (NotFoundException | ChecksumException | FormatException e) {
            // QR Code not found/decoded - assume it's not a valid image
            result = null;
        }
        return result;
    }
}