package unitor.uni.k1a2.unitor2.activitys.SharedPreference;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.activitys.MainActivity;

/**
 * Created by jckim on 2017-12-03.
 */

public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    private SwitchPreference switch_display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);

        switch_display = (SwitchPreference)findPreference("setting_screen");
        switch_display.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("setting_screen")) {

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }
}
