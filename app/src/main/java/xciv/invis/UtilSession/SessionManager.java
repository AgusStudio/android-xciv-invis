package xciv.invis.UtilSession;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import xciv.invis.LoginActivity;

/**
 * Created by Betha on 8/22/2018.
 */

public class SessionManager {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public static final String KEY_LOGIN = "login_id";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_NAME = "user_name";
    public static final String KEY_API = "url_api";
    public static final String KEY_METODE = "metode";
    public static final String is_login = "loginstatus";
    private final String SHARE_NAME = "loginsession";
    private final int MODE_PRIVATE = 0;
    private Context _context;

    public SessionManager(Context context)
    {
        this._context = context;
        sp = _context.getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        editor = sp.edit();
    }

    public void checkLogin()
    {
        if(!this.Login())
        {
            Intent login = new Intent(_context, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(login);
        }
    }

    public Boolean Login()
    {
        return sp.getBoolean(is_login,false);
    }

    public void logOut()
    {
        editor.clear();
        editor.commit();
    }

    public void storeTokenLogin(String user_token)
    {
        editor.putBoolean(is_login,true);
        editor.putString(KEY_TOKEN, user_token);
        editor.commit();
    }

    public HashMap getTokenLogin()
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_TOKEN, sp.getString(KEY_TOKEN,null));
        return map;
    }

    public void storeUserInfo(String login_id,String user_name)
    {
        editor.putString(KEY_NAME, user_name);
        editor.putString(KEY_LOGIN, login_id);
        editor.commit();
    }

    public HashMap getUserInfo()
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_NAME, sp.getString(KEY_NAME,null));
        map.put(KEY_LOGIN, sp.getString(KEY_LOGIN,null));
        return map;
    }

    public void storeConfig(String url_api,String metode)
    {
        editor.putString(KEY_API, url_api);
        editor.putString(KEY_METODE, metode);
        editor.commit();
    }

    public HashMap getConfig()
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_API, sp.getString(KEY_API,null));
        map.put(KEY_METODE, sp.getString(KEY_METODE,null));
        return map;
    }


}
