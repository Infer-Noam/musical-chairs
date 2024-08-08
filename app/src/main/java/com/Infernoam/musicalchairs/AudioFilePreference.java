package com.Infernoam.musicalchairs; // Replace with your actual package name

import static com.Infernoam.musicalchairs.FileUtils.copyToInternalStorageAndGetFileUri;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class AudioFilePreference extends Preference {

    public static final int REQUEST_CODE_PICK_AUDIO = 1001;

    public AudioFilePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTitle("Select Audio File"); // Set the title or summary as needed
    }

    @Override
    public void onClick() {
        super.onClick();
        // Launch an intent to pick an audio file
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (getContext() instanceof Activity) {((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO);
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) throws IOException {
        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == Activity.RESULT_OK) {
            Uri audioUri = data != null ? data.getData() : null;

            Uri fileUri = copyToInternalStorageAndGetFileUri(getContext(), audioUri);

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("localSong", fileUri != null ? fileUri.toString() : null);
            editor.apply();
        }
    }
}


