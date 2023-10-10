package com.sustain;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sustain.databinding.ActivityRegisterBinding;
import com.sustain.model.User;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;

public class RegisterActivity extends BaseActivity
{
    ActivityRegisterBinding binding;
    FirebaseAuth auth;
    String userName, email, phoneNumber, password;
    private SignInClient oneTapClient;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);

        //Btn SignUp with Fb
        binding.btnFb.setOnClickListener(view -> {
        });

        //Btn SignUp with Google Account\
        binding.btnGoogle.setOnClickListener(view -> {
            signUp();
        });

        //Btn SignUp with email & password
        binding.btnRegister.setOnClickListener(view -> {

            userName = binding.etUsername.getText().toString();
            email = binding.etEmail.getText().toString();
            phoneNumber = binding.etPhone.getText().toString();
            password = binding.etPassword.getText().toString();

            boolean validate = validation(email, userName, phoneNumber, password);

            if (!validate) return;

            showLoaderDialog("SignUp..");
            registerUserWithEmailAndPassword();

        });

    }


    private boolean validation(String email, String userName, String phoneNumber, String password)
    {
        if (email.isEmpty())
        {
            binding.etEmail.setError(getString(R.string.msg_empty_email));
            return false;
        } else if (userName.isEmpty())
        {
            binding.etUsername.setError(getString(R.string.msg_empty_user_name));
            return false;
        } else if (phoneNumber.isEmpty())
        {
            binding.etPhone.setError(getString(R.string.msg_empty_phone_number));
        } else if (password.isEmpty())
        {
            binding.etPassword.setError(getString(R.string.msg_empty_password));
            return false;
        }
        return true;
    }

    private void registerUserWithEmailAndPassword()
    {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                hideLoaderDialog();
                Toast.makeText(getApplicationContext(), "SignUp Successfully" + "", Toast.LENGTH_LONG).show();

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("user").child(auth.getUid()).child("userInfo");
                User user = new User(userName, email, phoneNumber, 0, 0, 0, 0, 0, 0,false);
                usersRef.setValue(user);
                finish();
            } else
            {

            }
        });
    }

    private void signUp()
    {
        BeginSignInRequest signUpRequest = createSignUpRequest();
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, result -> {
                    showLoaderDialog("SignUp..");
                    loginResultHandler.launch(new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build());
                })
                .addOnFailureListener(this, e -> {

                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + "", Toast.LENGTH_LONG).show();
                });
    }

    private BeginSignInRequest createSignUpRequest()
    {
        return BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }


    private ActivityResultLauncher<IntentSenderRequest> loginResultHandler = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
        // handle intent result here
        if (result.getResultCode() == RESULT_OK)
        {
            hideLoaderDialog();
            SignInCredential credential = null;
            try
            {
                credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                String idToken = credential.getGoogleIdToken();
                userName = credential.getDisplayName();
                phoneNumber = credential.getPhoneNumber();
                email = credential.getId();
                if (idToken != null)
                {
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("user").child(auth.getUid()).child("userInfo");

                    User user = new User(userName, email, phoneNumber, 0, 0, 0, 0, 0, 0,false);
                    usersRef.setValue(user);
                }
            } catch (ApiException e)
            {
                e.printStackTrace();
            }
        } else
        {
            Toast.makeText(getApplicationContext(), result.describeContents(), Toast.LENGTH_LONG).show();

        }
    });
}