package nz.ac.auckland.ivs.mybus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class BusRouteActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mLastLocation;
    private String mBusIdex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_bus_route);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        mBusIdex = bundle.getString(TimetableActivity.BUS_INDEX);

        try {
            getSupportActionBar().setTitle(mBusIdex);
        } catch (NullPointerException e) {
            System.out.print(e.toString());
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng startLatLng;
        if (mLastLocation != null) {
            startLatLng = mLastLocation;
        } else {
            startLatLng = MapsActivity.AUCKLAND_UOA;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
        mMap.setOnMyLocationButtonClickListener(this);
        PermissionUtils.enableMyLocation(mMap, this);

        addPolyLines(testStops());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private void addPolyLines(LatLng... points) {
        mMap.addPolyline((new PolylineOptions()).add(points).width(5.0f).color(Color.BLUE).geodesic(true));
        mMap.addMarker(new MarkerOptions().position(testStops()[0]));
        mMap.addMarker(new MarkerOptions().position(testStops()[testStops().length - 1]));
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (LatLng point : points) {
            bounds.include(point);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), getResources().getInteger(R.integer.map_padding))); // padding
    }

    private LatLng[] testStops() {
        ArrayList<LatLng> stops = new ArrayList<>();
        stops.add(new LatLng(-36.844895, 174.767096));
        stops.add(new LatLng(-36.847058, 174.765744));
        stops.add(new LatLng(-36.848278, 174.765551));
        stops.add(new LatLng(-36.850905, 174.764521));
        stops.add(new LatLng(-36.853927, 174.768233));
        stops.add(new LatLng(-36.858442, 174.763641));
        return stops.toArray(new LatLng[stops.size()]);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            mLastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
