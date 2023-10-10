package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sustain.R;
import com.sustain.adapters.ArticleAdapter;
import com.sustain.databinding.FragmentMyArticlesBinding;
import com.sustain.model.UploadArticle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MyArticlesFragment extends Fragment
{
    FragmentMyArticlesBinding binding;

    private List<UploadArticle> articleList;

    ArticleAdapter adapter;

    DatabaseReference articleDataBaseReference;

    FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_articles, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        articleList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();

        adapter = new ArticleAdapter(requireContext(), articleList);
        binding.rvArticles.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvArticles.setHasFixedSize(true);
        binding.rvArticles.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvArticles.setAdapter(adapter);
        fetchArticle();
    }


    private void fetchArticle()
    {
        articleList.clear();

        articleDataBaseReference = FirebaseDatabase.getInstance().getReference("Article").child(auth.getUid());

        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {try
                {
                    UploadArticle list = ds.getValue(UploadArticle.class);
                    articleList.add(list);
                }catch (DatabaseException e)
                {
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        articleDataBaseReference.addListenerForSingleValueEvent(eventListener);
    }
}
