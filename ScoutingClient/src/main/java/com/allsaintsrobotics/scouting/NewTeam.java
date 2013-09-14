package com.allsaintsrobotics.scouting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class NewTeam extends Activity {

    private ImageButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newteam);

        save = (ImageButton) findViewById(R.id.save_team);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView name = (TextView) findViewById(R.id.new_teamname);
                TextView number = (TextView) findViewById(R.id.new_teamnum);
                boolean save = true;
                int numberI = 0;

                try
                {
                    numberI = Integer.parseInt(number.getText().toString());
                } catch (NumberFormatException e)
                {
                    number.setError(getString(R.string.error_invalid_integer));
                    save = false;
                }

                if (TextUtils.isEmpty(name.getText()))
                {
                    name.setError(getString(R.string.error_invalid_teamname));
                    save = false;
                }

                if (save)
                {
                    Team team = new Team(numberI, name.getText().toString());

                    Intent data = new Intent(NewTeam.this, NewTeam.class);

                    data.putExtra("team", team);

                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });



        // Show the Up button in the action bar.
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_team, menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
