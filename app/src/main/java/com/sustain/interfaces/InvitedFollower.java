package com.sustain.interfaces;

import android.os.Parcelable;

import com.sustain.model.Follower;

import java.util.List;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 05/05/2023
 */
public interface InvitedFollower extends Parcelable
{
    void invitedFollowerList(List<Follower> invitedFollowerList);
}
