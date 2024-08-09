package com.Infernoam.musicalchairs;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.io.IOException;


public class Settings extends AppCompatActivity {

    private PreferenceFragmentCompat settingsFragment;


    public static final String SHARED_PREFS = "sharedPrefs";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            settingsFragment = new SettingsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, settingsFragment, "SettingsFragmentTag")
                    .commit();
        } else {
            settingsFragment = (PreferenceFragmentCompat) getSupportFragmentManager().findFragmentByTag("SettingsFragmentTag");
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

                // Existing log
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("background")){
                    SaveBackground();
                }
                if (key.equals("song")){
                    SaveSong();
                }
                if (key.equals("TimerVisibility")){
                    SaveTimerVisibility();
                }
                if (key.equals("auto")){
                    SaveAuto();
                }
                if (key.equals("autoTimes")){
                    SaveAutoTime();
                }
                if(key.equals("isLocalSong")){
                    SaveIsLocalSong();
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }

        @Override public boolean onPreferenceTreeClick(Preference preference) {
            if (preference.getKey().equals("third_party")) {
                showSongListDialog();
                return true; // Click handled
            }
            return super.onPreferenceTreeClick(preference);
        }
        private void showSongListDialog() {
            String[] thirdPartyEntries = getResources().getStringArray(R.array.about_entries);
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.third_party_title)
                    .setItems(thirdPartyEntries, (dialog, which) -> {
                        dialog.dismiss();
                        // Add any logic you need when a song is tapped
                    })
                    .show();
        }
    }

    public void SaveBackground(){
        SharedPreferences shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean value = sharedPref.getBoolean("background", false);
        editor.putBoolean("background", value);
        editor.apply();// commit is important here

        if(value){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void SaveSong() {
        SharedPreferences shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String value = sharedPref.getString("song", "0");
        editor.putString("song", value);
        editor.apply();// commit is important here
        //Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
        Log.d("MusiChairs", value + " was chosen");

    }

    public void SaveTimerVisibility(){
        SharedPreferences shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean value = sharedPref.getBoolean("TimerVisibility", false);
        editor.putBoolean("TimerVisibility", value);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("TimerVisibility", value); // Replace "key" and "value" with your data
        setResult(RESULT_OK, resultIntent);
        editor.apply();// commit is important here
    }
    public void SaveAuto() {
        SharedPreferences shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean value = sharedPref.getBoolean("auto", false);
        editor.putBoolean("auto", value);
        editor.apply();// commit is important here
    }
    public void SaveAutoTime() {
        SharedPreferences shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String value = sharedPref.getString("autoTimes", "10");

        if (value.matches("\\d+")) {
            if (Integer.parseInt(value) > 0 && Integer.parseInt(value) <= 60) {
                editor.putString("autoTimes", value);
                editor.apply();// commit is important here
                //Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
            }
            else {
                editor.putString("autoTimes", "10");
                editor.apply();
            }
        }
        else {
            editor.putString("autoTimes", "10");
            editor.apply();
        }
    }
    public void SaveIsLocalSong(){
        SharedPreferences shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean value = sharedPref.getBoolean("isLocalSong", false);
        editor.putBoolean("isLocalSong", value);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isLocalSong", value); // Replace "key" and "value" with your data
        setResult(RESULT_OK, resultIntent);
        editor.apply();// commit is important here
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Check if the up button in the action bar was clicked
        if (id == android.R.id.home) {
            // Finish the Settings Activity to return to the Main Activity
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AudioFilePreference.REQUEST_CODE_PICK_AUDIO) {
            if(settingsFragment != null) {
                AudioFilePreference audioFilePreference = settingsFragment.findPreference("audioFile");
                if (audioFilePreference != null) {
                    try {
                        audioFilePreference.handleActivityResult(requestCode, resultCode, data);
                    } catch (IOException e) {
                        Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
