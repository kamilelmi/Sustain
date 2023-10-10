package com.sustain.session;

import com.google.firebase.auth.FirebaseAuth;
import com.sustain.model.User;

import java.util.HashMap;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 07/05/2023
 */
public class Session
{
    public static final String LOGGED_IN_USER = "logged_in_user";

    HashMap<String, User> cookies = new HashMap<>();

    public void add(String key, User value)
    {
        if (cookies != null && key != null && value != null)
        {
            cookies.put(key, value);
        }
    }

    public Object get(String key)
    {
        if (cookies == null) return null;
        else return cookies.get(key);
    }

    public void end()
    {
        cookies.clear();
        cookies = null;
    }

    public Boolean isActive()
    {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
}
