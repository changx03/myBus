package nz.ac.auckland.ivs.mybus.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

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

import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.SnappedPoint;

import java.util.ArrayList;
import java.util.List;

import nz.ac.auckland.ivs.mybus.R;

public class BusRouteActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mLastLocation;
    private String mBusIdex;

    private GeoApiContext mContext;
    private ProgressBar mProgressBar;
    List<LatLng> mStopPoints;
    List<SnappedPoint> mSnappedPoints;

    /**
     * The number of points allowed per API request. This is a fixed value.
     */
    private static final int PAGE_SIZE_LIMIT = 100;

    /**
     * Define the number of data points to re-send at the start of subsequent requests. This helps
     * to influence the API with prior data, so that paths can be inferred across multiple requests.
     * You should experiment with this value for your use-case.
     */
    private static final int PAGINATION_OVERLAP = 5;

    AsyncTask<Void, Void, List<SnappedPoint>> mTaskSnapToRoads =
            new AsyncTask<Void, Void, List<SnappedPoint>>() {
                @Override
                protected void onPreExecute() {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setIndeterminate(true);
                }

                @Override
                protected List<SnappedPoint> doInBackground(Void... params) {
                    try {
                        return snapToRoads(mContext);
                    } catch (final Exception e) {
                        System.out.print(e.toString());
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(List<SnappedPoint> snappedPoints) {
                    mSnappedPoints = snappedPoints;
                    mProgressBar.setVisibility(View.INVISIBLE);
                    LatLng[] mapPoints = new LatLng[snappedPoints.size()];
                    LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                    int i = 0;
                    for (SnappedPoint point : mSnappedPoints) {
                        mapPoints[i] = new LatLng(point.location.lat, point.location.lng);
                        bounds.include(mapPoints[i]);
                        i++;
                    }
                    mMap.addPolyline(new PolylineOptions().add(mapPoints).color(Color.RED));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),
                            getResources().getInteger(R.integer.map_padding)));
                }
            };

    private List<SnappedPoint> snapToRoads(GeoApiContext context) throws Exception {
        List<SnappedPoint> snappedPoints = new ArrayList<>();
        int offset = 0;

        try {
            SnappedPoint tempSP = new SnappedPoint();
            tempSP.location = new com.google.maps.model.LatLng(mStopPoints.get(0).latitude, mStopPoints.get(0).longitude);
            snappedPoints.add(tempSP);
            while (offset < mStopPoints.size()) {
                // Calculate which points to include in this request. We can't exceed the APIs
                // maximum and we want to ensure some overlap so the API can infer a good location for
                // the first few points in each request.
                if (offset > 0) {
                    offset -= PAGINATION_OVERLAP;   // Rewind to include some previous points
                }
                int lowerBound = offset;
                int upperBound = Math.min(offset + PAGE_SIZE_LIMIT, mStopPoints.size());

                // Grab the data we need for this page.
                List<LatLng> pages = mStopPoints.subList(lowerBound, upperBound);
                com.google.maps.model.LatLng[] mapPage = new com.google.maps.model.LatLng[pages.size()];
                int i = 0;
                for (LatLng page : pages) {
                    mapPage[i] = new com.google.maps.model.LatLng(page.latitude, page.longitude);
                    i++;
                }

                // Perform the request. Because we have interpolate=true, we will get extra data points
                // between our originally requested path. To ensure we can concatenate these points, we
                // only start adding once we've hit the first new point (i.e. skip the overlap).
                SnappedPoint[] points = RoadsApi.snapToRoads(context, true, mapPage).await();
                boolean passedOverlap = false;
                for (SnappedPoint point : points) {
                    if (offset == 0 || point.originalIndex >= PAGINATION_OVERLAP) {
                        passedOverlap = true;
                    }
                    if (passedOverlap) {
                        snappedPoints.add(point);
                    }
                }

                offset = upperBound;
            }
            tempSP = new SnappedPoint();
            tempSP.location = new com.google.maps.model.LatLng(mStopPoints.get(mStopPoints.size() - 1).latitude, mStopPoints.get(mStopPoints.size() - 1).longitude);
            snappedPoints.add(tempSP);
        } catch (final Exception e) {
            System.out.println(e.toString());
        }

        System.out.println("snappedPoints size = " + snappedPoints.size());
        return snappedPoints;
    }

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

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mContext = new GeoApiContext().setApiKey(getString(R.string.google_maps_web_services_key));
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

        mStopPoints = testStops();
        addPolyLines(mStopPoints.toArray(new LatLng[mStopPoints.size()]));

        mTaskSnapToRoads.execute();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private void addPolyLines(LatLng... points) {
        mMap.addPolyline((new PolylineOptions()).add(points).width(5.0f).color(Color.BLUE).geodesic(true));
        mMap.addMarker(new MarkerOptions().position(mStopPoints.get(0)).title("start"));
        mMap.addMarker(new MarkerOptions().position(mStopPoints.get(mStopPoints.size() - 1)).title("end"));
//        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
//        for (LatLng point : points) {
//            bounds.include(point);
//        }
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),
//                getResources().getInteger(R.integer.map_padding))); // padding
    }

    private List<LatLng> testStops() {
        List<LatLng> stops = new ArrayList<>();
        stops.add(new LatLng(-36.850984, 174.765015));  // 0
        stops.add(new LatLng(-36.852796, 174.767364));
        stops.add(new LatLng(-36.854178, 174.768083));
        stops.add(new LatLng(-36.856625, 174.765422));
        stops.add(new LatLng(-36.857921, 174.76392));   // 4
        stops.add(new LatLng(-36.857930, 174.760970));
        stops.add(new LatLng(-36.857578, 174.758288));
        return stops;
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
