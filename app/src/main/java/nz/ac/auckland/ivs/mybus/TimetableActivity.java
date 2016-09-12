package nz.ac.auckland.ivs.mybus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class TimetableActivity extends AppCompatActivity {

    private String marker_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Back to top", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        marker_id = bundle.getString(MapsActivity.MARKER_ID);
        if(marker_id != null) getSupportActionBar().setTitle(marker_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.menu_timetableRefresh) {
            Toast.makeText(this, "Refresh function goes here.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
