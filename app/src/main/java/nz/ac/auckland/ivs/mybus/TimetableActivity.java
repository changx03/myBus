package nz.ac.auckland.ivs.mybus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class TimetableActivity extends AppCompatActivity
        implements TableRow.OnClickListener {

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
                ((ScrollView) findViewById(R.id.tableScroll)).fullScroll(View.FOCUS_UP);
            }
        });

        Bundle bundle = getIntent().getExtras();
        marker_id = bundle.getString(MapsActivity.MARKER_ID);
        try {
            getSupportActionBar().setTitle(marker_id);
        } catch (NullPointerException e) {
            System.out.print(e.toString());
        }
        generateTable();
    }

    private void generateTable() {
        final TableLayout tableLayout_create = (TableLayout) findViewById(R.id.TableLayout);
        final int rows = 50;

        // Set layout
        TableLayout.LayoutParams rowLayout = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        rowLayout.setMargins(getResources().getInteger(R.integer.table_row_left),
                getResources().getInteger(R.integer.table_row_top),
                getResources().getInteger(R.integer.table_row_right),
                getResources().getInteger(R.integer.table_row_bottom));

        TypedValue weight = new TypedValue();
        for (int i = 0; i < rows; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(rowLayout);

            TextView cellNumber = new TextView(this);
            cellNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_font_size));
            cellNumber.setGravity(Gravity.CENTER);
            String formattedName = String.format(Locale.US, "%04d", i);
            cellNumber.setText(formattedName);
            getResources().getValue(R.dimen.cell1_weight, weight, true);
            row.addView(cellNumber, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,
                    weight.getFloat()));

            TextView cellDest = new TextView(this);
            cellDest.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_font_size));
            cellDest.setGravity(Gravity.START);
            cellDest.setText(String.format(Locale.US, "Auckland CBD %02d", i));
            getResources().getValue(R.dimen.cell2_weight, weight, true);
            row.addView(cellDest, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,
                    weight.getFloat()));

            Calendar cTime = Calendar.getInstance();
            TextView cellTime1 = fillTime(cTime);
            getResources().getValue(R.dimen.cell3_weight, weight, true);
            row.addView(cellTime1, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,
                    weight.getFloat()));

            TextView cellTime2 = fillTime(cTime);
            getResources().getValue(R.dimen.cell3_weight, weight, true);
            row.addView(cellTime2, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,
                    weight.getFloat()));

            row.setClickable(true);
            row.setOnClickListener(this);
            tableLayout_create.addView(row);
        }
    }

    private TextView fillTime(Calendar time) {
        TextView cellTime1 = new TextView(this);
        cellTime1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_font_size));
        cellTime1.setGravity(Gravity.CENTER);
        String formattedTime = String.format("%tl:%tM", time, time);
        cellTime1.setText(formattedTime);
        return cellTime1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_timetableRefresh) {
            Toast.makeText(this, "Refresh function goes here.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View v) {
        TableRow row = (TableRow) v;
        row.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        Toast.makeText(this, ((TextView) row.getChildAt(0)).getText(), Toast.LENGTH_SHORT).show();
    }
}