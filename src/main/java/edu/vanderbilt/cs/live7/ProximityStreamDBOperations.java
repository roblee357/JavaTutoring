package edu.vanderbilt.cs.live7;


	import java.util.Map;
	import java.util.Random;
	import java.util.function.Function;
	import java.util.stream.Collectors;
	import java.util.stream.Stream;

	public class ProximityStreamDBOperations {
	  public static <T> Map<T, Integer> histogram(Stream<T> stream) {
	    return stream.collect(
	        Collectors.groupingBy(Function.<T>identity(),
	            Collectors.mapping(i -> 1, Collectors.summingInt(s -> s.intValue()))));
	  }

	  public static void main(String [] a) {
	    Map<Integer, Integer> histo = histogram(new Random().ints(1, 11).limit(10000).boxed());
	    System.out.println(histo);
	  }
	}

