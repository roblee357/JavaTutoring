package edu.vanderbilt.cs.live6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;



/**
 *
 * GeoHash Spatial Precision:
 *
 * Bits Spatial Tile Size
 *  1	5,009.4km x 4,992.6km
 2	1,252.3km x 624.1km
 3	156.5km x 156km
 4	39.1km x 19.5km
 5	4.9km x 4.9km
 6	1.2km x 609.4m
 7	152.9m x 152.4m
 8	38.2m x 19m
 9	4.8m x 4.8m
 10	1.2m x 59.5cm
 11	14.9cm x 14.9cm
 12	3.7cm x 1.9cm

 See: https://releases.dataone.org/online/api-documentation-v2.0.1/design/geohash.html
 */
public class GeoHash implements Iterable<Boolean> {

	/**
	 * This enum represents movement to a neighboring geohash. 
	 * The algorithm implemented for movement relies on knowing
	 * which "index" in the geohash is being modified -- this
	 * corresponds to either the even or odd bits. The increment
	 * is how much should be added/subtracted from the even or
	 * odd bits to reach the neighbor in the specified direction.
	 */
	public enum Direction {
		
		WEST(1,-1), EAST(1,1),
		NORTH(0,-1), SOUTH(0,1);
		
		private final int index;
		private final int increment;
		
		Direction(int index, int increment) {
			this.index = index;
			this.increment = increment;
		}

		public int getIndex() {
			return index;
		}

		public int getIncrement() {
			return increment;
		}
		
	}

	
	private static final double[] LATITUDE_RANGE = { -90, 90 };
	private static final double[] LONGITUDE_RANGE = { -180, 180 };

	/**
	 * Converts a boolean array to a Java BitSet to make calculating
	 * neighbors easier.
	 * 
	 * @param bits
	 * @return
	 */
	private static BitSet toBitSet(boolean[] bits) {
		BitSet bitset = new BitSet(bits.length);
		
		for(int i = 0; i < bits.length; i++) {
			bitset.set(i, bits[bits.length - i - 1]);
		}
		return bitset;
	}
	
	/**
	 * Converts a boolean array representing a set of
	 * bits into its long value.
	 * 
	 * Only supports geohashes with up to 63bits
	 * 
	 * @param bits
	 * @return
	 */
	private static long toLong(boolean[] bits) {
		long[] vs = toBitSet(bits).toLongArray();
		return (vs.length > 0)? vs[0] : 0;
	}

	/**
	 * Given a BitSet, converts the first `n` bits into a boolean
	 * array representation.
	 * 
	 * 
	 * @param bitset
	 * @param n
	 * @return
	 */
	private static boolean[] toBooleanArray(BitSet bitset, int n) {
		boolean[] bits = new boolean[n];
		for(int i = 0; i < n; i++) {
			bits[i] = bitset.get(n - i - 1);
		}
		return bits;
	}
	
	/**
	 * 
	 * Converts a long value into a base 2 representation as a 
	 * boolean array.
	 * 
	 * @param v
	 * @param n
	 * @return
	 */
	private static boolean[] toBooleanArray(long v, int n) {
		return toBooleanArray( BitSet.valueOf(new long[] {v}), n);
	}
	
	/**
	 * 
	 * Splits a boolean array into two arrays, one with
	 * the even bits, and one with the odd bits. 
	 * 
	 * @param v
	 * @return
	 */
	private static List<boolean[]> unzip(boolean[] v){
		List<boolean[]> split = new ArrayList<>(2);
	
		int v1size = v.length / 2;
		int v0size = v.length - v1size;
		
		boolean[] v0 = new boolean[v0size];
		boolean[] v1 = new boolean[v1size];
		
		for(int i = 0; i < v.length; i++) {
			if(i % 2 == 0) {
				v0[i/2] = v[i]; 
			}
			else {
				v1[i/2] = v[i];
			}
		}
		
		return Arrays.asList(v0, v1);
	}
	
	/**
	 * 
	 * Given two arrays of boolean values, interleaves the arrays
	 * into a single combined array.
	 * 
	 * @param v0
	 * @param v1
	 * @return
	 */
	public static boolean[] zip(boolean[] v0, boolean[] v1) {
		
		boolean[] combinedhash = new boolean[v0.length + v1.length];
		
		for(int i = 0; i < combinedhash.length; i++) {
			if(i % 2 == 0) {
				combinedhash[i] = v0[i/2];
			}
			else {
				combinedhash[i] = v1[(i-1)/2];
			}
		}
		return combinedhash;
	}
	
	
	/**
	 * 
	 * Given a geohash, this method computes the neighboring geohash
	 * in the direction specified. 
	 * 
	 * The neighbor will have the following properties:
	 * 
	 * 1. either all of the latitude or longitude (even or odd) bits
	 *    will match the starting geohash
	 *    
	 * 2. if you convert the other half of the bits from base 2 to a
	 *    base 10 value, the neighbor's value will be +1 or -1 away.
	 * 
	 * 3. once the differing half of the bits has had 1 added or subtracted,
	 *    the bits can be recombined with the matching half of the bits to
	 *    produce the neighboring geohash
	 * 
	 * @param hash
	 * @param direction
	 * @return
	 */
	private static boolean[] move(boolean[] hash, Direction direction) {
		
		// split the geohash so that we have a 1D hash for latitude and
		// a 1D hash for longitude
		List<boolean[]> latlon = unzip(hash);
		
		// grab the bits for either latitude or longitude, depending
		// on which direction we are moving
		boolean[] startingLocation = latlon.get( direction.index );
		
		// convert the bits of the hash into a long value so that we
		// can easily find the hash that is 1 bit away (neighbors)
		long v = toLong(startingLocation);
		
		// either add or subtract one bit from the hash to find the
		// neighbor
		v += direction.increment;
		
		// convert the neighbor's lat/lon hash into its boolean array representation
		boolean[] newLocation = toBooleanArray(v, startingLocation.length);
		
		// reuse the bits from the unmodified half of the original hash
		// and combine them with the calculated half of the bits for the 
		// neighbor
		latlon.set(direction.index, newLocation);
		
		// zip the two 1D hashes back up into a geohash for the neighbor
		return zip(latlon.get(0), latlon.get(1));
	}
	
	/**
	 * 
	 * Calculates a 1-dimensional geohash based on the specified value range and bits
	 * of precision.
	 * 
	 * @param valueToHash
	 * @param valueRange
	 * @param bitsOfPrecision
	 * @return
	 */
	public static boolean[] geohash1D(double valueToHash, double[] valueRange, int bitsOfPrecision) {

		boolean[] resultHash = new boolean[bitsOfPrecision];

		for(int i = 0; i < bitsOfPrecision; i++){
			double midPoint = (valueRange[0] + valueRange[1]) / 2;
			boolean bit = (valueToHash >= midPoint);
			valueRange = (bit) ? new double[] { midPoint, valueRange[1] }
			                   : new double[] { valueRange[0], midPoint };
			resultHash[i] = bit;
		}

		return resultHash;
	}
	
	/**
	 * 
	 * Calculates a 2D geohash for a latitude and longitude.
	 * 
	 * @param lat
	 * @param lon
	 * @param bitsOfPrecision
	 * @return
	 */
	private static boolean[] geohash(double lat, double lon, int bitsOfPrecision) {
		int lonprecision = bitsOfPrecision / 2;
		int latprecision = bitsOfPrecision - lonprecision;

		boolean[] lathash = geohash1D(lat, LATITUDE_RANGE, latprecision);
		boolean[] lonhash = geohash1D(lon, LONGITUDE_RANGE, lonprecision);

		return zip(lathash, lonhash);
	}
	
	
	/**
	 * 
	 * This is a factory method to produce GeoHashes with the specified coordinates
	 * and precision.
	 * 
	 * @param lat
	 * @param lon
	 * @param bitsOfPrecision
	 * @return
	 */
	public static GeoHash with(double lat, double lon, int bitsOfPrecision) {
		boolean[] hash = geohash(lat, lon, bitsOfPrecision);
		return new GeoHash(hash);
	}


	
    private final boolean[] geohash;

    private GeoHash(boolean[] geohash) {
        this.geohash = geohash;
    }
    
    public int bitsOfPrecision() {
    	return geohash.length;
    }
    
    /**
     * 
     * Returns a new GeoHash representing the first n bits of the
     * current geohash.
     * 
     * @param n
     * @return
     */
    public GeoHash prefix(int n) {
    	if(n < this.bitsOfPrecision()) {
    		boolean[] pre = Arrays.copyOf(geohash, n);
    		return new GeoHash(pre);
    	}
    	else {
    		return new GeoHash(Arrays.copyOf(geohash, geohash.length));
    	}
    }
    
    /**
     * 
     * Returns the northern neighbor of the geohash
     * 
     * @return
     */
	public GeoHash northNeighbor() {
		return new GeoHash( move(this.geohash, Direction.NORTH) );
	}
	
    /**
     * 
     * Returns the southern neighbor of the geohash
     * 
     * @return
     */
	public GeoHash southNeighbor() {
		return new GeoHash( move(this.geohash, Direction.SOUTH) );
	}
	
    /**
     * 
     * Returns the western neighbor of the geohash
     * 
     * @return
     */
	public GeoHash westNeighbor() {
		return  new GeoHash( move(this.geohash, Direction.WEST) );
	}
	
    /**
     * 
     * Returns the eastern neighbor of the geohash
     * 
     * @return
     */
	public GeoHash eastNeighbor() {
		return new GeoHash( move(this.geohash, Direction.EAST) );
	}

	/**
	 * 
	 * Returns an interator over the bits in the geohash. 
	 * 
	 */
    @Override
    public Iterator<Boolean> iterator() {
        return new Iterator<Boolean>() {

            private int index = -1;

            @Override
            public boolean hasNext() {
                return index < geohash.length - 1;
            }

            @Override
            public Boolean next() {
                index++;
                return geohash[index];
            }
        };
    }

    /**
     * 
     * Geohashes are considered equal if they have the same number of bits
     * and each bit is exactly the same.
     * 
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoHash booleans = (GeoHash) o;
        return Arrays.equals(geohash, booleans.geohash);
    }

    /**
     * 
     * The hashcode for a geohash is a hash of its bits. 
     * 
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(geohash);
    }

	public String toString() {
		String hashString = "";
		for (boolean b : this) {
			hashString += (b ? "1" : "0");
		}
		return hashString;
	}
	

}
