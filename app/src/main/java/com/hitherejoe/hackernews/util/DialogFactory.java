package com.hitherejoe.hackernews.util;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.hitherejoe.hackernews.R;

public class DialogFactory {

    public static Dialog createRateDialog(Context context,
                                          DialogInterface.OnClickListener onClickListener) {
        AlertDialog rateDialog = new AlertDialog.Builder(context).create();
        rateDialog.setTitle(context.getString(R.string.dialog_rate_app_title));
        rateDialog.setMessage(context.getString(R.string.dialog_rate_app_text));
        rateDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.dialog_positive_button_text), onClickListener);
        rateDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.dialog_negative_button_text), onClickListener);
        rateDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.dialog_neutral_button_text), onClickListener);
        return rateDialog;
    }

}
