package com.sustain.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentEnergyBinding;

public class EnergyFragment extends Fragment
{

    FragmentEnergyBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_energy, container, false);
        binding.otherContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_other)));
        binding.washColdContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_wash_cold)));
        binding.smarterContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_smarter)));
        binding.switchOffContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_switch_off)));
        binding.readInsteadContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_read_instead)));

        return binding.getRoot();
    }

    private void navigateToLogActionFragment(String selectedCategory)
    {
        ((MainActivity) requireContext()).displayFragmentWithBackstack(LogActionFragment.newInstance(getString(R.string.title_energy), selectedCategory));
    }
}