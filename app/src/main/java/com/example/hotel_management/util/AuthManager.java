package com.example.hotel_management.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "MuziAuth";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public AuthManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        
        // Add default admin account if not exists
        if (!pref.contains("user_pass_admin@gmail.com")) {
            editor.putString("user_pass_admin@gmail.com", "admin123");
            editor.apply();
        }
    }

    public boolean register(String email, String password) {
        if (pref.contains("user_pass_" + email)) {
            return false; // User already exists
        }
        editor.putString("user_pass_" + email, password);
        // Also save profile info if needed in future
        editor.commit();
        login(email, password); // Auto login
        return true;
    }

    public boolean login(String email, String password) {
        String storedPassword = pref.getString("user_pass_" + email, null);
        if (storedPassword != null && storedPassword.equals(password)) {
            setLoginSession(true, email);
            return true;
        }
        return false;
    }

    private void setLoginSession(boolean isLoggedIn, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USER_EMAIL, email);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }
    
    public void logout() {
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_EMAIL);
        editor.commit();
    }
}
