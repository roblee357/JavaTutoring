package edu.vanderbilt.cs.live9.expr;

import edu.vanderbilt.cs.live6.DataAndPosition;

import java.util.Map;
import java.util.stream.Stream;

public class FindExpression<T> implements Expression<T, Stream<DataAndPosition<T>>> {

    private NearExpression near;
    private WhereExpression where;

    public FindExpression(NearExpression near, WhereExpression where) {
        this.near = near;
        this.where = where;
    }

    @Override
    public Stream<DataAndPosition<T>> evaluate(Context<T> ctx) {
        Stream<DataAndPosition<Map<String,?>>> nearby = near.evaluate(ctx);
        ctx.setTarget(nearby);
        return where.evaluate(ctx);
    }
}
