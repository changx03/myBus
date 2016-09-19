package nz.ac.auckland.ivs.mybus.ui;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;

import nz.ac.auckland.ivs.mybus.R;
import nz.ac.auckland.ivs.mybus.db.DatabaseHelper;
import nz.ac.auckland.ivs.mybus.db.DbTableContract;

public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnInfoWindowClickListener,
        SearchView.OnQueryTextListener {

    public static final String MARKER_ID = "MARKER_ID";
    public static final LatLng AUCKLAND_UOA = new LatLng(-36.853315, 174.768416);

    private GoogleMap mMap;
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        try {
            databaseHelper.createDatabase();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try {
//            databaseHelper.openDatabase();
            long rows = databaseHelper.getProfilesCount();
            System.out.println("db rows = " + rows);
        } catch (SQLiteException e) {
            System.err.println(e.getMessage());
        }

//        // Debug: assets test
//        System.out.println("debug starts:");
//        String filesDir = getFilesDir().getPath();
//        String databaseDir = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/";
//        System.out.println(databaseDir);
//        InputStream inputStream = null;
//        try {
//            inputStream = getAssets().open(DbTableContract.DB_NAME + ".db");
//
//            byte[] b = new byte[128];
//            inputStream.read(b);
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Move the camera to the University of Auckland
        mMap.addMarker(new MarkerOptions().position(AUCKLAND_UOA).title("the University of Auckland"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AUCKLAND_UOA, 15));

        mMap.setOnMyLocationButtonClickListener(this);
        PermissionUtils.enableMyLocation(mMap, this);
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            PermissionUtils.enableMyLocation(mMap, this);
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(sm.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
            searchView.setIconifiedByDefault(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.search) {
            Toast.makeText(this, "Search function goes here.", Toast.LENGTH_SHORT).show();
        }
        if (menuItem.getItemId() == R.id.menu_refresh) {
            Toast.makeText(this, "Refresh function goes here.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Searching by: " + query, Toast.LENGTH_SHORT).show();

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: " + uri, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Clicked " + marker.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MapsActivity.this, TimetableActivity.class);

        //TODO update bus id here
        String dummyID = "0001";
        intent.putExtra(MARKER_ID, dummyID);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
