package edu.vanderbilt.cs.live9.expr;


import edu.vanderbilt.cs.live6.DataAndPosition;

import java.util.stream.Stream;

public class WhereExpression<T> implements Expression<T, Stream<DataAndPosition<T>>> {

    private Expression<T, Boolean> filterExpression;


    public Expression<T, Boolean> getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(Expression<T, Boolean> filterExpression) {
        this.filterExpression = filterExpression;
    }

    @Override
    public Stream<DataAndPosition<T>> evaluate(Context<T> ctx) {
        Stream<DataAndPosition<T>> target = (Stream<DataAndPosition<T>>)ctx.getTarget();

        return target.filter(e -> {ctx.setTarget(e); return filterExpression.evaluate(ctx);});
    }
}
