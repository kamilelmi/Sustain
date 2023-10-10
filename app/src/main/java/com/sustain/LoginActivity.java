package com.sustain;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sustain.databinding.ActivityLoginBinding;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

public class LoginActivity extends BaseActivity
{
    ActivityLoginBinding binding;
    private SignInClient oneTapClient;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        oneTapClient = Identity.getSignInClient(this);
        auth = FirebaseAuth.getInstance();

        binding.tvSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();

                boolean validate = validation(email, password);
                if (!validate) return;
                showLoaderDialog("login..");

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            hideLoaderDialog();
                            Toast.makeText(getApplicationContext(), "Login Successfully" + "", Toast.LENGTH_LONG).show();
                            navigateToHomeActivity();

                        } else
                        {
                            hideLoaderDialog();
                            Toast.makeText(getApplicationContext(), "Email & password is incorrect" + "", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        binding.tvResetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                resetPassword();
            }
        });

        binding.btnFb.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });

        binding.btnGoogle.setOnClickListener(view -> {
            signIn();
        });
    }

    private void signIn()
    {
        BeginSignInRequest signUpRequest = createSignInRequest();
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, result -> loginResultHandler.launch(new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build()))
                .addOnFailureListener(this, e -> {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + "", Toast.LENGTH_LONG).show();
                });
    }

    private BeginSignInRequest createSignInRequest()
    {
        return BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();
    }


    private void resetPassword()
    {
        EditText resetPassword = new EditText(this);

        final AlertDialog.Builder passswordReset = new AlertDialog.Builder(this);
        passswordReset.setMessage("Enter email to receive Reset the password link");
        passswordReset.setView(resetPassword);
        passswordReset.setPositiveButton("Yes", (dialogInterface, i) -> {

            String email = resetPassword.getText().toString();
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(this, "Reset link send to Your Email" + "", Toast.LENGTH_LONG).show();
                        }
                    });
        });


        passswordReset.setNegativeButton("No", (dialogInterface, i) -> {
        });

        passswordReset.create().show();
    }
    private ActivityResultLauncher<IntentSenderRequest> loginResultHandler = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
        // handle intent result here
        if (result.getResultCode() == RESULT_OK)
        {
            SignInCredential credential = null;
            try
            {
                //credential obj used for getting the user info if required
                Toast.makeText(getApplicationContext(), "Login Successfully" + "", Toast.LENGTH_LONG).show();

                credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                navigateToHomeActivity();
            } catch (ApiException e)
            {
                e.printStackTrace();
            }
        } else
        {
            //...
        }
    });

    private boolean validation(String email, String password)
    {
        if (email.isEmpty())
        {
            binding.etEmail.setError(getString(R.string.msg_empty_email));
            return false;
        } else if (password.isEmpty())
        {
            binding.etPassword.setError(getString(R.string.msg_empty_password));
            return false;
        }
        return true;
    }

    private void navigateToHomeActivity()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}