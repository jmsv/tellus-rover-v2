package net.jamesvickery.tellusroverremote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

public class RemoteControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public boolean returnToLoginActivity(){
        // TODO: Add some stuff here to close connection to Pi
        finish();
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item){
        return returnToLoginActivity();
    }

    @Override
    public void onBackPressed() {
        returnToLoginActivity();
    }

}
