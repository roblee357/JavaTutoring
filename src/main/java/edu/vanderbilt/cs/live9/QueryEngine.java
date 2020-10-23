package edu.vanderbilt.cs.live9;

import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live7.AttributesStrategy;
import edu.vanderbilt.cs.live7.ProximityStreamDB;
import edu.vanderbilt.cs.live9.ast.ExpressionNode;
import edu.vanderbilt.cs.live9.ast.visitor.PrintVisitor;
import edu.vanderbilt.cs.live9.expr.*;

import java.util.Map;
import java.util.stream.Stream;

public class QueryEngine {

    public static <T> Stream<DataAndPosition<T>> execute(ProximityStreamDB<T> db, AttributesStrategy<T> attrs, String querystr){
        Expression<T, Stream<DataAndPosition<T>>> root = parseQuery(querystr);
        Context ctx = new Context<>();
        ctx.setAttributesStrategy(attrs);
        ctx.setDb(db);
        return root.evaluate(ctx);
    }

    private static <T> Expression<T, Stream<DataAndPosition<T>>> parseQuery(String querystr) {

        // @ToDo
        //
        // Implement an AstVisitor that builds the correct query expression tree for
        // a given AST
        //
        // Use your visitor here to return the root expression for the query.
        //
        // The structure of the query language is:
        //
        // (find
        //    (near -45.0 -145.0 2)
        //    (where
        //       (> :height 8)
        //     )
        //  )
        //
        // ALL queries will have find, near, and where expressions.
        // For simplicity, only > is supported. You can also have
        // "and" combining > expressions:
        //
        // (find
        //    (near -45.0 -145.0 2)
        //    (where
        //       (and (> :height 8)
        //            (> :age 50)
        //     )
        //  )
        //
        // The third parameter to "near" is the bits of precision.
        //
        // Any terms prefixed with ":" are references to an attribute.
        //
        // The main(...) method below shows how to build the first query
        // above manually using the expression classes.
        //
        // The QueryParser code below produces the abstract syntax tree
        // for a query string.
        //
        // Your job is to write a visitor that traverses the abstract syntax
        // tree and constructs the appropriate expressions to implement the
        // query.
        //
        // To help you figure out the structure of the abstract syntax tree,
        // I would recommend putting in a breakpoint after the "raw"
        // ExpressionNode is created so that you can see what the tree
        // looks like. You can also use the PrintVisitor to dump the
        // abstract syntax tree to the console to figure out its structure.
        //
        // The main() method below both prints the abstract syntax tree for a
        // query and manually constructs the FindExpression that the abstract
        // syntax tree would be translated into by your visitor. That is, if your
        // visitor was given the abstract syntax tree printed by the main method,
        // it would generate a FindExpression equivalent to what is shown in the
        // main method.

        ExpressionNode raw = QueryParser.parse(querystr);
        // This will print out the abstract syntax tree
        // raw.accept(new PrintVisitor());

        // You have to a QueryVisitor and use it like this to
        // complete the QueryEngine:
        //
        // QueryVisitor queryv = new QueryVisitor();
        // raw.accept(queryv);
        // return queryv.getRoot()

        return null;
    }

    public static DataAndPosition<Map<String,?>> data(Map<String,?> m){
        return new DataAndPosition<Map<String, ?>>() {
            @Override
            public Map<String, ?> getData() {
                return m;
            }

            @Override
            public double getLatitude() {
                return (Double)m.get("lat");
            }

            @Override
            public double getLongitude() {
                return (Double)m.get("lon");
            }
        };
    }

    public static void main(String[] args){

        // This is how the abstract syntax tree is generated:
        ExpressionNode expr = QueryParser.parse(
            "(find " +
                    "     (near -45.0 -145.0 2) " +
                    "     (where " +
                    "          (> :height 8)" +
                    "     )" +
                    ")");

        // This will print out the tree structure for you to the console
        expr.accept(new PrintVisitor());

        // Your visitor should be able to translate the abstract syntax
        // tree that you see printed in the console to the equivalent of
        // the hand constructed FindExpression below.

        // Here is an example of building a FindExpression (query) manually:
        //
        // The next part is going to be hand-constructing
        // the equivalent of this query:
        //
        //               (find
        //                      (near -45.0 -145.0 2)
        //                      (where
        //                         (> :height 8)
        //                     )
        //                )
        //
        NumberExpression lat = new NumberExpression(-45.0);
        NumberExpression lon = new NumberExpression(-145.0);
        NumberExpression bits = new NumberExpression(2);

        NearExpression near = new NearExpression();
        near.setLeftChild(lat);
        near.setMiddleChild(lon);
        near.setRightChild(bits);

        WhereExpression where = new WhereExpression();
        AttributeValueExpression av = new AttributeValueExpression("height");
        NumberExpression val = new NumberExpression(8);
        GreaterThanExpression gt = new GreaterThanExpression();
        gt.setLeftChild(av);
        gt.setRightChild(val);

        where.setFilterExpression(gt);

        FindExpression find = new FindExpression(near, where);


        // Now, to evaluate the query against a database, we have
        // to create a database and context to execute the query.
        DataAndPosition data = data(Map.of(
                "height", 10.0,
                "age", 32.0,
                "lat", -90.0,
                "lon", -180.0
        ));

        DataAndPosition data2 = data(Map.of(
                "age", 56.0,
                "height", 8.0,
                "lat", -90.0,
                "lon", -180.0
        ));

        ProximityStreamDB db = null; // replace with your implementation and using a MapAttributesStrategy
        db.insert(data);
        db.insert(data2);
        Context<Map<String,?>> ctx = new Context<>();
        ctx.setAttributesStrategy(new MapAttributesStrategy());
        ctx.setDb(db);

        // Finally, we can execute the query with the context.
        Stream<DataAndPosition<Map<String,?>>> result = find.evaluate(ctx);

        // Print the result of the query
        result.forEach(m -> System.out.println(m.getLatitude() + "," + m.getLongitude() + " -- " + m.getData()));



        // This should produce the same result as the manually created
        // query above
        Stream<DataAndPosition<Map<String,?>>> result2 =
                QueryEngine.execute(db,
                        new MapAttributesStrategy(),
                        "(find " +
                                "     (near -45.0 -145.0 2) " +
                                "     (where " +
                                "          (> :height 8)" +
                                "     )" +
                                ")");

        result2.forEach(m -> System.out.println(m.getLatitude() + "," + m.getLongitude() + " -- " + m.getData()));
    }

}
