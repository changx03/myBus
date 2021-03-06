package nz.ac.auckland.ivs.mybus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import nz.ac.auckland.ivs.mybus.R;

public class TimetableActivity extends AppCompatActivity
        implements TableRow.OnClickListener {

    public final static String BUS_INDEX = "BUS_INDEX";
    private String mMarker_id;

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
        mMarker_id = bundle.getString(MapsActivity.MARKER_ID);
        try {
            getSupportActionBar().setTitle(mMarker_id);
        } catch (NullPointerException e) {
            System.out.print(e.toString());
        }
        generateTable();
    }

    private void generateTable() {
        final TableLayout tableLayout_create = (TableLayout) findViewById(R.id.TableLayout);
        final int rows = 50;


        for (int i = 0; i < rows; i++) {
            String content = String.format(Locale.US, "Auckland CBD %02d", i);
            Calendar cTime = Calendar.getInstance();

            TableRow row = createRow(i, content, cTime, cTime);
            row.setClickable(true);
            row.setOnClickListener(this);
            tableLayout_create.addView(row);
        }
    }

    private TableRow createRow(int number, String content, Calendar t1, Calendar t2) {
        TableRow row = new TableRow(this);
        // Set layout
        TableLayout.LayoutParams rowLayout = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        rowLayout.setMargins(getResources().getInteger(R.integer.table_row_left), getResources().getInteger(R.integer.table_row_top),
                getResources().getInteger(R.integer.table_row_right), getResources().getInteger(R.integer.table_row_bottom));
        row.setLayoutParams(rowLayout);

        TypedValue weight = new TypedValue();
        TextView cellNo = new TextView(this);
        cellNo.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_font_size));
        cellNo.setGravity(Gravity.CENTER);
        String formattedName = String.format(Locale.US, "%04d", number);
        cellNo.setText(formattedName);
        getResources().getValue(R.dimen.cell1_weight, weight, true);
        row.addView(cellNo, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,
                weight.getFloat()));

        TextView cellDest = new TextView(this);
        cellDest.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_font_size));
        cellDest.setGravity(Gravity.START);
        cellDest.setText(content);
        getResources().getValue(R.dimen.cell2_weight, weight, true);
        row.addView(cellDest, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,
                weight.getFloat()));

        TextView cellTime = createTimeCell(t1);
        getResources().getValue(R.dimen.cell3_weight, weight, true);
        row.addView(cellTime, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,
                weight.getFloat()));

        cellTime = createTimeCell(t2);
        getResources().getValue(R.dimen.cell3_weight, weight, true);
        row.addView(cellTime, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,
                weight.getFloat()));

        return row;
    }

    private TextView createTimeCell(Calendar time) {
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
        String busIndexStr = ((TextView) row.getChildAt(0)).getText().toString();
        Toast.makeText(this, busIndexStr, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, BusRouteActivity.class);
        intent.putExtra(BUS_INDEX, busIndexStr);
        startActivity(intent);
    }
}
