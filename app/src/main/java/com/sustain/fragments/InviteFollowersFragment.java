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
import com.sustain.R;
import com.sustain.adapters.FollowerAdapter;
import com.sustain.databinding.FragmentInviteFollowersBinding;
import com.sustain.interfaces.InvitedFollower;
import com.sustain.model.Follower;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class InviteFollowersFragment extends Fragment implements FollowerAdapter.InvitedFollowerListener
{
    private static final String INVITED_FOLLOWER = "INVITED_FOLLOWER";

    FirebaseAuth auth;
    FragmentInviteFollowersBinding binding;

    List<Follower> invitedFollowerList;

    List<Follower> followerList;
    FollowerAdapter adapter;

    InvitedFollower invitedFollowerListener;

    public static InviteFollowersFragment newInstance(InvitedFollower invitedFollowerListener)
    {
        InviteFollowersFragment fragment = new InviteFollowersFragment();
        Bundle args = new Bundle();
        args.putParcelable(INVITED_FOLLOWER, invitedFollowerListener);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite_followers, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if (getArguments() != null)
        {
            invitedFollowerListener = (InvitedFollower) getArguments().getParcelable(INVITED_FOLLOWER);
        }
        followerList = new ArrayList<>();
        invitedFollowerList = new ArrayList<>();
        adapter = new FollowerAdapter(requireContext(), followerList, this);

        binding.rvFollowers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFollowers.setAdapter(adapter);


        binding.btnAddSelected.setOnClickListener(view1 ->
        {
            if (invitedFollowerList.size() < 1)
            {
                Toast.makeText(requireContext(), "Invite at least one Follower", Toast.LENGTH_SHORT).show();
                return;
            }
            invitedFollowerListener.invitedFollowerList(invitedFollowerList);
            requireActivity().onBackPressed();
        });
        getFollowerList();

    }

    private void getFollowerList()
    {
        DatabaseReference fetchChallenges = FirebaseDatabase.getInstance().getReference("user").child(auth.getUid()).child("follower");
        fetchChallenges.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                try
                {
                    Follower listData = snapshot.getValue(Follower.class);
                    assert listData != null;
                    followerList.add(listData);
                } catch (DatabaseException e)
                {
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void invitedFollower(List<Follower> invitedFolloerList)
    {
        this.invitedFollowerList = invitedFolloerList;
    }

}
