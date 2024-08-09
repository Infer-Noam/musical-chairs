package com.Infernoam.musicalchairs;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.preference.Preference;

public class TermsAndConditionsPreference extends Preference {


    public TermsAndConditionsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTitle("Terms and conditions"); // Set the title or summary as needed
    }

    @Override
    public void onClick() {
        super.onClick();
        if (getContext() instanceof Activity) {
            String url = getContext().getString(R.string.terms_and_conditions_url);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


            if (getContext() instanceof Activity) {
                getContext().startActivity(intent);
            }

        }
    }


}


