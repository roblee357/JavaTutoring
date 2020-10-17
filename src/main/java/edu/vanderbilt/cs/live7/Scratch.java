package edu.vanderbilt.cs.live7;

import edu.vanderbilt.cs.live6.ProximityDBHashMap;
import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live6.Position;

public class Scratch<T> {
	
	
	DataAndPosition<T> dap1;
	DataAndPosition<T> dap2;
	DataAndPosition<T> dap3;
	DataAndPosition<T> dap4;
	Position p1;
	
	
	ProximityDBHashMap<T> db = new ProximityDBHashMap(10);
	public Scratch () {
		dap1 = (DataAndPosition<T>) DataAndPosition.with(12,13,"this");
		dap2 = (DataAndPosition<T>) DataAndPosition.with(12,13,new int[] {1,2,3,4,5});
		dap3 = (DataAndPosition<T>) DataAndPosition.with(12,13,"the other");
		dap4 = (DataAndPosition<T>) DataAndPosition.with(12,13,"Some more");
	db.insert(  dap1);
	p1 = Position.with(15,13);
	System.out.println(db.contains(p1, 15));
	}
	
	public static void main(String[] a) {
		new Scratch();
	}
}
