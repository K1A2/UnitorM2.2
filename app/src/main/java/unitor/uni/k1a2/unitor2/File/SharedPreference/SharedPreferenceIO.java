package unitor.uni.k1a2.unitor2.File.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jckim on 2017-12-03.
 */

public class SharedPreferenceIO {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferenceIO(Context context, String repository) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(repository, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void setNewSharedPreference(String repository) {
        this.sharedPreferences = context.getSharedPreferences(repository, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public String getString(String name, String defaultV) {
        return sharedPreferences.getString(name, defaultV);
    }

    public boolean getBoolean(String name, boolean defaultV) {
        return sharedPreferences.getBoolean(name, defaultV);
    }

    public void setString(String name, String value) {
        editor.putString(name, value).commit();
    }

    public void setBoolean(String name, boolean value) {
        editor.putBoolean(name, value).commit();
    }
}
