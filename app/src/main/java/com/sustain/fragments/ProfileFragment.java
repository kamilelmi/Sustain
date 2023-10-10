package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentProfileBinding;
import com.sustain.model.User;
import com.sustain.session.Session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment
{
    FragmentProfileBinding binding;
    User userInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        userInfo = new User();

        userInfo = (User) ((MainActivity) requireActivity()).session().get(Session.LOGGED_IN_USER);
        binding.txtActionLogCount.setText(String.valueOf(userInfo.logActionCount));
        binding.txtArticleCount.setText(String.valueOf(userInfo.articleCount));
        binding.txtUserName.setText(userInfo.userName);
        binding.tvFollowersCount.setText(String.valueOf(userInfo.followerCount));
        binding.tvFollowingCount.setText(String.valueOf(userInfo.followingCount));
        binding.txtChallengesCount.setText(String.valueOf(userInfo.challengesCount));
        binding.invitationCount.setText(String.valueOf(userInfo.invitationCount));

        binding.rlArticles.setOnClickListener(view12 -> ((MainActivity) requireActivity()).displayFragmentWithBackstack(new MyArticlesFragment()));
        binding.rlInvitation.setOnClickListener(view1 -> ((MainActivity) requireActivity()).displayFragmentWithBackstack(new InvitationFragment()));
    }
}
