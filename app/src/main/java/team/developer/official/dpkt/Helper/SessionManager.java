package team.developer.official.dpkt.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import team.developer.official.dpkt.LoginActivity;
import team.developer.official.dpkt.MainActivity;

/**
 * Created by Haydar Dzaky S on 8/14/2018.
 */

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int mode = 0;

    private static final String pref_name = "MyPref";
    private static final String is_login = "islogin";
    public static final String KEY_USER = "keyUser";
    public static final String KEY_PASS = "keyPass";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(pref_name, mode);
        editor = pref.edit();
    }

    public void createSession(String user, String pass){
        editor.putBoolean(is_login, true);
        editor.putString(KEY_USER, user);
        editor.putString(KEY_PASS, pass);
        editor.commit();
    }

    public void checkLogin(){
        if (!this.is_login()){
            //Intent i = new Intent(context, LoginActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(i);
        }else {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    private boolean is_login() {
        return pref.getBoolean(is_login, false);
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(pref_name, pref.getString(pref_name, null));
        user.put(KEY_USER, pref.getString(KEY_USER, null));
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));
        return user;
    }

}
