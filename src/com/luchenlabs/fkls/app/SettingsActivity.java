package com.luchenlabs.fkls.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.luchenlabs.fkls.R;
import com.luchenlabs.fkls.app.storage.DropboxCoreNook;
import com.luchenlabs.fkls.core.C;

/**
 * App settings
 *
 * @author cheezmeister
 */
public class SettingsActivity extends Activity implements OnSharedPreferenceChangeListener {
    private static final String _DROPBOX_AUTH_IN_PROGRESS = "_dropboxAuthInProgress"; //$NON-NLS-1$
    private boolean _dropboxAuthInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        _dropboxAuthInProgress = savedInstanceState.getBoolean(_DROPBOX_AUTH_IN_PROGRESS);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_dropboxAuthInProgress) {
            DropboxCoreNook.Session.getInstance().completeAuth(this);
            _dropboxAuthInProgress = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(_DROPBOX_AUTH_IN_PROGRESS, _dropboxAuthInProgress);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(C.KEY_PREF_DROPBOX_SYNC) && sharedPreferences.getBoolean(key, false)) {
            _dropboxAuthInProgress = true;
            DropboxCoreNook.Session.getInstance().initialize(this);
        }
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }
    }

}
