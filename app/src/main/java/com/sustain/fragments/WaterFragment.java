package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentWaterBinding;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


public class WaterFragment extends Fragment
{
    FragmentWaterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_water, container, false);
        binding.otherContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_other)));
        binding.buyBarrelContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_buy_barrel)));
        binding.makeQuickContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_make_quick)));
        binding.saveFlushContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_save_flush)));
        binding.turnKnobContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_turn_knob)));

        return binding.getRoot();
    }

    private void navigateToLogActionFragment(String selectedCategory)
    {
        ((MainActivity) requireContext()).displayFragmentWithBackstack(LogActionFragment.newInstance(getString(R.string.title_water), selectedCategory));
    }
}