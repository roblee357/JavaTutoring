package edu.vanderbilt.cs.live9.expr;

public class GreaterThanExpression<T> implements Expression<T,Boolean> {

    private Expression leftChild;
    private Expression rightChild;

    public Expression getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Expression leftChild) {
        this.leftChild = leftChild;
    }

    public Expression getRightChild() {
        return rightChild;
    }

    public void setRightChild(Expression rightChild) {
        this.rightChild = rightChild;
    }

    @Override
    public Boolean evaluate(Context<T> ctx) {
        Double lhs = (Double)leftChild.evaluate(ctx);
        Double rhs = (Double)rightChild.evaluate(ctx);

        return lhs > rhs;
    }
}
