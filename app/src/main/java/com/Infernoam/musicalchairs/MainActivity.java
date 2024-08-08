package com.Infernoam.musicalchairs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import com.facebook.FacebookSdk;

public class MainActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    private int AdCounter = 1;
    private EditText input1; //input of players
    private EditText input2; //input of shortest round
    private EditText input3; //input of longest round
    private Button buttonStart; //start button
    private static long START_TIME_IN_MILLIS = 0;

    private TextView mTextViewCountDown;
    private Button mButtonReset;
    private TextView rCount;
    private Button EndButton; //the buttonthat end the game

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private int round = 1;
    private int fRound;

    private int MaxRound;
    private int MinRound;
    private int MaxDownLimit = 60;


    public static final String SHARED_PREFS = "sharedPrefs";
    private Integer songNumber = 0;
    MediaPlayer player;
    boolean settingsActive = true;
   SharedPreferences sharedPreferences;
   private static final int SETTINGS_REQUEST_CODE = 1;

   boolean autoRound = false;
   boolean timerDefult = true; // timerdefult is the timer that count rounds, if false the timer is being use between rounds
    boolean fPause = true;
    List<Integer> SONGS = Arrays.asList(R.raw.springupbeat, R.raw.chaseme, R.raw.happyenergeticday, R.raw.sunnydaysindie, R.raw.quickstart, R.raw.childrenelectroswing1, R.raw.aherofthe80s, R.raw.catchit, R.raw.electrosummerpositiveparty, R.raw.energeticindierockjump, R.raw.funnyrunning, R.raw.happyday, R.raw.ladyofthe80, R.raw.retrofunkenergeticbackgroundmusic, R.raw.upbeatrockgoodnews); // example for playlist

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_settings);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        input3 = findViewById(R.id.input3);
        buttonStart = findViewById(R.id.button_start);

        input1.addTextChangedListener(loginTextWatcher);
        input2.addTextChangedListener(loginTextWatcher);
        input3.addTextChangedListener(loginTextWatcher);

        mTextViewCountDown = findViewById(R.id.CountdownTimer);
        mButtonReset = findViewById(R.id.button_reset);
        EndButton = findViewById(R.id.button_End);

        rCount = findViewById(R.id.roundCounter);

        TimerVisibility();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                    fPause = false;
                } else {
                    startTimer();
                    settingsActive = false;
                }
                if (timerDefult) {
                    playPause(v);
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                mButtonReset.setEnabled(false);
            }
        });

        EndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.cancel();
                mTimerRunning = false;
                EndButton.setEnabled(false);
                EndButton.setVisibility(View.INVISIBLE);
                buttonStart.setText("Start a new game");
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                mButtonReset.setVisibility(View.INVISIBLE);
                mButtonReset.setEnabled(false);
                input1.setEnabled(true);
                input2.setEnabled(true);
                input3.setEnabled(true);
                round = 1;
                rCount.setText("Game ended");
                Random r = new Random();
                if (MaxRound == MinRound){
                    mTimeLeftInMillis = 15000;
                }
                else {
                    mTimeLeftInMillis = (r.nextInt(MaxRound - MinRound) + MinRound) * 1000L;
                }
                START_TIME_IN_MILLIS = mTimeLeftInMillis;
                ((ProgressBar)findViewById(R.id.progressBar)).setMax((int) mTimeLeftInMillis * 1000);
                updateCountDownText();
                //the end button stops working, the timer returns to 00:00 and you need to reenter values
                stopPlayer();
                settingsActive = true;
                timerDefult = true;
            }
        });
        updateCountDownText();
        ((ProgressBar)findViewById(R.id.progressBar)).setMax(1);
        ((ProgressBar)findViewById(R.id.progressBar)).setProgress(1);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        boolean Background = sharedPreferences.getBoolean("background", false);

        if(Background){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-4384673899469944/3278334250", adRequest,//"ca-app-pub-3940256099942544/8691691433"
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
    }


    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userInput1 = input1.getText().toString().trim();
            String userInput2 = input2.getText().toString().trim();
            String userInput3= input3.getText().toString().trim();

            boolean Valid = handleInput() ;// check how its true;

            if(Valid){
                Random r = new Random();
                MinRound = Integer.parseInt(userInput2);
                MaxRound = Integer.parseInt(userInput3);
                if (MaxRound == MinRound){
                    mTimeLeftInMillis = MaxRound * 1000L;
                }
                else {
                    mTimeLeftInMillis = (r.nextInt(MaxRound - MinRound) + MinRound) * 1000L;
                }
                START_TIME_IN_MILLIS = mTimeLeftInMillis;
                fRound = Integer.parseInt(userInput1);
                updateCountDownText();
            }

            buttonStart.setEnabled(!userInput1.isEmpty() && !userInput2.isEmpty() && !userInput3.isEmpty() && Valid);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    public boolean handleInput(){
        //Lengths();
        boolean Valid = true;
        boolean i2v = true;
        boolean i3v = true;
        TextView t = findViewById(R.id.input1);
        if(!t.getText().toString().equals("")) {
            String input = t.getText().toString();
            Log.d("inputio1", input);
            int inp = Integer.parseInt(input);
            if (inp < 2 || inp > 60) {
                (findViewById(R.id.warning1)).setVisibility(View.VISIBLE);
                Valid = false;
            } else {
                ( findViewById(R.id.warning1)).setVisibility(View.INVISIBLE);
            }
        }
        else {( findViewById(R.id.warning1)).setVisibility(View.INVISIBLE);
            Valid = false;}

        TextView t2 = findViewById(R.id.input2);
        if (!t2.getText().toString().equals("")){
            String input = t2.getText().toString();
            Log.d("inputio2" , input);
            int inp = Integer.parseInt(input);
            if(inp < 5 || inp > 59) {
                (findViewById(R.id.warning2)).setVisibility(View.VISIBLE);
                Valid = false;
                i2v = false;

                ((TextView)findViewById(R.id.warning2)).setText("The shortest round has to be between 5 - 59");
                ((TextView)findViewById(R.id.warning3)).setText("The Longest round has to be between " + MaxDownLimit + " - 60");
            }
            else {
                (findViewById(R.id.warning2)).setVisibility(View.INVISIBLE);
                MaxDownLimit = inp;
                ((TextView)findViewById(R.id.warning2)).setText("The shortest round has to be between 5 - 59");
                ((TextView)findViewById(R.id.warning3)).setText("The Longest round has to be between " + MaxDownLimit + " - 60");
            }
        }
        else {(findViewById(R.id.warning2)).setVisibility(View.INVISIBLE);
            Valid = false;
            i2v = false;}

        TextView t3 = findViewById(R.id.input3);
        if (!t3.getText().toString().equals("")){
            String input = t3.getText().toString();
            Log.d("inputio3" , input);
            int inp = Integer.parseInt(input);
            if(inp < MaxDownLimit || inp > 60 || inp < 5) {
                (findViewById(R.id.warning3)).setVisibility(View.VISIBLE);
                Valid = false;
                i3v = false;

            }
            else {
                (findViewById(R.id.warning3)).setVisibility(View.INVISIBLE);
                if (!i2v && i3v){
                    MaxDownLimit = inp;
                }
            }
        }
        else {(findViewById(R.id.warning3)).setVisibility(View.INVISIBLE);
            Valid = false;
            i3v = false;}

        if(!i2v && i3v){
            ((TextView)findViewById(R.id.warning2)).setText("The shortest round has to be between 5 - " + MaxDownLimit);
            (findViewById(R.id.warning2)).setVisibility(View.VISIBLE);
        } else if (i2v && !i3v) {
            ((TextView)findViewById(R.id.warning3)).setText("The Longest round has to be between " + MaxDownLimit + " - 60");
            (findViewById(R.id.warning3)).setVisibility(View.VISIBLE);
        } else if (!i2v && !i3v) {
            MaxDownLimit = 5;
            ((TextView)findViewById(R.id.warning2)).setText("The shortest round has to be between 5 - 59");
            ((TextView)findViewById(R.id.warning3)).setText("The Longest round has to be between " + MaxDownLimit + " - 60");
        }

        return Valid;
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                if (round != fRound) {
                    mTimeLeftInMillis = 3000;
                } else {
                    mTimerRunning = false;
                }
                mTimerRunning = false;
                buttonStart.setText("Start");
                buttonStart.setVisibility(View.VISIBLE);
                playPause(findViewById(R.id.button_start));

                if (round != fRound) {
                    if (!timerDefult) {
                        round++;
                    }
                    Random r = new Random();
                    if (MaxRound == MinRound) {
                        mTimeLeftInMillis = MaxRound * 1000L;
                    } else {
                        mTimeLeftInMillis = (r.nextInt(MaxRound - MinRound) + MinRound) * 1000L;
                    }
                    START_TIME_IN_MILLIS = mTimeLeftInMillis;
                    ((ProgressBar) findViewById(R.id.progressBar)).setMax((int) mTimeLeftInMillis * 1000);
                    Log.d("ThisRoundNum:", String.valueOf(round));

                    String tempTimeAuto = Auto();
                    if (autoRound) {
                        if (timerDefult) {
                            mTextViewCountDown.setVisibility(View.VISIBLE);
                            ((ProgressBar)(findViewById(R.id.progressBar))).setVisibility(View.VISIBLE);
                            mTimeLeftInMillis = Integer.parseInt(tempTimeAuto) * 1000L;
                            timerDefult = false;
                            startTimer();
                            settingsActive = false;
                        } else {
                            TimerVisibility();
                            timerDefult = true;
                            startTimer();
                            settingsActive = false;
                            playPause(findViewById(R.id.button_start));
                        }

                    } else {
                        ((ProgressBar) findViewById(R.id.progressBar)).setMax((int) mTimeLeftInMillis * 1000);
                        updateCountDownText();
                        rCount.setText("Break after round " + round);
                        round++;
                    }
                } else {
                    input1.setEnabled(true);
                    input2.setEnabled(true);
                    input3.setEnabled(true);
                    EndButton.setVisibility(View.INVISIBLE);
                    EndButton.setEnabled(false);
                    round = 1;
                    rCount.setText("Game ended");
                    buttonStart.setText("Start a new game");
                    Random r = new Random();
                    if (MaxRound == MinRound) {
                        mTimeLeftInMillis = MaxRound * 1000L;
                    } else {
                        mTimeLeftInMillis = (r.nextInt(MaxRound - MinRound) + MinRound) * 1000L;
                    }
                    START_TIME_IN_MILLIS = mTimeLeftInMillis;
                    ((ProgressBar) findViewById(R.id.progressBar)).setMax((int) mTimeLeftInMillis * 1000);
                    updateCountDownText();
                    stopPlayer();
                    settingsActive = true;
                }
            }
        }.start();

        mTimerRunning = true;
        buttonStart.setText("Pause");
        mButtonReset.setVisibility(View.INVISIBLE);
        EndButton.setEnabled(true);
        EndButton.setVisibility(View.VISIBLE);
        input1.setEnabled(false);
        input2.setEnabled(false);
        input3.setEnabled(false);
        if (timerDefult) {
            rCount.setText("Round " + round + " out of " + fRound);
        } else {
            rCount.setText("Break after round " + round);
        }
        if (fPause) {
            ((ProgressBar) findViewById(R.id.progressBar)).setMax((int) (mTimeLeftInMillis * 1000));
        }
        else {
            fPause = true;
        }
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        buttonStart.setText("Start");
        if(timerDefult) {
            mButtonReset.setVisibility(View.VISIBLE);
            mButtonReset.setEnabled(true);
        }
        EndButton.setEnabled(true);
        EndButton.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        buttonStart.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);

        mTextViewCountDown.setText(timeLeftFormatted);

        ((ProgressBar)findViewById(R.id.progressBar)).setProgress((int) mTimeLeftInMillis * 1000);
    }

    public void playPause(View v) {
        if(!mTimerRunning){
            Log.d("MusiChairs", "pause");
            if (player != null) {
                player.pause();
            }
        }
        else {
            if (player == null) {

                Log.d("MusiChairs", "play");
                Songs();
                player = MediaPlayer.create(this, SONGS.get(songNumber));
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlayer();
                    }
                });
                }
            player.start();
        }
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Log.d("MusiChairs", "song released");
            //Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();

        playPause(findViewById(R.id.button_start));
        Log.d("MusiChairs", "song played again");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(settingsActive) {
            if (item.getItemId() == android.R.id.home) {
                Intent intent = new Intent(this, Settings.class);
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                //startActivity(intent);

                return true;
            }
            return super.onOptionsItemSelected(item);

        }
        return false;
    }


    public void Songs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String value = sharedPreferences.getString("song", "0");
        if (value.matches("\\d+")) {
            Log.d("MusiChairs",  "check1");
            if (Integer.parseInt(value) < 0 || Integer.parseInt(value) >= 15) {
                Log.d("MusiChairs",  "check2");
                value = "0";
            }
        }
        else {
            Log.d("MusiChairs",  "check3");
            value = "0";
        }
        songNumber = Integer.parseInt(value);
        Log.d("MusiChairs", songNumber.toString() + "  is playing");
    }

    public void TimerVisibility() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean TimerVis =sharedPreferences.getBoolean("TimerVisibility", false);
        if (!TimerVis) {
            mTextViewCountDown.setVisibility(View.GONE);
            ((ProgressBar)(findViewById(R.id.progressBar))).setVisibility(View.GONE);
            Log.d("MusiChairs", "Timer Invisible");

        }
        else {
            mTextViewCountDown.setVisibility(View.VISIBLE);
            ((ProgressBar)(findViewById(R.id.progressBar))).setVisibility(View.VISIBLE);
            Log.d("MusiChairs", "Timer Visible");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                TimerVisibility();
            }
        }

        if(AdCounter < 3){
            AdCounter++;
        }
        else {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            }
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(this, "ca-app-pub-4384673899469944/3278334250", adRequest, //"ca-app-pub-3940256099942544/8691691433"
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                            //Toast.makeText(MainActivity.this, "ad is not ready to show", Toast.LENGTH_SHORT).show();
                        }
                    });
            AdCounter = 1;
        }
    }
    public String Auto() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        autoRound = sharedPreferences.getBoolean("auto", false);

        return sharedPreferences.getString("autoTimes", "10");
    }
}