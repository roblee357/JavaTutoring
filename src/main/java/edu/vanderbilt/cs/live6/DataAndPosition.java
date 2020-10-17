package edu.vanderbilt.cs.live6;

public interface DataAndPosition<T> extends Position {

    public static <T> DataAndPosition<T> with(double lat, double lon, T data){
        return new DataAndPosition() {

            @Override
            public double getLatitude() {
                return lat;
            }

            @Override
            public double getLongitude() {
                return lon;
            }

            @Override
            public Object getData() { return data; }
        };
    }

    public T getData();

}
