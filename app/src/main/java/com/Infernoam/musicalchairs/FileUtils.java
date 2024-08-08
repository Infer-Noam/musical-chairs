package com.Infernoam.musicalchairs;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static Uri copyToInternalStorageAndGetFileUri(Context context, Uri contentUri) throws IOException {
        // Get the file name from the content URI
        String fileName = "localSong.mp3";

        // Create a file in the app's internal storage
        File internalFile = new File(context.getFilesDir(), fileName);

        // Copy the file from the content URI to internal storage
        try (InputStream inputStream = context.getContentResolver().openInputStream(contentUri);
             FileOutputStream outputStream = new FileOutputStream(internalFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        // Return the file URI of the copied file
        return Uri.fromFile(internalFile);
    }
}