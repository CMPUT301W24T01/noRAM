package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.noram.model.QRCode;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for the QRCode class.
 */
public class QRCodeUnitTest {
    /**
     * Tests that updating the encoded data of QR code updates the bitmap
     */
    @Test
    public void bitmapUpdateTest() {
        // Arrange
        QRCode qrCode = mock(QRCode.class);
        doNothing().when(qrCode).updateDBQRCode();
        doCallRealMethod().when(qrCode).setEncodedData(any(String.class));

        // Act
        qrCode.setEncodedData("start_data");

        // Assert
        verify(qrCode, times(1)).updateBitmap();
    }

    /**
     * Tests the update with map method in the QR code properly sets fields.
     */
    @Test
    public void updateWithMapTest() {
        QRCode qrCode = mock(QRCode.class);
        doCallRealMethod().when(qrCode).updateWithMap(any(Map.class));
        doCallRealMethod().when(qrCode).getHashId();
        doCallRealMethod().when(qrCode).getAssociatedEvent();
        doCallRealMethod().when(qrCode).getEncodedData();
        doCallRealMethod().when(qrCode).getQrCodeType();
        doNothing().when(qrCode).updateBitmap();

        String encodedData = "data!";
        String hashId = "coolHash";
        String associatedEvent = "coolEvent!";
        String type = "PROMOTIONAL";
        Map<String, Object> data = new HashMap<>();
        data.put("encodedData", encodedData);
        data.put("event", associatedEvent);
        data.put("type", type);
        data.put("hashID", hashId);
        qrCode.updateWithMap(data);

        assertEquals(encodedData, qrCode.getEncodedData());
        assertEquals(hashId, qrCode.getHashId());
        assertEquals(type, qrCode.getQrCodeType().toString());
        assertEquals(associatedEvent, qrCode.getAssociatedEvent());

    }

}
