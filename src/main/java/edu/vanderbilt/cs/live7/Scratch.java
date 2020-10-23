package edu.vanderbilt.cs.live7;

import edu.vanderbilt.cs.live6.ProximityDBHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live6.Position;
import edu.vanderbilt.cs.live7.Attribute;
public class Scratch<T> {
	
	
	DataAndPosition<T> dap1;
	DataAndPosition<T> dap2;
	DataAndPosition<T> dap3;
	DataAndPosition<T> dap4;
	Position p1;
	
	
	//ProximityDBHashMap<T> db = new ProximityDBHashMap(10);
	ProximityStreamDBImp<T> db = new ProximityStreamDBImp(10);
	
	public Scratch () {
		p1 = Position.with(12,13);
		dap1 = (DataAndPosition<T>) DataAndPosition.with(12,13,new Attribute("salesPrice",Double.class,12000.00));
		dap2 = (DataAndPosition<T>) DataAndPosition.with(12,13,new Attribute("salesPrice",Double.class,13000.00));
		dap3 = (DataAndPosition<T>) DataAndPosition.with(12,13,new Attribute("salesPrice",Double.class,13000.00));
		dap4 = (DataAndPosition<T>) DataAndPosition.with(12,13,new Attribute("salesPrice",Double.class,14000.00));
	db.insert(  dap1);
	db.insert(  dap2);
	db.insert(  dap3);
	Map<Double, Long> map = db.histogramNearby(new Attribute("salesPrice",Double.class,19000.00),p1,10);
	System.out.println("histogram is " + map);
	ProximityStreamDB dbPreviousCopy = db.databaseStateAtTime(3);
	Map<Double, Long> map2 = dbPreviousCopy.histogramNearby(new Attribute("salesPrice",Double.class,19000.00),p1,10);
	System.out.println("histogram is " + map2);
	
	db.insert(  dap4);
	
//	OptionalDouble min = db.minNearby(new Attribute("salesPrice",Double.class,19000.00), p1, 10) ;
//	System.out.println("min is " + min.getAsDouble());
//	OptionalDouble max = db.maxNearby(new Attribute("salesPrice",Double.class,19000.00), p1, 10) ;
//	System.out.println("max is " + max.getAsDouble());
//	OptionalDouble avg = db.averageNearby(new Attribute("salesPrice",Double.class,19000.00), p1, 10) ;
//	System.out.println("average is " + avg.getAsDouble());
//	Map<Double, Long> map = db.histogramNearby(new Attribute("salesPrice",Double.class,19000.00),p1,10);
//	System.out.println("histogram is " + map);
//	List<String> sampleList = new ArrayList<>();
//	sampleList.add("This");
//	sampleList.add("That");
//	sampleList.add("These");
//	
//	//sampleList.forEach(e -> System.out.println(e));
//	System.out.println(sampleList.get(0));// first to be added is the first in line
//	System.out.println(sampleList.get(2));// last to be added is the last in line
//	// To search for the latest addtion, we should start from the last index
	}
	
	public static void main(String[] a) {
		new Scratch();
	}
}
