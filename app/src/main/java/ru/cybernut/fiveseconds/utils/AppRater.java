package ru.cybernut.fiveseconds.utils;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import ru.cybernut.fiveseconds.R;

public class AppRater {
    private final static String APP_TITLE = "FiveSecondsGame";
    private final static String APP_PNAME = "ru.cybernut.fiveseconds";

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();
        showRateDialog(mContext, editor);
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext, R.style.DialogTheme);
        dialog.setTitle(APP_TITLE);
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(mContext).inflate(R.layout.rater_dialog, null);
        Button rateButton = constraintLayout.findViewById(R.id.rate_button);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, R.string.launch_playstore_app_error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        Button laterButton = constraintLayout.findViewById(R.id.later_rate_button);
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button dontShowAgainButton = constraintLayout.findViewById(R.id.dontshowagain_button);
        dontShowAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(constraintLayout);
        dialog.show();
    }
}
