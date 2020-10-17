package edu.vanderbilt.cs.live6;

import edu.vanderbilt.cs.live6.GeoHash;
import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live6.Position;
import edu.vanderbilt.cs.live6.ProximityDB;

import java.util.*;
import java.util.stream.Collectors;



public class ProximityDBHashMap<T> implements ProximityDB<T> {

    private int bitsOfPrecision;

    private Map<GeoHash,List<DataAndPosition<T>>> geohashLookup = new HashMap<>();

    public ProximityDBHashMap(int bitsOfPrecision) {
        this.bitsOfPrecision = bitsOfPrecision;
    }
    
    
    private void insert(GeoHash geohash, DataAndPosition<T> l){
        List<DataAndPosition<T>> existing = geohashLookup.get(geohash);
        
        if(existing == null){
        	System.out.println("Existing does not exist  " + geohash);
            existing = new LinkedList<>();
            geohashLookup.put(geohash,existing);
        }
        System.out.println(existing.size());
        existing.add(l);
        System.out.println(existing.size());
        
    }


    private List<DataAndPosition<T>> find(GeoHash hash){
    	// We do the work up-front on insert of associating data with every
    	// possible geohash prefix that it can be searched by. Because of
    	// this up-front work, we can look up any geohash in the hashmap
    	// and immediately get all matching data items. 
        return geohashLookup.getOrDefault(hash, Collections.emptyList());
    }

    
	@Override
	public void insert(DataAndPosition<T> data) {
		GeoHash hash = GeoHash.with(data.getLatitude(), data.getLongitude(), this.bitsOfPrecision);

		// We insert the data under the full geohash
		insert(hash, data);
		
		// We also need to calculate each prefix of the hash so that
		// we can insert the data under those prefixes and make the
		// nearby search O(1) rather than O(n)
        for(int i = 1; i < bitsOfPrecision; i++){
        	
        	// The prefix method of the geohash separates us from the
        	// underlying representation of the hash. All we know is that
        	// we have a prefix of a hash and we don't need to know that
        	// it is a boolean array at this point. 
        	
        	// The only reason this works properly is that we have overridden
        	// the equals and hashcode methods in the GeoHash class so that
        	// they function correctly in a hashmap
            insert(hash.prefix(i), data);
        }
	}

	@Override
	public Collection<DataAndPosition<T>> delete(Position pos, int bitsOfPrecision) {
		GeoHash hash = GeoHash.with(pos.getLatitude(),pos.getLongitude(),bitsOfPrecision);
    	List<DataAndPosition<T>> locs = find(hash);
    	List<DataAndPosition<T>> copy = new ArrayList<>(locs);
        locs.clear();

        // Unfortunately, the delete operation is expensive with this approach. We
        // must check every key to see if it is prefixed by the geohash that we want
        // to delete
        List<GeoHash> keysToRemove = geohashLookup
        		.keySet()
        		.stream()
        		.filter(k -> k.prefix(bitsOfPrecision).equals(hash))
        		.collect(Collectors.toList());
        
        keysToRemove.forEach(k -> geohashLookup.remove(k));

        return copy;
	}
	
	@Override
	public Collection<DataAndPosition<T>> delete(Position pos) {
		// This delete is a special case of the other delete, so we
		// just delegate to it in order avoid repeating ourselves (DRY)
		return delete(pos, this.bitsOfPrecision);
	}

	@Override
	public boolean contains(Position pos, int bitsOfPrecision) {
		// We implement contains in terms of find, since there is no need
		// to repeat that logic here (DRY)
		System.out.println("Inspecting contains input: " +pos.getLatitude()  + ", " + pos.getLongitude());
		List<DataAndPosition<T>> data = find(GeoHash.with(pos.getLatitude(), pos.getLongitude(), bitsOfPrecision));
		//System.out.println(data.get(0).getLatitude() + ", " + data.get(0).getLongitude());
		return !data.isEmpty();
	}

	@Override
	public Collection<DataAndPosition<T>> nearby(Position pos, int bitsOfPrecision) {
		// Because we have already indexed every data item under all possible prefixes
		// it could be searched by, we can simply lookup the set of nearby locations in
		// O(1) time
		return find(GeoHash.with(pos.getLatitude(), pos.getLongitude(), bitsOfPrecision));
	}
}
