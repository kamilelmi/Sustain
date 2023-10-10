package com.sustain.interfaces;

public interface StripeResultCallback
{
    void onTransactionSuccess();
    void onTransactionFailed();
}
