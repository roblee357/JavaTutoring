package edu.vanderbilt.cs.live9.expr;

import edu.vanderbilt.cs.live7.AttributesStrategy;
import edu.vanderbilt.cs.live7.ProximityStreamDB;

public class Context<T> {

    private Object target;

    private ProximityStreamDB<T> db;

    private AttributesStrategy attributesStrategy;

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public ProximityStreamDB<T> getDb() {
        return db;
    }

    public void setDb(ProximityStreamDB<T> db) {
        this.db = db;
    }

    public AttributesStrategy getAttributesStrategy() {
        return attributesStrategy;
    }

    public void setAttributesStrategy(AttributesStrategy attributesStrategy) {
        this.attributesStrategy = attributesStrategy;
    }
}
