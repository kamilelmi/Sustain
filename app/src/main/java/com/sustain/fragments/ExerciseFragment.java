package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentExerciseBinding;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


public class ExerciseFragment extends Fragment
{
    FragmentExerciseBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exercise, container, false);
        binding.gymContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_gym)));
        binding.caryBottleContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_cary_bottle)));
        binding.freeWeightContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_free_weight)));
        binding.walkContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_walk_walk)));

        return binding.getRoot();
    }

    private void navigateToLogActionFragment(String selectedCategory)
    {
        ((MainActivity) requireContext()).displayFragmentWithBackstack(LogActionFragment.newInstance(getString(R.string.title_exercise), selectedCategory));
    }
}