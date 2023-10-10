package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sustain.BaseActivity;
import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.adapters.FeedAdapter;
import com.sustain.databinding.FragmentHomeBinding;
import com.sustain.interfaces.NavigateToAllCommentListiner;
import com.sustain.model.UploadPostData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class HomeFragment extends Fragment implements NavigateToAllCommentListiner
{
    FragmentHomeBinding binding;
    List<UploadPostData> list_Data;
    private DatabaseReference db;
    FeedAdapter myAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance().getReference("Post");

        binding.rvFeed.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFeed.setHasFixedSize(true);
        list_Data = new ArrayList<>();

        myAdapter = new FeedAdapter(requireContext(), list_Data, this, (BaseActivity) requireActivity());
        binding.rvFeed.setAdapter(myAdapter);
        fetchPostData();

    }

    private void fetchPostData()
    {
        list_Data.clear();
        db.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String authKey = snapshot.getKey();
                for (DataSnapshot post : snapshot.getChildren())
                {
                    try
                    {
                        UploadPostData listData = post.getValue(UploadPostData.class);
                        assert listData != null;
                        listData.setKey(post.getKey());
                        listData.setAuthKey(authKey);
                        if (listData.displayOnFeed)
                        {
                            list_Data.add(listData);
                        }
                    } catch (DatabaseException e)
                    {
                        Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                myAdapter.notifyDataSetChanged();
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
    public void navigateToAllComment(UploadPostData uploadPostData)
    {

        ((MainActivity) requireContext()).displayFragmentWithBackstack(AllCommentsFragment.newInstance(uploadPostData));
    }
}
