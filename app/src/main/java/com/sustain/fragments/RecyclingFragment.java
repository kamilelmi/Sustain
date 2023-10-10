package com.sustain.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentRecyclingBinding;

public class RecyclingFragment extends Fragment
{

    FragmentRecyclingBinding binding;
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycling, container, false);
        binding.otherContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_other)));
        binding.cardBoardContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_cardboard)));
        binding.paperContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_paper)));
        binding.glassContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_glass)));
        binding.batteriseContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_batteries)));
        binding.foodAndDrinkContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_food_and_drink)));
        binding.metalContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_metal)));
        binding.waterBottleContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_plastic)));
        binding.electronicContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_electronic)));


        return binding.getRoot();
    }

    private void navigateToLogActionFragment(String selectedCategory)
    {
        ((MainActivity) requireContext()).displayFragmentWithBackstack(LogActionFragment.newInstance(getString(R.string.title_recycling
        ), selectedCategory));
    }
}