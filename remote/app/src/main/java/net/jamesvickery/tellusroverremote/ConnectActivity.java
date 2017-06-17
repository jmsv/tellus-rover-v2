package net.jamesvickery.tellusroverremote;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A login screen that offers login via ip/password.
 */
public class ConnectActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mIPView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mRememberPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        // Set up the login form.
        mIPView = (AutoCompleteTextView) findViewById(R.id.ip);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mIPSignInButton = (Button) findViewById(R.id.ip_sign_in_button);
        mIPSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mRememberPasswordView = findViewById(R.id.checkBoxRememberPassword);

        loadDefaults();
    }

    private void loadDefaults(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = preferences.getString("ip", "");
        mIPView.setText(ip);
        boolean rememberPassword = preferences.getBoolean("rememberPassword", false);
        if (rememberPassword){
            CheckBox checkBox = (CheckBox)mLoginFormView.findViewById(R.id.checkBoxRememberPassword);
            checkBox.setChecked(true);
            String password = preferences.getString("password", "");
            mPasswordView.setText(password);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid ip, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mIPView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String ip = mIPView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid ip address.
        if (TextUtils.isEmpty(ip)) {
            mIPView.setError(getString(R.string.error_field_required));
            focusView = mIPView;
            cancel = true;
        } else if (!isIPValid(ip)) {
            mIPView.setError(getString(R.string.error_invalid_ip));
            focusView = mIPView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(ip, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isIPValid(String ip) {
        String ipRegex = "^\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b$";
        return ip.matches(ipRegex);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mIP;
        private final String mPassword;

        UserLoginTask(String ip, String password) {
            mIP = ip;
            mPassword = password;

            final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxRememberPassword);

            // https://stackoverflow.com/a/11027631/5127934
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ip", mIP);
            if (checkBox.isChecked()) {
                editor.putBoolean("rememberPassword", true);
                editor.putString("password", mPassword);
            } else {
                editor.putBoolean("rememberPassword", false);
            }
            editor.apply();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: If password is wrong, return false. Otherwise, start Remote Control

            try {
                Thread.sleep(1000); // Replace with POST call
            } catch (InterruptedException e) {
                return false;
            }

            Intent startRemoteControl = new Intent(getApplicationContext(), RemoteControlActivity.class);
            startActivity(startRemoteControl);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // TODO: This is where the remote activity should be started, passing the auth key
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
