package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentFoodAndDrinkBinding;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class FoodAndDrinkFragment extends Fragment
{
    FragmentFoodAndDrinkBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_food_and_drink, container, false);
        binding.otherContainer.setOnClickListener(view1 -> navigateToLogActionFragment(getString(R.string.title_other)));
        binding.meatlessContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_meatless)));
        binding.caryContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_cary)));
        binding.paperContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_paper_vs_plastic)));
        binding.sourceContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_source)));
        binding.otherContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_other)));
        return binding.getRoot();
    }

    private void navigateToLogActionFragment(String selectedCategory)
    {
        ((MainActivity) requireContext()).displayFragmentWithBackstack(LogActionFragment.newInstance(getString(R.string.title_food_and_drink), selectedCategory));
    }
}