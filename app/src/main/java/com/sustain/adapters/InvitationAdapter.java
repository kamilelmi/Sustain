package com.sustain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sustain.BaseActivity;
import com.sustain.R;
import com.sustain.databinding.LiInvitationsBinding;
import com.sustain.model.Invitation;
import com.sustain.model.User;
import com.sustain.session.Session;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 06/05/2023
 */
public class InvitationAdapter extends RecyclerView.Adapter<InvitationsViewHolder>
{
    private Context context;

    private BaseActivity activity;
    private List<Invitation> list;

    int actionCount;


    User userInfo;

    DatabaseReference challengeInvitationRef;
    DatabaseReference challengeRef;
    DatabaseReference leaderBoardRef;

    DatabaseReference userInfoRef;

    ValueEventListener postListener;


    FirebaseAuth auth;

    public InvitationAdapter(Context context, List<Invitation> list, BaseActivity activity)
    {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public InvitationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LiInvitationsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.li_invitations, parent, false);
        return new InvitationsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationsViewHolder holder, int position)
    {
        if (auth == null) auth = FirebaseAuth.getInstance();
        if (userInfo == null) userInfoRef = FirebaseDatabase.getInstance().getReference("user");
        if (userInfo == null) userInfo = new User();
        userInfo = (User) activity.session().get(Session.LOGGED_IN_USER);

        holder.binding.tvChallengeDesc.setText(list.get(position).challengeData.description);
        holder.binding.tvChallengeTitle.setText(list.get(position).challengeData.title);
        holder.binding.btnAccept.setOnClickListener(view -> acceptChallenge(position));
        holder.binding.btnReject.setOnClickListener(view -> rejectChallenge(position));
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    private void acceptChallenge(int position)
    {
        if (challengeInvitationRef == null)
            challengeInvitationRef = FirebaseDatabase.getInstance().getReference("user");
        challengeInvitationRef.child(auth.getUid()).child("invitations").child(list.get(position).getKey()).child("isAccepted").setValue(true);
        decreaseInvitationCount();
        updateActionCount();
        Toast.makeText(context, "Challenge Accepted Successful", Toast.LENGTH_SHORT).show();
    }

    private void rejectChallenge(int position)
    {
        if (challengeRef == null) challengeRef = FirebaseDatabase.getInstance().getReference("challenges");
        if (challengeInvitationRef == null) challengeInvitationRef = FirebaseDatabase.getInstance().getReference("user");
        if (leaderBoardRef == null) leaderBoardRef = FirebaseDatabase.getInstance().getReference("leaderBoard");

        challengeInvitationRef.child(auth.getUid()).child("invitations").child(list.get(position).getKey()).removeValue();
        challengeRef.child(list.get(position).getKey()).child("participants").child(auth.getUid()).removeValue();
        leaderBoardRef.child(list.get(position).getKey()).child(auth.getUid()).removeValue();
        decreaseInvitationCount();
        Toast.makeText(context, "Challenge Rejected !", Toast.LENGTH_SHORT).show();

    }

    private void decreaseInvitationCount()
    {
        if (userInfo.invitationCount > 0)
        {
            userInfo.invitationCount = userInfo.invitationCount - 1;
            activity.session().add(Session.LOGGED_IN_USER, userInfo);
            userInfoRef.child(auth.getUid()).child("userInfo").child("invitationCount").setValue(userInfo.invitationCount);

        }
    }

    private void updateActionCount()
    {
        userInfoRef.child(auth.getUid()).child("userInfo").child("challengesCount").setValue(userInfo.challengesCount + 1);
    }
}

class InvitationsViewHolder extends RecyclerView.ViewHolder
{
    LiInvitationsBinding binding;

    public InvitationsViewHolder(@NonNull LiInvitationsBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }
}