package edu.vanderbilt.cs.live7;



import java.util.Collection;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Stream;

import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live6.Position;
import edu.vanderbilt.cs.live6.ProximityDBHashMap;

public abstract class ProximityStreamDBImp implements ProximityStreamDB<T> {

	public void insert(DataAndPosition<T> data) {
		// TODO Auto-generated method stub
		ProximityDBHashMap.insert( data);
	}

	@Override
	public Collection<DataAndPosition<T>> delete(Position pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Collection<DataAndPosition<T>> delete(Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<DataAndPosition<T>> nearby(Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProximityStreamDB<T> databaseStateAtTime(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> Stream<V> streamNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V extends Double> OptionalDouble averageNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V extends Double> OptionalDouble minNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V extends Double> OptionalDouble maxNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> Map<V, Long> histogramNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
		// TODO Auto-generated method stub
		return null;
	}

}
