package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.sustain.BaseActivity;
import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.adapters.ChallengeAdapter;
import com.sustain.databinding.FragmentChallengeBinding;
import com.sustain.interfaces.BookingCallback;
import com.sustain.interfaces.StripeResultCallback;
import com.sustain.model.Invitation;
import com.sustain.model.User;
import com.sustain.session.Session;
import com.sustain.utils.Constants;
import com.sustain.utils.StripeHelper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ChallengeFragment extends Fragment
{
    FragmentChallengeBinding binding;
    ChallengeAdapter adapter;
    FirebaseAuth auth;
    List<Invitation> onGoingChallengesList;
    List<Invitation> pastChallengesList;
    Boolean pastChallengesSelected = true;
    User userInfo;
    StripeHelper helper;
    String empKey, secret;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (userInfo == null) userInfo = new User();
        userInfo= (User) ((BaseActivity)requireActivity()).session().get(Session.LOGGED_IN_USER);


        if (onGoingChallengesList == null) onGoingChallengesList = new ArrayList<>();
        if (pastChallengesList == null) pastChallengesList = new ArrayList<>();
        getChallenge();

        helper = new StripeHelper();
        helper.initPaymentSheet(this, new StripeResultCallback()
        {
            @Override
            public void onTransactionSuccess()
            {
                userInfo.setPaid(true);
                FirebaseDatabase.getInstance().getReference()
                        .child("user")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("userInfo")
                        .child("isPaid")
                        .setValue(true);

                ((MainActivity) requireActivity()).displayFragmentWithBackstack(new AddChallengeFragment());
            }

            @Override
            public void onTransactionFailed()
            {

            }
        });
        helper.getEphemeralKey(new BookingCallback()
        {
            @Override
            public void onKeyDownloaded(String ephemeralKey)
            {
                empKey = ephemeralKey;
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        binding.rvChallenges.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.rvChallenges.getContext(), manager.getOrientation());
        binding.rvChallenges.addItemDecoration(dividerItemDecoration);
        if (pastChallengesSelected)
        {
            adapter = new ChallengeAdapter(requireContext(), pastChallengesList);

        } else
        {
            adapter = new ChallengeAdapter(requireContext(), onGoingChallengesList);
        }
        binding.rvChallenges.setAdapter(adapter);
        binding.radioOnGoingChallenge.setOnClickListener(view1 -> onChallengeMethodChange(binding.radioOnGoingChallenge));
        binding.radioPastChallenge.setOnClickListener(view1 -> onChallengeMethodChange(binding.radioPastChallenge));
        binding.btnAddChallenge.setOnClickListener(view12 -> {
            if (userInfo.isPaid)
            {
                ((MainActivity) requireActivity()).displayFragmentWithBackstack(new AddChallengeFragment());
            } else
            {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());

                dialogBuilder.setTitle(R.string.title_add_challenge)
                        .setMessage(R.string.msg_paid)
                        .setNegativeButton("cancel", (dialog, which) -> {
                            dialogBuilder.setCancelable(true);
                        })
                        .setPositiveButton("subscribe", (dialog, which) -> {
                            helper.runStripe(19.99, new BookingCallback()
                            {
                                @Override
                                public void onKeyDownloaded(String clientSecret)
                                {
                                    secret = clientSecret;
                                    PaymentConfiguration.init(requireContext(), Constants.PUBLISHABLE_KEY);

                                    dialog.dismiss();
                                    presentPaymentSheet();
                                }
                            });
                        }).show();
            }
        });
    }

    private void onChallengeMethodChange(View view)
    {
        if (view.getId() == R.id.radioOnGoingChallenge)
        {
            pastChallengesSelected = false;
            adapter = new ChallengeAdapter(requireContext(), onGoingChallengesList);
        } else
        {
            pastChallengesSelected = true;
            adapter = new ChallengeAdapter(requireContext(), pastChallengesList);
        }
        binding.rvChallenges.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    private void getChallenge()
    {
        onGoingChallengesList.clear();
        pastChallengesList.clear();

        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm");
        Date date = new Date();
        Timestamp startTimestamp = new Timestamp(date.getTime());
        String currentTime = timeFormat.format(startTimestamp);


        DatabaseReference fetchChallenges = FirebaseDatabase.getInstance().getReference("user").child(auth.getUid()).child("invitations");
        fetchChallenges.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                try
                {
                    Invitation listData = snapshot.getValue(Invitation.class);
                    assert listData != null;
                    listData.setKey(snapshot.getKey());
                    if (Objects.requireNonNull(timeFormat.parse(currentTime)).before(timeFormat.parse(listData.challengeData.endTime)))
                    {
                        onGoingChallengesList.add(listData);
                    } else
                    {
                        pastChallengesList.add(listData);
                    }
                } catch (DatabaseException e)
                {
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } catch (ParseException e)
                {
                    throw new RuntimeException(e);
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void presentPaymentSheet()
    {
        helper.paymentSheet.presentWithPaymentIntent(
                secret,
                new PaymentSheet.Configuration(
                        "Sustain",
                        new PaymentSheet.CustomerConfiguration(
                                userInfo.getStripeId(),
                                empKey
                        )
                )
        );
    }

}

