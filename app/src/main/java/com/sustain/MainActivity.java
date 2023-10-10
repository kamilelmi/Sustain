package com.sustain;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sustain.databinding.ActivityMainBinding;
import com.sustain.fragments.AddActionFragment;
import com.sustain.fragments.ChallengeFragment;
import com.sustain.fragments.HomeFragment;
import com.sustain.fragments.ProfileFragment;
import com.sustain.fragments.SearchFragment;
import com.sustain.fragments.SettingsFragment;
import com.sustain.model.User;
import com.sustain.session.Session;
import com.sustain.utils.Constants;
import com.sustain.utils.StripeHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class MainActivity extends BaseActivity
{
    ActivityMainBinding binding;
    FirebaseAuth auth;

    User userInfo;
    ValueEventListener userInfoListener;
    DatabaseReference userInfoRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        userInfoRef = FirebaseDatabase.getInstance().getReference("user").child(auth.getUid()).child("userInfo");

        getLoginUserInfo();
        userInfoRef.addValueEventListener(userInfoListener);
        displayFragment(new HomeFragment());

        binding.btnSearch.setOnClickListener(view -> displayFragmentWithBackstack(new SearchFragment()));
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.mFeed)
            {
                displayFragment(new HomeFragment());
            } else if (id == R.id.mChallenge)
            {
                displayFragment(new ChallengeFragment());
            } else if (id == R.id.mAdd)
            {
                displayFragment(new AddActionFragment());
            } else if (id == R.id.mProfile)
            {
                displayFragment(new ProfileFragment());
            } else if (id == R.id.mSettings)
            {
                displayFragment(new SettingsFragment());
            }

            return true;
        });

    }

    public void displayFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rlRoot, fragment)
                .commit();
    }

    public void displayFragmentWithBackstack(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rlRoot, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
    }

    private void getLoginUserInfo()
    {
        userInfoListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                userInfo = dataSnapshot.getValue(User.class);

                Constants.userObj = userInfo;

                session().add(Session.LOGGED_IN_USER, userInfo);

                if(userInfo.stripe_id == null)
                {
                    new StripeHelper().getStripeCustomerId(MainActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onBackPressed()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            getSupportFragmentManager().popBackStack();
            setSearchOptionVisibility(true);
        } else
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

            dialogBuilder.setTitle(R.string.title_signOut)
                    .setMessage(R.string.txt_logout_confirmation)
                    .setNegativeButton("No", (dialog, which) -> {
                        dialogBuilder.setCancelable(true);
                    })
                    .setPositiveButton("ok", (dialog, which) -> {
                        // session().end();
                        auth.signOut();
                        finish();
                    }).show();

        }
    }

    public void setSearchOptionVisibility(boolean isVisible)

    {
        if (isVisible)
        {
            binding.btnSearch.setVisibility(View.VISIBLE);
        }else
        {
            binding.btnSearch.setVisibility(View.GONE);
        }

    }
}