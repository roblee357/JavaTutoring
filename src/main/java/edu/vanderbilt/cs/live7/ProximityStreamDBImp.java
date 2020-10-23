package edu.vanderbilt.cs.live7;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live6.GeoHash;
import edu.vanderbilt.cs.live6.Position;
import edu.vanderbilt.cs.live6.ProximityDBHashMap;

public class ProximityStreamDBImp<T> implements ProximityStreamDB {

    private int bitsOfPrecision;
    private ProximityDBHashMap<T> db ;
    private List<HashMap<GeoHash, List<DataAndPosition<T>>>> history;
    
    
    
    public void setDb(ProximityDBHashMap<T> db) {
		this.db = db;
	}

	public ProximityStreamDBImp (int bitsOfPrecision) {
    	db = new ProximityDBHashMap(bitsOfPrecision);
    	history = new ArrayList<>();
    }
	
    private void backup() {
    	HashMap<GeoHash, List<DataAndPosition<T>>> copy = new HashMap<>();
    	for(GeoHash gh : db.getGeoLookup().keySet()) {
    		List<DataAndPosition<T>> independentList = new ArrayList<>(db.getGeoLookup().get(gh));
    		copy.put(gh, independentList);
    	}
    	System.out.println("Backup " + copy);
    	history.add(copy);
    }
    
	@Override
	public void insert(DataAndPosition data) {
		// TODO Auto-generated method stub
		backup();
		db.insert(data);
	}

	@Override
	public Collection delete(Position pos) {
		// TODO Auto-generated method stub
		backup();
		return db.delete(pos);
	}

	@Override
	public Collection delete(Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		backup();
		return db.delete(pos);
	}

	@Override
	public boolean contains(Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return db.contains(pos,bitsOfPrecision);
	}

	@Override
	public Collection nearby(Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return db.nearby(pos,bitsOfPrecision);
	}

	@Override
	public ProximityStreamDB databaseStateAtTime(int n) { // 1
		System.out.println("------Existing backups -------");
		for(HashMap hm : history) {
			System.out.println(hm);
		}
		System.out.println("------Finish Existing backups -------");
		if(n < 0) {
			throw new IllegalArgumentException("Invalid time state requested");
		}
		if(n == 0) {
			return this;
		}
							// 3	-    	2     = -1
		int timeRequested = history.size() - n;
		if(timeRequested >= 0) {
			
			HashMap<GeoHash, List<DataAndPosition<T>>> copy = history.get(timeRequested);
			System.out.println("Recovered backup " + copy);
			ProximityDBHashMap dbcopy = new ProximityDBHashMap(10);
			dbcopy.setGeoLookup(copy);
			ProximityStreamDBImp<T> streamdb = new ProximityStreamDBImp(10);
			streamdb.setDb(dbcopy);
			return streamdb;
		}
		else {
			return null;
		}
	}

	@Override
	public Stream streamNearby(Attribute attr, Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		// Collection result from the db by calling  nearby in db
		Collection<DataAndPosition<T>> near = db.nearby(pos, bitsOfPrecision);
		
		// Lambda expression to filter based on the attr
		// whatever is filtered in is returned
		Collection<Attribute> allDataAttribute = 
				near.stream()
				.map(e -> {
					return (Attribute)e.getData();
				})
				.collect(Collectors.toList());
		// filter all attributes by the given attribute name
		Collection<Attribute> filteredAttributes = allDataAttribute
		.stream()
		.filter(s -> 
			s.getName().equals(attr.getName())
		)
		.collect(Collectors.toList());
		
		// find the min Attribute
		return filteredAttributes.stream();
	}

	@Override
	public OptionalDouble averageNearby(Attribute attr, Position pos, int bitsOfPrecision) {
		Stream<Attribute> nearby = streamNearby( attr,  pos,  bitsOfPrecision);
		OptionalDouble avg = nearby.mapToDouble(s -> (Double)s.getValue())
		.average();
		return avg;
	}

	@Override
	public OptionalDouble minNearby(Attribute attr, Position pos, int bitsOfPrecision) {
		Stream<Attribute> nearby = streamNearby( attr,  pos,  bitsOfPrecision);
		OptionalDouble min = nearby.mapToDouble(s -> (Double)s.getValue())
		.min();
		return min;
	}

	@Override
	public OptionalDouble maxNearby(Attribute attr, Position pos, int bitsOfPrecision) {
		Stream<Attribute> nearby = streamNearby( attr,  pos,  bitsOfPrecision);
		OptionalDouble max = nearby.mapToDouble(s -> (Double)s.getValue())
		.max();
		return max;
	}

	@Override
	public Map histogramNearby(Attribute attr, Position pos, int bitsOfPrecision) {
		Stream<Attribute> nearby = streamNearby( attr,  pos,  bitsOfPrecision);
//		int bin = 5;
//		min = minNearby( attr,  pos,  bitsOfPrecision);
//		max = maxNearby( attr,  pos,  bitsOfPrecision);
//		double div = (max - min) / bin;
		
		
		Map<Object, Long> histogram = 
				nearby.collect(
			    Collectors.groupingBy(
			       Attribute::getValue,
			      Collectors.counting()
			    ));
				// Group Attributes by values
			   //  Map<Double, Long> histogram
			    //     = nearby
			     //    .collect(Function.identity(),Collectors.groupingBy(Attribute::getValue));
		
//		Map<T, Long> histogram2 = nearby.collect(
//			        Collectors.groupingBy(Function.<T>identity(),
//			            Collectors.mapping(i -> 1, Collectors.summingInt(s -> s.intValue()))));
		
		return histogram;
	}


}
