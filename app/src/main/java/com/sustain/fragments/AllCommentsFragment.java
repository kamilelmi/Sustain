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
import com.google.firebase.database.ValueEventListener;
import com.sustain.BaseActivity;
import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.adapters.AllCommentsAdapter;
import com.sustain.databinding.FragmentAllCommentsBinding;
import com.sustain.model.Comment;
import com.sustain.model.UploadPostData;
import com.sustain.model.User;
import com.sustain.session.Session;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class AllCommentsFragment extends Fragment
{
    private static final String POST_DATA = "param1";

    private Boolean isCommentDataExist = false;

    private UploadPostData postData;
    FragmentAllCommentsBinding binding;

    FirebaseAuth auth;

    DatabaseReference uploadCommentReference;
    DatabaseReference fetchCommentReference;

    DatabaseReference updateCommentCountReference;


    List<Comment> listData;

    AllCommentsAdapter adapter;

    Comment commentData;

    public static AllCommentsFragment newInstance(UploadPostData uploadPostData)
    {
        AllCommentsFragment fragment = new AllCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(POST_DATA, uploadPostData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            postData = (UploadPostData) getArguments().getSerializable(POST_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_comments, container, false);

        auth = FirebaseAuth.getInstance();
        listData = new ArrayList<>();

        binding.rvComment.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvComment.setHasFixedSize(true);

        adapter = new AllCommentsAdapter(requireContext(), listData);
        binding.rvComment.setAdapter(adapter);
        binding.btnComment.setOnClickListener(view -> uploadComment());
        getCommentData();
        return binding.getRoot();
    }

    private void uploadComment()
    {
        String comment = binding.edtAddComment.getText().toString();

        ((BaseActivity) requireActivity()).showLoaderDialog("");
        Boolean validate = validation(comment);

        if (!validate) return;
        String currentDate_time = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        commentData = new Comment(comment, auth.getCurrentUser().getDisplayName());

        uploadCommentReference = FirebaseDatabase.getInstance().getReference("Post").child(postData.getAuthKey()).child(postData.getKey()).child("Comments").child(auth.getUid()).child(currentDate_time);
        updateCommentCountReference = FirebaseDatabase.getInstance().getReference("Post");

        uploadCommentReference.setValue(commentData).addOnSuccessListener(aVoid -> {
                    ((MainActivity) requireActivity()).hideLoaderDialog();
                    int commentCount = 1 + Integer.parseInt(postData.commentCount);
                    updateCommentCountReference.child(postData.authKey).child(postData.getKey()).child("commentCount").setValue(Integer.toString(commentCount));
                    getCommentData();

                    isCommentDataExist = true;
                    resetUi();
                    Toast.makeText(requireActivity(), "Success", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    ((BaseActivity) requireActivity()).hideLoaderDialog();
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }

    private void getCommentData()
    {

        ((BaseActivity) requireActivity()).showLoaderDialog("");
        listData.clear();

        fetchCommentReference = FirebaseDatabase.getInstance().getReference("Post").child(postData.getAuthKey()).child(postData.getKey()).child("Comments");
        fetchCommentReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                for (DataSnapshot post : snapshot.getChildren())
                {
                    try
                    {
                        ((BaseActivity) requireActivity()).hideLoaderDialog();
                        Comment comment = post.getValue(Comment.class);
                        listData.add(comment);

                    } catch (DatabaseException e)
                    {
                        Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }
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
                isCommentDataExist = false;
                ((BaseActivity) requireActivity()).hideLoaderDialog();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (!isCommentDataExist)
        {
            ((BaseActivity) requireActivity()).hideLoaderDialog();
            // Toast.makeText(getContext(), "Data not found", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validation(String comment)
    {
        if (comment.isBlank() || comment.isEmpty())
        {
            binding.edtAddComment.setError(getString(R.string.msg_comment_empty));
            return false;
        } else
        {
            return true;
        }
    }

    private void resetUi()
    {
        binding.edtAddComment.getText().clear();
    }
}