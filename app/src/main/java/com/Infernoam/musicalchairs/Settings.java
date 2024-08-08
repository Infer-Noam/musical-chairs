package com.Infernoam.musicalchairs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // Your preference change handling code
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AudioFilePreference.REQUEST_CODE_PICK_AUDIO && settingsFragment != null) {
            AudioFilePreference audioFilePreference = settingsFragment.findPreference("audioFile");
            if (audioFilePreference != null) {
                try {
                    audioFilePreference.handleActivityResult(requestCode, resultCode, data);
                } catch (IOException e) {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
