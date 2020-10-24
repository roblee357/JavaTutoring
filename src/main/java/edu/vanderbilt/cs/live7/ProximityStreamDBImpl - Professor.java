package edu.vanderbilt.cs.live7;

import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live6.Position;
import edu.vanderbilt.cs.live6.tree.ProximityDBTree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProximityStreamDBImpl<T> implements ProximityStreamDB<T> {

    private final List<Command<T,?>> transactionLog = new ArrayList<>();

    private final ProximityDBTree<T> db;

    private final AttributesStrategy<T> attributes;

    public ProximityStreamDBImpl(ProximityDBTree<T> db, AttributesStrategy<T> attributes) {
        this.db = db;
        this.attributes = attributes;
    }

    private <R> R execute(Command<T,R> action){
        R result = action.execute(this.db);
        transactionLog.add(action);
        return result;
    }

    public void insert(DataAndPosition<T> e) {

        execute(new InsertCommand(e));
    }

    public Collection<DataAndPosition<T>> delete(Position pos) {
        return execute(new DeleteCommand<>(pos));
    }

    public Collection<DataAndPosition<T>> delete(Position pos, int bitsOfPrecision) {
        return execute(new DeleteCommand<>(pos, bitsOfPrecision));
    }

    public boolean contains(Position pos, int bitsOfPrecision) {
        return db.contains(pos, bitsOfPrecision);
    }

    public Collection<DataAndPosition<T>> nearby(Position pos, int bitsOfPrecision) {
        return db.nearby(pos, bitsOfPrecision);
    }

    @Override
    public ProximityStreamDB<T> databaseStateAtTime(int t) {
        ProximityDBTree<T> treecopy = new ProximityDBTree<T>(this.db.getBitsOfPrecision());
        ProximityStreamDBImpl<T> copy = new ProximityStreamDBImpl<>(treecopy, this.attributes);
        transactionLog.subList(0, t).forEach(cmd -> copy.execute(cmd));
        return copy;
    }

    @Override
    public <V> Stream<V> streamNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
        return nearby(pos,bitsOfPrecision).stream()
                .flatMap(posdata -> attributes.getAttributes(posdata.getData()).stream())
                .filter(a -> attr.getName().equals(a.getName()))
                .map(a -> (V) a.getValue());
    }

    @Override
    public <V extends Double> OptionalDouble averageNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
        return streamNearby(attr,pos,bitsOfPrecision).mapToDouble(v -> v).average();
    }

    @Override
    public <V extends Double> OptionalDouble minNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
        return streamNearby(attr,pos,bitsOfPrecision).mapToDouble(v -> v).min();
    }

    @Override
    public <V extends Double> OptionalDouble maxNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
        return streamNearby(attr,pos,bitsOfPrecision).mapToDouble(v -> v).max();
    }

    @Override
    public <V> Map<V, Long> histogramNearby(Attribute<V> attr, Position pos, int bitsOfPrecision) {
        return streamNearby(attr,pos,bitsOfPrecision)
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()));
    }

}
