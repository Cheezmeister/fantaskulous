package com.luchenlabs.fantaskulous.app.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AppKeyPair;
import com.luchenlabs.fantaskulous.app.MainActivity;
import com.luchenlabs.fantaskulous.core.C;

public class DropboxCoreNook implements NookOrCranny {

    private final String _filename;

    private final Context _context;

    public DropboxCoreNook(Context context, String filename) {
        this._filename = filename;
        this._context = context;
    }

    @Override
    public void begAndPleadToBloodyUpdateTheDamnFile() {
        // TODO Auto-generated method stub

    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    @Override
    public InputStream fetchMeAnInputStream() {
        Session instance = Session.getInstance();
        instance.initialize(_context, true);
        DropboxAPI<AndroidAuthSession> api = instance.getAPI();

        if (api == null)
            return null;

        InputStream stream = null;
        try {
            stream = api.getFileStream(_filename, null);
        } catch (DropboxServerException e) {
            Log.e(getClass().getSimpleName(), "Got a http " + e.error + "; " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (DropboxException e) {
            Log.e(getClass().getSimpleName(), "Unexpected dbx exception: " + e.getMessage()); //$NON-NLS-1$
        }
        return stream;
    }

    @Override
    public OutputStream fetchMeAnOutputStream() {
        Session instance = Session.getInstance();
        instance.initialize(_context, true);
        final DropboxAPI<AndroidAuthSession> api = instance.getAPI();

        if (api == null)
            return null;

        DbxUploadStream stream = new DbxUploadStream() {

            @Override
            public void write() {
                InputStream is = new ByteArrayInputStream(this.toByteArray());
                try {
                    Entry e = api.putFileOverwrite(_filename, is, this.size(), null);
                    Log.i(getClass().getSimpleName(), "Bytes written to Dropbox: " + e.bytes); //$NON-NLS-1$
                } catch (DropboxServerException e) {
                    Log.e(getClass().getSimpleName(), "Got a http " + e.error + "; " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
                } catch (DropboxException e) {
                    Log.e(getClass().getSimpleName(), "Unexpected dbx exception: " + e.getMessage()); //$NON-NLS-1$
                }
            }
        };

        return stream;
    }

    /**
     * This is disgusting. Dropbox requires an inputstream to feed it data, but
     * {@link MainActivity} likes to feed into outputstreams.
     *
     * I'm not mucking with {@link NookOrCranny} to placate dbx, so I guess this
     * works.
     *
     * @author cheezmeister
     *
     */
    private abstract class DbxUploadStream extends ByteArrayOutputStream {
        public UploadRequest uploadRequest;

        @Override
        public void close() throws IOException {
            write();
            super.close();
        }

        public abstract void write();
    }

    /**
     * Manage the session/login/auth with dropbox api
     *
     * @author cheezmeister
     *
     */
    public static final class Session {
        public static Session getInstance() {
            return instance != null ? instance : (instance = new Session());
        }

        private static Session instance;

        private AndroidAuthSession _session;

        private Session() {
        }

        /**
         * Required immediately after authorizing app with dropbox
         *
         * @param context
         *
         * @see #initialize(Context, boolean)
         */
        public boolean completeAuth(Context context) {
            if (_session != null && _session.authenticationSuccessful()) {
                _session.finishAuthentication();
                String oaString = _session.getOAuth2AccessToken();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                prefs.edit().putString(C.KEY_PREF_DBX_OAUTH_TOKEN, oaString).commit();
                return _session.isLinked();
            }
            return false;
        }

        protected DropboxAPI<AndroidAuthSession> getAPI() {
            if (_session == null)
                return null;
            if (!_session.isLinked())
                return null;
            return new DropboxAPI<AndroidAuthSession>(_session);
        }

        /**
         * @see #initialize(Context, boolean)
         */
        public void initialize(Context context) {
            initialize(context, false);
        }

        /**
         *
         * @param context
         * @param suppressAuth
         *            Don't start authentication activity, even if no account is
         *            linked. Defaults to false
         */
        public void initialize(Context context, boolean suppressAuth) {
            if (_session != null && _session.isLinked()) { return; }

            AppKeyPair keypair = new AppKeyPair(C.DBX_APP_KEY, C.DBX_APP_SECRET);
            _session = new AndroidAuthSession(keypair);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String oauthToken = prefs.getString(C.KEY_PREF_DBX_OAUTH_TOKEN, null);

            if (oauthToken != null) {
                _session.setOAuth2AccessToken(oauthToken);
            } else if (!suppressAuth) {
                Log.i(getClass().getSimpleName(), "No oauth token found, need to auth dropbox"); //$NON-NLS-1$
                _session.startOAuth2Authentication(context);
            }

        }

    }

}