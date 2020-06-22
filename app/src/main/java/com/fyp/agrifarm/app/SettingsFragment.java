package com.fyp.agrifarm.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.utils.FirebaseUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    private OnPreferencesChangeListener listener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);

        Preference deletePreference = findPreference("delete");
        if (deletePreference != null) {
            deletePreference.setOnPreferenceClickListener(preference -> {
                FirebaseUtils.deleteAccount(o -> {
                            Toast.makeText(getContext(), "Account Deleted!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), LoginActivity.class));
                        },
                        e -> Toast.makeText(getContext(), "Account could not be deleted!", Toast.LENGTH_SHORT).show()
                );

                return true;
            });
        }

        Preference weather = findPreference("weatherUnit");
        weather.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                listener.onWeatherChanged(newValue.toString());
                return true;
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (OnPreferencesChangeListener) context;
    }

    public interface OnPreferencesChangeListener {
        void onWeatherChanged(String newValue);
    }


    

}
