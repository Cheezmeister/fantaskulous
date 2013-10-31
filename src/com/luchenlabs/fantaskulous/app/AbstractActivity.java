package com.luchenlabs.fantaskulous.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

import com.luchenlabs.fantaskulous.R;

public class AbstractActivity extends FragmentActivity {

    protected String ex(Exception e, int resId, Object... args) {
        return getString(resId, args) + "\n" //$NON-NLS-1$
                + getString(R.string.fmt_the_exception_thrown_was, e.toString());
    }

    protected void showTextInputDialog(String title, String hint, final StringRunnable onString) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle(title);
        final EditText field = new EditText(this);
        field.setHint(hint);
        alert.setView(field);

        alert.setPositiveButton(R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onString.run(field.getText().toString());
            }
        });

        alert.show();
    }

    public interface StringRunnable {
        public void run(String string);
    }
}