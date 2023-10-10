package com.sustain.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sustain.BaseActivity;
import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentLogActionBinding;
import com.sustain.model.Invitation;
import com.sustain.model.UploadPostData;
import com.sustain.model.User;
import com.sustain.session.Session;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class LogActionFragment extends Fragment
{
    FragmentLogActionBinding binding;
    StorageReference storageReference;
    DatabaseReference updateLeaderBoard;
    DatabaseReference userinfoRef;

    User user;


    ValueEventListener postListener;
    String description;

    Uri uri;

    private static final String SELECTED_CATEGORY = "param1";
    private static final String SELECTED_SUB_CATEGORY = "param2";

    private String selectedCategory;
    private String selectedSubCategory;

    private String imageUri;

    int actionCount;

    FirebaseAuth auth;


    public static LogActionFragment newInstance(String selectedCategory, String selectedSubCategory)
    {
        LogActionFragment fragment = new LogActionFragment();
        Bundle args = new Bundle();
        args.putString(SELECTED_CATEGORY, selectedCategory);
        args.putString(SELECTED_SUB_CATEGORY, selectedSubCategory);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log_action, container, false);

        if (auth == null) auth = FirebaseAuth.getInstance();
        if (userinfoRef == null) userinfoRef = FirebaseDatabase.getInstance().getReference("user").child(auth.getUid()).child("userInfo").child("logActionCount");

        if (user == null)
        {
            user = new User();
            user = (User) ((BaseActivity) requireActivity()).session().get(Session.LOGGED_IN_USER);
        }
        binding.btnTimePeriod.setOnClickListener(view -> saveDataInDataBase(false));

        binding.btnUploadPicture.setOnClickListener(view -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        binding.btnFeed.setOnClickListener(view -> saveDataInDataBase(true));
        binding.btnFb.setOnClickListener(view -> sharePost());
        binding.btnWhatsapp.setOnClickListener(view -> sharePost());
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            selectedCategory = getArguments().getString(SELECTED_CATEGORY);
            selectedSubCategory = getArguments().getString(SELECTED_SUB_CATEGORY);
        }
    }

    private void saveDataInDataBase(Boolean displayOnFeed)
    {
        description = binding.etDesc.getText().toString();


        if (description.isEmpty() && !displayOnFeed)
        {
            binding.etDesc.setError(getString(R.string.msg_des_empty));
            return;
        }
        String currentDate_time = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        DatabaseReference uploadPostRef = FirebaseDatabase.getInstance().getReference("Post").child(auth.getUid()).child(currentDate_time);
        UploadPostData uploadPostData = new UploadPostData(selectedCategory, selectedSubCategory, imageUri, description, displayOnFeed, "0", "0", user.userName);

        ((MainActivity) requireActivity()).showLoaderDialog("Post uploading..");

        uploadPostRef.setValue(uploadPostData).addOnSuccessListener(aVoid -> {
                    uploadPostRef.setValue(uploadPostData);
                    ((BaseActivity) requireActivity()).hideLoaderDialog();
                    Toast.makeText(requireActivity(), "Post Uploaded", Toast.LENGTH_SHORT).show();
                    binding.sharePostContainer.setVisibility(View.VISIBLE);
                    updateLeaderBoard();

                    User user = (User) ((MainActivity) requireActivity()).session().get(Session.LOGGED_IN_USER);
                    user.logActionCount = user.logActionCount + 1;
                    ((MainActivity) requireActivity()).session().add(Session.LOGGED_IN_USER, user);
                    userinfoRef.setValue(user.logActionCount);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                });
    }

    private void uploadImage()
    {
        ((MainActivity) requireContext()).showLoaderDialog("Upload image...");
        storageReference = FirebaseStorage.getInstance().getReference("Post").child(uri.getLastPathSegment());

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete()) ;
            Uri urlImage = uriTask.getResult();
            imageUri = urlImage.toString();
            ((MainActivity) requireContext()).hideLoaderDialog();
            Toast.makeText(requireActivity(), "Image Uploaded ", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            ((MainActivity) requireContext()).hideLoaderDialog();
            Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        });
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null)
        {

            int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            requireContext().getContentResolver().takePersistableUriPermission(uri, flag);
            this.uri = uri;
            uploadImage();
        } else
        {
            Toast.makeText(requireActivity(), "Fail to upload due to some internal error", Toast.LENGTH_SHORT).show();
        }
    });

    private void sharePost()
    {
        String data = selectedCategory + "\n" + selectedSubCategory + "\n" + description;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, selectedCategory);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("*/*");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void updateLeaderBoard()
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm");
        Date date = new Date();
        Timestamp startTimestamp = new Timestamp(date.getTime());
        String currentTime = timeFormat.format(startTimestamp);


        DatabaseReference fetchChallenges = FirebaseDatabase.getInstance().getReference("user").child(auth.getUid()).child("invitations");

        fetchChallenges.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                try
                {
                    Invitation listData = snapshot.getValue(Invitation.class);
                    assert listData != null;
                    listData.setKey(snapshot.getKey());
                    if (selectedCategory.equals(listData.challengeData.category))
                    {
                        if (Objects.requireNonNull(timeFormat.parse(currentTime)).before(timeFormat.parse(listData.challengeData.endTime)))
                        {
                            updateLeaderBoard = FirebaseDatabase.getInstance().getReference("leaderBoard").child(listData.getKey()).child(auth.getUid());
                            updateLeaderBoard();
                        }
                    }

                } catch (DatabaseException e)
                {
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } catch (ParseException e)
                {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
