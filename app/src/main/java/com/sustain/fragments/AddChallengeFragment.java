package com.sustain.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sustain.BaseActivity;
import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentAddChallengeBinding;
import com.sustain.interfaces.InvitedFollower;
import com.sustain.model.AddChallenges;
import com.sustain.model.Follower;
import com.sustain.model.Invitation;
import com.sustain.model.LeaderBoard;
import com.sustain.model.User;
import com.sustain.session.Session;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

@SuppressLint("ParcelCreator")
public class AddChallengeFragment extends Fragment implements InvitedFollower
{
    FragmentAddChallengeBinding binding;
    String startDateTime = "";
    String endDateTime = "";
    String title;
    String description;

    int challengesCount;

    String selectedCategory = "";

    FirebaseAuth auth;

    Calendar selectedEndDate;


    AddChallenges addChallengesData;


    ValueEventListener userInfoListener;

    List<Follower> invitedFollowerList;

    User userInfo;

    DatabaseReference userInfoRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_challenge, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (auth == null) auth = FirebaseAuth.getInstance();
        if (userInfo == null) userInfo = new User();
        if (userInfoRef == null) userInfoRef = FirebaseDatabase.getInstance().getReference("user");

        binding.btnInvite.setOnClickListener(view1 -> ((MainActivity) requireActivity()).displayFragmentWithBackstack(InviteFollowersFragment.newInstance(this)));
        if (invitedFollowerList == null) invitedFollowerList = new ArrayList<>();

        binding.btnStart.setOnClickListener(view1 ->
        {
            description = binding.etDesc.getText().toString();
            title = binding.etTitle.getText().toString();

            Boolean validation = validation(description, title, endDateTime);
            if (!validation) return;
            uploadChallenge();
        });

        binding.btnCategory.setOnClickListener(view1 -> showDialog());

        binding.btnTimePeriod.setOnClickListener(view1 -> getStartAndEndTime());
    }

    private Boolean validation(String description, String title, String timePeriod)
    {
        if (description.isEmpty() || description.isBlank())
        {
            binding.etDesc.setError(getString(R.string.msg_des_empty));
            return false;
        } else if (title.isBlank() || title.isEmpty())
        {
            binding.etTitle.setError(getString(R.string.msg_title_empty));
            return false;
        } else if (timePeriod.isEmpty() || timePeriod.isBlank())
        {
            Toast.makeText(requireContext(), getString(R.string.msg_time_range_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (invitedFollowerList.isEmpty() || invitedFollowerList == null)
        {
            Toast.makeText(requireContext(), getString(R.string.msg_invite_follower), Toast.LENGTH_SHORT).show();
            return false;
        } else if (selectedCategory.isEmpty() || selectedCategory == null)
        {
            Toast.makeText(requireContext(), getString(R.string.msg_category_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else
        {
            return true;
        }
    }

    private void uploadChallenge()
    {
        startDateTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        DatabaseReference addChallengeRef = FirebaseDatabase.getInstance().getReference("challenges");


        addChallengesData = new AddChallenges(title, description, startDateTime, endDateTime, auth.getUid(), selectedCategory);
        String challengeId = addChallengeRef.push().getKey();

        ((MainActivity) requireActivity()).showLoaderDialog("Challenge uploading..");

        addChallengeRef.child(challengeId).setValue(addChallengesData).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        sendInvitation(addChallengesData, challengeId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                });
    }

    private void sendInvitation(AddChallenges addChallenges, String challengeId)
    {
        DatabaseReference invitationRef = FirebaseDatabase.getInstance().getReference("user");
        DatabaseReference challengeCountRef = FirebaseDatabase.getInstance().getReference("user");

        Invitation invitation = new Invitation(false, addChallenges);

        for (int a = 0; a < invitedFollowerList.size(); a++)
        {
            invitationRef.child(invitedFollowerList.get(a).uid).child("invitations").child(challengeId).setValue(invitation);
            challengeCountRef.child(auth.getUid()).child("userInfo").child("follower").child(invitedFollowerList.get(a).uid).child("invitationCount").setValue(invitedFollowerList.get(a).invitationCount+1);
            challengeCountRef.child(invitedFollowerList.get(a).uid).child("userInfo").child("invitationCount").setValue(invitedFollowerList.get(a).invitationCount+1);
        }
        addParticipants(challengeId);
    }

    private void addParticipants(String challengeId)
    {
        DatabaseReference participant = FirebaseDatabase.getInstance().getReference("challenges").child(challengeId).child("participants");
        DatabaseReference leaderBoard = FirebaseDatabase.getInstance().getReference("leaderBoard").child(challengeId);
        for (int i = 0; i < invitedFollowerList.size(); i++)
        {
            participant.child(invitedFollowerList.get(i).uid).setValue(invitedFollowerList.get(i).userName);
            LeaderBoard leaderBoardData = new LeaderBoard(0, invitedFollowerList.get(i).userName);
            leaderBoard.child(invitedFollowerList.get(i).uid).setValue(leaderBoardData);
        }
        Toast.makeText(requireActivity(), "Challenge Uploaded", Toast.LENGTH_SHORT).show();
        ((BaseActivity) requireActivity()).hideLoaderDialog();
    }

    private void getStartAndEndTime()
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm");

        Date date = new Date();
        Timestamp startTimestamp = new Timestamp(date.getTime());
        startDateTime = timeFormat.format(startTimestamp);

        // Get Current Date
        if (selectedEndDate == null) selectedEndDate = Calendar.getInstance();
        int mYear = selectedEndDate.get(Calendar.YEAR);
        int mMonth = selectedEndDate.get(Calendar.MONTH);
        int mDay = selectedEndDate.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {

                    selectedEndDate.set(Calendar.YEAR, year);
                    selectedEndDate.set(Calendar.MONTH, monthOfYear + 1);
                    selectedEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    selectedEndDate.set(Calendar.HOUR_OF_DAY, 0);
                    selectedEndDate.set(Calendar.MINUTE, 0);
                    selectedEndDate.set(Calendar.SECOND, 0);

                    endDateTime = timeFormat.format(selectedEndDate.getTime());
                    timePicker();
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void timePicker()
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm");

        // Get Current Time

        int mHour = selectedEndDate.get(Calendar.HOUR_OF_DAY);
        int mMinute = selectedEndDate.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedEndDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedEndDate.set(Calendar.MINUTE, minute);
                    endDateTime = timeFormat.format(selectedEndDate.getTime());

                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void invitedFollowerList(List<Follower> invitedFollowerList)
    {
        this.invitedFollowerList = invitedFollowerList;
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
            selectedCategory = getString(R.string.title_food_and_drink);
            dialog.dismiss();
        });

        transportContainer.setOnClickListener(v -> {
            selectedCategory = getString(R.string.title_transport);
            dialog.dismiss();

        });


        recyclingContainer.setOnClickListener(v -> {
            selectedCategory = getString(R.string.title_recycling);
            dialog.dismiss();
        });

        exerciseContainer.setOnClickListener(v -> {
            selectedCategory = getString(R.string.title_exercise);
            dialog.dismiss();

        });


        waterContainer.setOnClickListener(v -> {
            selectedCategory = getString(R.string.title_water);
            dialog.dismiss();
        });

        energyContainer.setOnClickListener(v -> {
            selectedCategory = getString(R.string.title_energy);
            dialog.dismiss();

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void increaseChallengesCount(String authId)
    {
        userInfoListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                challengesCount = dataSnapshot.child(authId).child("userInfo").child("challengesCount").getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // Getting Post failed, log a message
                return;
            }
        };

        userInfo = (User) ((BaseActivity) requireActivity()).session().get(Session.LOGGED_IN_USER);
        userInfo.challengesCount = userInfo.challengesCount + 1;
        userInfo.invitationCount = userInfo.invitationCount + 1;
        ((BaseActivity) requireActivity()).session().add(Session.LOGGED_IN_USER, userInfo);
        userInfoRef.child(auth.getUid()).child("userInfo").child("challengesCount").setValue(userInfo.challengesCount);
        userInfoRef.child(auth.getUid()).child("userInfo").child("invitationCount").setValue(userInfo.invitationCount);
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i)
    {

    }
}
