package nz.ac.auckland.ivs.mybus.db;

import android.provider.BaseColumns;

/**
 * Created by Luke X. Chang on 19/09/2016.
 */
public class DbTableContract {
    public DbTableContract() {

    }

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "AT_GTFS_Static";

    public static final String DB_TABLE_CREATE_STOPS = "CREATE TABLE STOPS( " +
            "stop_lat REAL NOT NULL, " +
            "stop_lon REAL NOT NULL, " +
            "stop_id INTEGER PRIMARY KEY, " +
            "stop_name TEXT, " +
            "location_type INT);";

    public static final String DB_TABLE_DELETE_STOPS = "DROP TABLE IF EXISTS " + BusTableStops.DB_TABLE_NAME;

    public static abstract class BusTableStops implements BaseColumns {
        public static final String DB_TABLE_NAME = "STOPS";
        public static final String FIELD_STOP_LAT = "stop_lat";
        public static final String FIELD_STOP_LON = "stop_lon";
        public static final String FIELD_STOP_ID = "stop_id";
        public static final String FIELD_STOP_NAME = "stop_name";
        public static final String FIELD_LOCATION_TYPE = "location_type";
    }
}
