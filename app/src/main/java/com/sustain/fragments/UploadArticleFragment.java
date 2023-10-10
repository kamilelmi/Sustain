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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sustain.BaseActivity;
import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.FragmentUploadArticleBinding;
import com.sustain.model.UploadArticle;
import com.sustain.model.User;
import com.sustain.session.Session;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class UploadArticleFragment extends Fragment
{
    FragmentUploadArticleBinding binding;
    Uri uri;

    StorageReference storageReference;


    FirebaseAuth auth;

    String articleUri;

    String thumbnailUri;

    Boolean isThumbnailUploaded = false;

    String articleTitle;

    DatabaseReference userinfoRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_article, container, false);
        if (auth == null) auth = FirebaseAuth.getInstance();
        if (userinfoRef == null)
            userinfoRef = FirebaseDatabase.getInstance().getReference("user").child(auth.getUid()).child("userInfo").child("articleCount");

        binding.btnUploadArticle.setOnClickListener(view -> {
            articleTitle = binding.etTitle.getText().toString();

            Boolean validation = validation(false);
            if (!validation) return;

            openFile();
        });
        binding.btnUploadThumbnail.setOnClickListener(view -> {

            articleTitle = binding.etTitle.getText().toString();

            Boolean validation = validation(true);
            if (!validation) return;

            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
        binding.btnFb.setOnClickListener(view -> shareArticle());
        binding.btnWhatsapp.setOnClickListener(view -> shareArticle());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);




    }

    private void uploadArticle()
    {
        ((MainActivity) requireContext()).showLoaderDialog("Upload article...");
        storageReference = FirebaseStorage.getInstance().getReference("Article").child(uri.getLastPathSegment());

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete()) ;
            Uri urlImage = uriTask.getResult();
            articleUri = urlImage.toString();
            ((MainActivity) requireContext()).hideLoaderDialog();
            Toast.makeText(requireActivity(), "Article Uploaded Successful ", Toast.LENGTH_SHORT).show();
            uploadArticleData();
        }).addOnFailureListener(e -> {
            ((MainActivity) requireContext()).hideLoaderDialog();
            Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        });
    }

    private void uploadThumbnail(Uri uri)
    {

        ((MainActivity) requireContext()).showLoaderDialog("Upload Thumbnail...");
        storageReference = FirebaseStorage.getInstance().getReference("Article").child("Thumbnail").child(uri.getLastPathSegment());

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete()) ;
            Uri urlImage = uriTask.getResult();
            thumbnailUri = urlImage.toString();
            isThumbnailUploaded = true;
            ((MainActivity) requireContext()).hideLoaderDialog();
            Toast.makeText(requireActivity(), "Thumbnail Uploaded  Successful", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            ((MainActivity) requireContext()).hideLoaderDialog();
            Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        });
    }

    private void openFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        articlePickerIntent.launch(intent);

    }

    ActivityResultLauncher<Intent> articlePickerIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null)
                {
                    uri = result.getData().getData();

                    uploadArticle();
                }
            });

    private Boolean validation(Boolean isThumbnail)
    {
        if (articleTitle.isEmpty() || articleTitle.isBlank())
        {
            binding.etTitle.setError(getString(R.string.msg_article_empty));
            return false;
        } else if (!isThumbnailUploaded && !isThumbnail)
        {
            Toast.makeText(requireContext(), "Thumbnail can not be empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else
        {
            return true;
        }
    }


    ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null)
        {

            int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            requireContext().getContentResolver().takePersistableUriPermission(uri, flag);
            uploadThumbnail(uri);
        } else
        {
            Toast.makeText(requireActivity(), "Fail to upload due to some internal error", Toast.LENGTH_SHORT).show();
        }
    });

    private void uploadArticleData()
    {
        UploadArticle uploadArticle = new UploadArticle(articleTitle, articleUri, thumbnailUri);


        DatabaseReference uploadArticleRef = FirebaseDatabase.getInstance().getReference("Article").child(auth.getUid()).child(articleTitle);

        ((MainActivity) requireActivity()).showLoaderDialog("Post uploading..");

        uploadArticleRef.setValue(uploadArticle).addOnSuccessListener(aVoid -> {

            uploadArticleRef.setValue(uploadArticle);

            User user = (User) ((MainActivity) requireActivity()).session().get(Session.LOGGED_IN_USER);
            user.articleCount = user.articleCount + 1;
            ((MainActivity) requireActivity()).session().add(Session.LOGGED_IN_USER, user);
            userinfoRef.setValue(user.articleCount);

            ((BaseActivity) requireActivity()).hideLoaderDialog();
            Toast.makeText(requireActivity(), "Article Uploaded Successful", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        });


    }

    private void shareArticle()
    {
        String data = "Title: " + articleTitle  + "\n" + "Link to article: " + articleUri;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Article");
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("*/*");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

}
