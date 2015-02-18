package com.luchenlabs.fkls.app.storage;

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
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AppKeyPair;
import com.luchenlabs.fkls.app.MainActivity;
import com.luchenlabs.fkls.core.C;

public class DropboxCoreNook implements NookOrCranny {

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

        private AndroidAuthSession _authSession;

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
            if (_authSession != null && _authSession.authenticationSuccessful()) {
                _authSession.finishAuthentication();
                String oaString = _authSession.getOAuth2AccessToken();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                prefs.edit().putString(C.KEY_PREF_DBX_OAUTH_TOKEN, oaString).commit();
                return _authSession.isLinked();
            }
            return false;
        }

        protected DropboxAPI<AndroidAuthSession> getAPI() {
            if (_authSession == null) throw new IllegalStateException("Missing dropbox session"); //$NON-NLS-1$
            if (!_authSession.isLinked()) return null;
            return new DropboxAPI<AndroidAuthSession>(_authSession);
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
            if (_authSession != null && _authSession.isLinked()) { return; }

            AppKeyPair keypair = new AppKeyPair(C.DBX_APP_KEY, C.DBX_APP_SECRET);
            _authSession = new AndroidAuthSession(keypair);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String oauthToken = prefs.getString(C.KEY_PREF_DBX_OAUTH_TOKEN, null);

            if (oauthToken != null) {
                _authSession.setOAuth2AccessToken(oauthToken);
            } else if (!suppressAuth) {
                Log.i(getClass().getSimpleName(), "No oauth token found, need to auth dropbox"); //$NON-NLS-1$
                _authSession.startOAuth2Authentication(context);
            }

        }

    }

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
        Log.v(getClass().getSimpleName(), "Getting instance");
        Session instance = Session.getInstance();
        Log.v(getClass().getSimpleName(), "Initing instance");
        instance.initialize(_context, true);
        Log.v(getClass().getSimpleName(), "Getting api");
        DropboxAPI<AndroidAuthSession> api = instance.getAPI();

        if (api == null) return null;

        Log.v(getClass().getSimpleName(), "Getting stream");
        for (int i = 0; i < 3; ++i) {
            try {
                DropboxInputStream stream = api.getFileStream(_filename, null);
                if (stream != null) return stream;
            } catch (DropboxServerException e) {
                Log.e(getClass().getSimpleName(), "Got a http " + e.error + "; " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            } catch (DropboxException e) {
                Log.e(getClass().getSimpleName(), "Unexpected dbx exception: " + e.getMessage()); //$NON-NLS-1$
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Fuck you, Java.
            }
        }
        return null;
    }

    @Override
    public OutputStream fetchMeAnOutputStream() {
        Session instance = Session.getInstance();
        instance.initialize(_context, true);
        final DropboxAPI<AndroidAuthSession> api = instance.getAPI();

        if (api == null) return null;

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

}
