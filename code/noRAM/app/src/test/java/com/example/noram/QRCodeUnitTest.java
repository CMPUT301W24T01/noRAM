package com.example.noram;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.noram.model.QRCode;
import com.example.noram.model.QRType;

import org.junit.Test;

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
     * Tests that setting values updates the database for the QR code
     */
    @Test
    public void dbUpdateTest() {
        // Arrange
        QRCode qrCode = mock(QRCode.class);
        doNothing().when(qrCode).updateDBQRCode();
        doCallRealMethod().when(qrCode).setQrCodeType(any(QRType.class));
        doCallRealMethod().when(qrCode).setAssociatedEvent(any(String.class));
        doCallRealMethod().when(qrCode).setEncodedData(any(String.class));

        // Act
        qrCode.setEncodedData("a");
        qrCode.setAssociatedEvent("a");
        qrCode.setQrCodeType(QRType.SIGN_IN);

        // Assert
        verify(qrCode, times(3)).updateDBQRCode();
    }
}
