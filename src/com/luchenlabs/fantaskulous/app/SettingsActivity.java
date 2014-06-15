package com.luchenlabs.fantaskulous.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.dropbox.sync.android.DbxAccountManager;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.core.C;

/**
 * App settings
 * 
 * @author cheezmeister
 */
public class SettingsActivity extends Activity implements OnSharedPreferenceChangeListener {
    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }
    }

    public static final String KEY_PREF_DROPBOX_SYNC = "prefEnableDropboxSync"; //$NON-NLS-1$

    private static final int CODE_DBX_LINK_ACCOUNT = 42;

    protected static void ospc(SharedPreferences sharedPreferences, String key, Activity activity) {
        if (key.equals(KEY_PREF_DROPBOX_SYNC) && sharedPreferences.getBoolean(key, false)) {
            DbxAccountManager dbxAcctMgr = DbxAccountManager.getInstance(activity.getApplicationContext(),
                    C.DBX_APP_KEY,
                    C.DBX_APP_SECRET);
            if (!dbxAcctMgr.hasLinkedAccount()) {
                dbxAcctMgr.startLink(activity, CODE_DBX_LINK_ACCOUNT);
            }
        }
    }

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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ospc(sharedPreferences, key, this);
    }

}
