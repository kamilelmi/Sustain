package com.sustain;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends BaseActivity
{
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //Setting timer to 2 seconds
        countDownTimer = new CountDownTimer(2700, 500)
        {

            public void onTick(long millisUntilFinished)
            {

            }

            public void onFinish()
            {
                moveToNext();
            }
        }.start();


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        countDownTimer.cancel();
    }

    private void moveToNext()
    {
        Intent intent = null;

        if (getIntent().getExtras() != null)
        {
            intent.putExtras(getIntent().getExtras());
            setIntent(null);
        }

        if (session().isActive())
        {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else
        {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}