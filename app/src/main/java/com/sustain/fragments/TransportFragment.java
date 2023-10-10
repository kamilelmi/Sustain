package com.sustain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentTransportBinding;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class TransportFragment extends Fragment
{

    FragmentTransportBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transport, container, false);
        binding.caryTownContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_cycle)));
        binding.planJourneyContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_plan)));
        binding.shareRideContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_share_ride)));
        binding.walkToWorkContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_walk)));
        binding.otherContainer.setOnClickListener(view -> navigateToLogActionFragment(getString(R.string.title_other)));

        return binding.getRoot();
    }

    private void navigateToLogActionFragment(String selectedCategory)
    {
        ((MainActivity) requireContext()).displayFragmentWithBackstack(LogActionFragment.newInstance(getString(R.string.title_transport), selectedCategory));
    }
}