package com.example.noram.model;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.noram.MainActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QRCode {
    private final String identifier;
    private String encodedData;
    private QRType qrCodeType;
    private Bitmap bitmap;

    // Constructors

    /**
     * Default Constructor for a QR code. Generates a unique id.
     * @param data data string associated with the QR code
     * @param type type of the QR code.
     */
    public QRCode(String data, QRType type) {
        identifier = UUID.randomUUID().toString();
        encodedData = data;
        qrCodeType = type;
        updateBitmap();
    }

    /**
     * Get the identifier for the QR code
     * @return string identifier for the code.
     */
    public String getIdentifier() {
        return identifier;
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
        updateBitmap();
        updateDBQRCode();
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
        updateDBQRCode();
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
        data.put("event", encodedData);
        data.put("type", qrCodeType.toString());
        MainActivity.db.getAttendeeRef().document(identifier).set(data);
    }

    /**
     * Updates the Bitmap to contain the encoded string.
     */
    private void updateBitmap() {
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix qrBitMatrix;
        try {
            // encode QR code and convert the encoded code to a bitmap
            qrBitMatrix = qrWriter.encode(this.encodedData, BarcodeFormat.QR_CODE, 200, 200);
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
}
