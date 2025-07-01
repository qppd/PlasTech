package com.qppd.plastech.Libs.Imagez;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageBase64 {

    private ByteArrayOutputStream bao = new ByteArrayOutputStream();
    private int quality = 50;

    public ImageBase64(int quality) {
        this.quality = quality;

    }

    public ImageBase64() {
        bao = new ByteArrayOutputStream();
    }

    public String enCode(Bitmap bitmap) {
        // Move the bitmap for recycling issues;
        Bitmap bitmap_to_encode = bitmap;
        // Create a new ByteArrayOutputStream
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        // Compress the bitmap into JPEG format
        bitmap_to_encode.compress(Bitmap.CompressFormat.JPEG, quality, bao);
        // Convert the compressed data to a byte array
        byte[] b = bao.toByteArray();
        // Encode the byte array to a Base64 string
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap deCode(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage.substring(encodedImage.indexOf(",") + 1),
                Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static Bitmap loadDrawableToBitmap(Context context, int drawableResourceId) {
        // Decode the drawable resource into a Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Adjust config as needed

        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawableResourceId, options);

        return bitmap;
    }
}