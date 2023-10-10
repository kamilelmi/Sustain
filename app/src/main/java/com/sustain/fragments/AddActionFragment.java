package com.sustain.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sustain.BaseActivity;
import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentAddActionBinding;
import com.sustain.model.User;
import com.sustain.session.Session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class AddActionFragment extends Fragment
{
    FragmentAddActionBinding binding;
    User userInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_action, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (userInfo == null) userInfo = new User();
        userInfo= (User) ((BaseActivity)requireActivity()).session().get(Session.LOGGED_IN_USER);
        binding.btnLogAction.setOnClickListener(view1 -> showDialog());

        binding.btnUploadArticle.setOnClickListener(view12 -> {
            if (userInfo.isPaid)
            {
                ((MainActivity) requireActivity()).displayFragmentWithBackstack(new UploadArticleFragment());
            } else
            {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());

                dialogBuilder.setTitle(R.string.title_article)
                        .setMessage(R.string.msg_paid)
                        .setNegativeButton("cancel", (dialog, which) -> {
                            dialogBuilder.setCancelable(true);
                        })
                        .setPositiveButton("subscribe", (dialog, which) -> {
                            ((MainActivity) requireActivity()).displayFragmentWithBackstack(new UploadArticleFragment());
                        }).show();
            }
        });
    }

    private void showDialog()
    {

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_bottom_sheet_dialog);

        LinearLayoutCompat foodAndDrinkContainer = dialog.findViewById(R.id.foodContainer);
        LinearLayoutCompat transportContainer = dialog.findViewById(R.id.transportContainer);
        LinearLayoutCompat waterContainer = dialog.findViewById(R.id.waterContainer);
        LinearLayoutCompat energyContainer = dialog.findViewById(R.id.energyContainer);
        LinearLayoutCompat recyclingContainer = dialog.findViewById(R.id.recyclingContainer);
        LinearLayoutCompat exerciseContainer = dialog.findViewById(R.id.exerciseContainer);
        foodAndDrinkContainer.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).displayFragmentWithBackstack(new FoodAndDrinkFragment());
            dialog.dismiss();
        });

        transportContainer.setOnClickListener(v -> {

            ((MainActivity) requireActivity()).displayFragmentWithBackstack(new TransportFragment());
            dialog.dismiss();

        });


        recyclingContainer.setOnClickListener(v -> {

            ((MainActivity) requireActivity()).displayFragmentWithBackstack(new RecyclingFragment());
            dialog.dismiss();
        });

        exerciseContainer.setOnClickListener(v -> {

            ((MainActivity) requireActivity()).displayFragmentWithBackstack(new ExerciseFragment());
            dialog.dismiss();

        });


        waterContainer.setOnClickListener(v -> {

            ((MainActivity) requireActivity()).displayFragmentWithBackstack(new WaterFragment());
            dialog.dismiss();
        });

        energyContainer.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).displayFragmentWithBackstack(new EnergyFragment());

            dialog.dismiss();

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}
