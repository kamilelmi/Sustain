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
import com.sustain.BaseActivity;
import com.sustain.R;
import com.sustain.adapters.InvitationAdapter;
import com.sustain.databinding.FragmentInvitationBinding;
import com.sustain.model.Invitation;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;


public class InvitationFragment extends Fragment
{
    FragmentInvitationBinding binding;
    InvitationAdapter adapter;
    List<Invitation> invitationsList;

    FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invitation, container, false);

        if (invitationsList == null) invitationsList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        binding.rvInvitation.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.rvInvitation.getContext(), manager.getOrientation());
        binding.rvInvitation.addItemDecoration(dividerItemDecoration);
        adapter = new InvitationAdapter(requireContext(), invitationsList, (BaseActivity) requireActivity());
        binding.rvInvitation.setAdapter(adapter);
        auth = FirebaseAuth.getInstance();
        getChallenge();
        return binding.getRoot();
    }


    private void getChallenge()
    {
        invitationsList.clear();

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
                    if (Objects.requireNonNull(timeFormat.parse(currentTime)).before(timeFormat.parse(listData.challengeData.endTime)) && !listData.isAccepted)
                    {
                        invitationsList.add(listData);
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
}