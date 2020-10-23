package edu.vanderbilt.cs.live7;


import edu.vanderbilt.cs.live6.Position;
import edu.vanderbilt.cs.live6.ProximityDB;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * We are going to begin modifying our database to support basic querying and filtering
 * capabilities. To do this, we are going to add support for Java Streams.
 *
 * Each data item stored in the database now can have one or more attributes associated
 * with it. You can think of the data items as rows and the attributes as the columns/values
 * for that row. Each item can have an arbitrary number of attributes -- it does not
 * have to be consistent like in a DB.
 *
 * Because we want the database to be extensible and support arbitrary data item types,
 * we are going to use the strategy pattern to extract the attributes from data items.
 * Your modified database should be able to have an AttributesStrategy passed to it
 * that can introspect the individual data items in the database. The strategy is
 * free to decide what constitutes an attributes on a data item. Each attribute has a
 * name, type, and value (basically typed key/value pairs).
 *
 * In addition, we are adding support for rolling the database backward to past states.
 * The database should track all of the changes applied to it and support creating a
 * copy that represents the state up to the nth operation being performed (exclusive).
 * The nth operation is not included. So, databaseStateAtTime(1) only includes the
 * first operation (e.g., operation at index 0).
 *
 */
public interface ProximityStreamDB<T> extends ProximityDB<T> {

    /**
     * Provide a method to create a version of the database after the nth operation
     * was performed on it.
     *
     * For example, if you insert X, insert Y, insert Z on the database, then calling
     * databaseStateAtTime(2) would return the database state after insert Y was called.
     *
     * It is OK if your implementation isn't efficient. Think about how to use the command
     * pattern for a simple solution.
     *
     *
     * @Bonus
     *
     * See persistent data structures for a more fun solution (bonus points -- only attempt
     * if you complete the version above with the command pattern first)
     *
     * See: https://en.wikipedia.org/wiki/Persistent_data_structure
     *
     * If you go this route, I recommend that you use a tree and take the path copying
     * approach. Look at the "trees" subsection for a helpful diagram of what has to
     * happen before you modify the tree.
     *
     * @param n
     * @return
     */
    public ProximityStreamDB<T> databaseStateAtTime(int n);

    /**
     * Returns a stream of the values for the specified attribute that are near the specified
     * location.
     *
     * For example, you could stream the "salePrice" of all houses that are in a specific area
     * and it would return the stream of double values representing the price.
     *
     * Your implementation should use an AttributesStrategy to extract the attributes from each
     * nearby data item. You then need to return the values for only the attribute that was specified.
     *
     * @param attr
     * @param pos
     * @param bitsOfPrecision
     * @param <V>
     * @return
     */
    public <V> Stream<V> streamNearby(Attribute<V> attr, Position pos, int bitsOfPrecision);

    /**
     *
     * You will want to rely on your method above.
     *
     * Obtain the stream of attribute values and calculate the average value for the attribute.
     *
     * @param attr
     * @param pos
     * @param bitsOfPrecision
     * @param <V>
     * @return
     */
    public <V extends Double> OptionalDouble averageNearby(Attribute<V> attr, Position pos, int bitsOfPrecision);

    /**
     *
     * Obtain the stream of attribute values and calculate the min value for the attribute.
     *
     * @param attr
     * @param pos
     * @param bitsOfPrecision
     * @param <V>
     * @return
     */
    public <V extends Double> OptionalDouble minNearby(Attribute<V> attr, Position pos, int bitsOfPrecision);

    /**
     *
     * Obtain the stream of attribute values and calculate the max value for the attribute.
     *
     * @param attr
     * @param pos
     * @param bitsOfPrecision
     * @param <V>
     * @return
     */
    public <V extends Double> OptionalDouble maxNearby(Attribute<V> attr, Position pos, int bitsOfPrecision);

    /**
     *
     * Obtain the stream of attribute values and produce a histogram of the values.
     *
     * @param attr
     * @param pos
     * @param bitsOfPrecision
     * @param <V>
     * @return
     */
    public <V> Map<V,Long> histogramNearby(Attribute<V> attr, Position pos, int bitsOfPrecision);
}
