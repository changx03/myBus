package nz.ac.auckland.ivs.mybus.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Luke X. Chang on 9/21/2016.
 */

public class BusStop implements ClusterItem {
    private final LatLng mPosition;
    public final String address;
    public final int stopNumber;

    public BusStop(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        address = "Test address";
        stopNumber = 101;
    }

    public BusStop(LatLng position) {
        mPosition = position;
        address = "Test address";
        stopNumber = 101;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
