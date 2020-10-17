package edu.vanderbilt.cs.live6;

public interface Position {

    public static Position with(double lat, double lon){
        return new Position() {

            @Override
            public double getLatitude() {
                return lat;
            }

            @Override
            public double getLongitude() {
                return lon;
            }
        };
    }

    public double getLatitude();

    public double getLongitude();
}
