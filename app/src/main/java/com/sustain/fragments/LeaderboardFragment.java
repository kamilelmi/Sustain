package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sustain.R;
import com.sustain.adapters.LeaderboardAdapter;
import com.sustain.databinding.FragmentLeaderboardBinding;
import com.sustain.model.Invitation;
import com.sustain.model.LeaderBoard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class LeaderboardFragment extends Fragment
{
    private static final String CHALLENGE_DATA = "param1";

    FragmentLeaderboardBinding binding;

    Invitation challengeData;

    List<LeaderBoard> leaderBoardList;

    LeaderboardAdapter adapter;

    public static LeaderboardFragment newInstance(Invitation invitation)
    {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHALLENGE_DATA, invitation);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_leaderboard, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null)
        {
            challengeData = (Invitation) getArguments().getSerializable(CHALLENGE_DATA);
        }

        leaderBoardList = new ArrayList<>();
        binding.rvLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LeaderboardAdapter(requireContext(), leaderBoardList);
        binding.rvLeaderboard.setAdapter(adapter);

        fetchLeaderBoardData();
    }


    private void fetchLeaderBoardData()
    {

        DatabaseReference fetchLeaderBoardData = FirebaseDatabase.getInstance().getReference("leaderBoard").child(challengeData.getKey());
        fetchLeaderBoardData.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                LeaderBoard leaderBoardData = snapshot.getValue(LeaderBoard.class);
                leaderBoardList.add(leaderBoardData);
                sortLeaderBoardList();
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

    private void sortLeaderBoardList()
    {
        adapter.notifyDataSetChanged();

        leaderBoardList.sort(Collections.reverseOrder());
        adapter.notifyDataSetChanged();

    }
}
