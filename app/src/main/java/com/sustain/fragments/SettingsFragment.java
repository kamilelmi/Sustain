package com.sustain.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sustain.R;
import com.sustain.databinding.FragmentSettingsBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment
{
    FragmentSettingsBinding binding;
    FirebaseAuth auth;
    String appVersion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        binding.btnResetPassword.setOnClickListener(this::resetPassword);
        binding.btnContactUs.setOnClickListener(view -> contactUs());

        try
        {
            appVersion = String.valueOf(requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).versionName);
            binding.txtAppVersion.setText(appVersion);
        } catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (auth == null) auth = FirebaseAuth.getInstance();
    }

    private void resetPassword(View view)
    {
        EditText resetPassword = new EditText(view.getContext());

        final AlertDialog.Builder passswordReset = new AlertDialog.Builder(view.getContext());
        passswordReset.setMessage("Enter email to receive Reset the password link");
        passswordReset.setView(resetPassword);
        passswordReset.setPositiveButton("Yes", (dialogInterface, i) -> {

            String email = resetPassword.getText().toString();
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(requireContext(), "Reset link send to Your Email" + "", Toast.LENGTH_LONG).show();
                        }
                    });
        });


        passswordReset.setNegativeButton("No", (dialogInterface, i) -> {
        });

        passswordReset.create().show();
    }

    private void contactUs()
    {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@sustain.com"));
        startActivity(Intent.createChooser(emailIntent, "Contact Us"));
    }
}
