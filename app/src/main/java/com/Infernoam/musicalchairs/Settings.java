package com.Infernoam.musicalchairs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class Settings extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

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
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

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

}