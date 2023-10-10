package com.sustain;

import android.app.Dialog;

import com.sustain.session.Session;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 27/04/2023
 */
public abstract class BaseActivity extends AppCompatActivity
{
    Session session;
    private Dialog dialogs;

    public void showLoaderDialog(String message)
    {

        dialogs = new Dialog(this, R.style.DialogLoaderStyle);
        dialogs.setContentView(R.layout.progress);
        dialogs.setCancelable(false);
        dialogs.show();
    }

    public void hideLoaderDialog()
    {
        if (dialogs.isShowing())
        {
            dialogs.dismiss();
        }
    }

    public Session session()
    {
        if (session == null) return session = new Session();
        else return session;
    }
}
