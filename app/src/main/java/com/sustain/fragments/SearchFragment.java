package com.sustain.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.sustain.adapters.SearchAdapter;
import com.sustain.databinding.FragmentSearchBinding;
import com.sustain.model.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class SearchFragment extends Fragment
{
    FragmentSearchBinding binding;

    List<User> userList;

    SearchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        binding.rvFeed.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFeed.setHasFixedSize(true);
        userList = new ArrayList<>();

        ((MainActivity) requireActivity()).setSearchOptionVisibility(false);
        adapter = new SearchAdapter(requireContext(), userList, (BaseActivity) requireActivity());
        binding.rvFeed.setAdapter(adapter);
        getUsers();


        binding.edtSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                filter(editable.toString());
            }
        });

        return binding.getRoot();
    }

    private void getUsers()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user");
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

                        User userInfoData = post.getValue(User.class);
                        assert userInfoData != null;
                        userInfoData.setKey(post.getKey());
                        userInfoData.setAuthKey(authKey);
                        if (userInfoData.userName != null)
                        {
                            userList.add(userInfoData);
                        }
                    } catch (DatabaseException e)
                    {
                        Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

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

    private void filter(String toString)
    {
        List<User> updatedUserList = new ArrayList<>();

        if (toString.isEmpty())
        {
            updatedUserList.clear();

        } else if (!toString.isEmpty())
        {

            for (User user : userList)
            {
                if (user.userName.toLowerCase().contains(toString.toLowerCase()))
                {
                    updatedUserList.add(user);
                }

            }

            if (!updatedUserList.isEmpty())
            {
                adapter.filterList(updatedUserList);
                binding.rvFeed.setAdapter(adapter);
            }
            if (updatedUserList.isEmpty())
            {
                Toast.makeText(requireActivity(), "User not found ", Toast.LENGTH_LONG).show();
            }


        }


    }

}