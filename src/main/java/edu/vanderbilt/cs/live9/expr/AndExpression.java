package edu.vanderbilt.cs.live9.expr;

public class AndExpression<T> implements Expression<T,Boolean>{

    private Expression<T,Boolean> leftChild;
    private Expression<T,Boolean> rightChild;

    public Expression<T, Boolean> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Expression<T, Boolean> leftChild) {
        this.leftChild = leftChild;
    }

    public Expression<T, Boolean> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Expression<T, Boolean> rightChild) {
        this.rightChild = rightChild;
    }

    public Boolean evaluate(Context<T> ctx) {
        return leftChild.evaluate(ctx) && rightChild.evaluate(ctx);
    }
}
