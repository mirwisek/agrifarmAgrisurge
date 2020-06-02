package com.fyp.agrifarm.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.utils.FirebaseUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);

        Preference deletePreference = findPreference("delete");
        deletePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FirebaseUtils.deleteAccount(o -> {
                            Toast.makeText(getContext(), "Account Deleted!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), UserRegistrationActivity.class));
                        },
                        e -> Toast.makeText(getContext(), "Account could not be deleted!", Toast.LENGTH_SHORT).show()
                );

                return true;
            }
        });

    }

}
