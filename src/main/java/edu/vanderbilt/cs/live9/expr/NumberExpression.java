package edu.vanderbilt.cs.live9.expr;

public class NumberExpression<T> implements Expression<T,Double> {

    private final double number;

    public NumberExpression(double number) {
        this.number = number;
    }

    @Override
    public Double evaluate(Context<T> obj) {
        return number;
    }
}
