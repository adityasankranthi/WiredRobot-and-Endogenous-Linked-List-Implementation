package edu.uwm.cs351;

import java.util.Comparator;
import java.util.function.Consumer;

public class WiredRobot implements Robot {
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
	
	private boolean report(String error) {
		reporter.accept(error);
		System.out.println("DS: " + this);
		return false;
	}
	
	public static class FunctionalPart extends Part {
		public FunctionalPart() {
			super("W");
		}
		
		// the following fields are mutable:
		String function;
		FunctionalPart next;
		
		/**
		 * Return the function of this part.
		 * @return string of this part, null if this part not in a robot
		 */
		public String getFunction() {
			return function;
		}
		
		/**
		 * Return the next part in this robot.
		 * This is valid only if the part is in a robot.
		 * @return the next part, possibly null
		 * @exception IllegalStateException if this part is not a part of a robot
		 */
		public FunctionalPart getNext() {
			if (function == null) throw new IllegalStateException("not part of a robot");
			return next;
		}
	}
	
	private FunctionalPart dummy; // never null, with null function
	private Comparator<FunctionalPart> comparator; // never null
	private Comparator<FunctionalPart> nonDiscrimination = (p1,p2) -> 0;
	
	private boolean wellFormed() {
		// Invariant:
		// TODO
		// 1. The comparator is not null
		if (comparator == null) return report("Comparator is null");;
		// 2. the dummy is not null
		if (dummy == null) return report("Dummy is null");
		// 3. the dummy's function is null
		if (dummy.function != null) return report("Dummy's function is not null");
		// 4. The list starting after the dummy is null-terminated (no cycles)
		//    Use Floyd's Tortoise and Hare algorithm
		if (dummy.next != null) {
			FunctionalPart slow = dummy.next;
			FunctionalPart fast = dummy.next.next;
            while (fast != null) {
                if (slow == fast) return report("Found cycle in list");
                slow = slow.next;
                fast = fast.next;
                if (fast != null) fast = fast.next;
            }
        }
		// 5. All of the parts reachable from the dummy have non-null function
		FunctionalPart cursor = dummy.next;
		while (cursor != null) {
			if (cursor.function == null) return report("Part with null function found in the list");
			cursor = cursor.next;
		}
		// 6. The list starting after the dummy is in non-decreasing order 
		//    according to the comparator
		cursor = dummy.next;
	    while (cursor != null && cursor.next != null) {
	        if (comparator.compare(cursor, cursor.next) > 0) {
	            return report("List is not in non-decreasing order");
	        }
	        cursor = cursor.next;
	    }
		return true;
	}
	
	private WiredRobot(boolean ignore) {} // do not change this constructor

	/**
	 * Create a wired robot without parts
	 * and no order.
	 */
	public WiredRobot() {
		this(null);
	}
	
	/**
	 * Create a wired robot without parts
	 * with the given order 
	 * @param comp order to use, if null, then unordered
	 */
	public WiredRobot(Comparator<FunctionalPart> comp) {
		// TODO
		if (comp != null) this.comparator = comp;
		else this.comparator = nonDiscrimination;
        this.dummy = new FunctionalPart();
		assert wellFormed() : "Invariant not established by constructor";
	}
	
	/**
	 * Return the first part in this robot.
	 * @return the first part, null if this robot is empty
	 */
	public FunctionalPart getFirst() {
		 // TODO
		return dummy.next;
	}

	// TODO: the three robot methods
	@Override
	public boolean addPart(String function, Part part) {
		assert wellFormed(): "invariant broke before addPart";
		if (part == null || function == null) throw new NullPointerException("function or part is null");
		if(!(part instanceof FunctionalPart)) throw new IllegalArgumentException("parameter part must be a Functional Part");
	    FunctionalPart newPart = (FunctionalPart) part; 
	    if (newPart.function != null) throw new IllegalArgumentException("part is already in a robot");
	    FunctionalPart lag = dummy;
	    FunctionalPart cursor = getFirst();
	    newPart.function = function;
	    while (cursor != null && comparator.compare(cursor, newPart) < 0) {
	        lag = cursor;
	        cursor = cursor.next;
	    }
	    newPart.next = cursor;
	    lag.next = newPart;
	    assert wellFormed(): "invariant broke after addPart";
	    return true;
	}

	@Override
	public Part removePart(String function) {
	    assert wellFormed(): "invariant broke before removePart";
	    FunctionalPart result = null;
	    FunctionalPart cursor = getFirst();
	    FunctionalPart lag = dummy;
	    while (cursor != null) {
	        if (function == null || function.equals(cursor.function)) {
	            lag.next = cursor.next; 
	            cursor.next = null;
	            cursor.function = null; 
	            result = cursor;
	            break;
	        }
	        lag = cursor;
	        cursor = cursor.next;
	    }
	    assert wellFormed(): "invariant broke after removePart";
	    return result;
	}

	@Override
	public Part getPart(String function, int index) {
	    assert wellFormed(): "invariant broke before getPart";
	    if(index < 0) throw new IllegalArgumentException("Negative index");
	    Part result = null;
	    FunctionalPart cursor = getFirst();
	    int count = 0;
	    while (cursor != null) {
	        if (function == null || function.equals(cursor.function)) {
	            if (count == index) {
	                result = cursor;
	                break;
	            }
	            count++;
	        }
	        cursor = cursor.next;
	    }
	    assert wellFormed(): "invariant broke by getPart";
	    return result;
	}

	/**
	 * Change the comparator used to order the robot parts.
	 * The parts will be reorganized as necessary to accomodate the new order,
	 * but two parts will be reordered only if necessary.
	 * (The sorting is "stable".) 
	 * @param comp comparator to use, if null, then henceforth the parts
	 * can be in any order.
	 */
	public void setComparator(Comparator<FunctionalPart> comp) {
		assert wellFormed() : "invariant broken in setComparator";
		if (comp != null) this.comparator = comp;
		else this.comparator = nonDiscrimination;
		// TODO: Complete this!
		// Hint: After handling special easy cases,
		//   reverse the list
		//   and then insert the parts again
		//   The solution avoids code duplication by factoring out
		//   the insertion code in "addPart" into a helper method
		//   that we can call here.
		assert wellFormed() : "invariant broken by setComparator";
	}
	

	/**
	 * Class for internal testing.  Do not modify.
	 * Do not use in client/application code
	 */
	public static class Spy {
		/**
		 * A public versio of the data structure's internal node class.
		 * This class is only used for testing.
		 */
		public static class Part extends FunctionalPart {
			/**
			 * Create a part with null function and null next fields.
			 */
			public Part() {
				this(null, null);
			}
			
			/**
			 * Create a part with the given values
			 * @param f function for new part, may be null
			 * @param n next for new part, may be null
			 */
			public Part(String f, Part n) {
				super();
				this.function = f;
				this.next = n;
			}
		}
		
		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}

		/**
		 * Create a part for testing.
		 * @param f function, may be null
		 * @param n next part, may be null
		 * @return newly created test node
		 */
		public Part newPart(String f, Part n) {
			return new Part(f, n);
		}
		
		/**
		 * Set the function of a spy part
		 * @param p part to set, must not be null
		 * @param f function to set it to
		 */
		public void setFunction(Part p, String f) {
			p.function = f;
		}
		
		/**
		 * Change a part's next field
		 * @param n1 part to change, must not be null
		 * @param n2 part to point to, may be null
		 */
		public void setNext(Part n1, Part n2) {
			n1.next = n2;
		}
		
		/**
		 * Create an instance of the ADT with given data structure.
		 * This should only be used for testing.
		 * @param d dummy
		 * @param c comparator
		 * @return instance of WiredRobot with the given field values.
		 */
		public WiredRobot create(Part d, Comparator<FunctionalPart> c) {
			WiredRobot result = new WiredRobot(false);
			result.dummy = d;
			result.comparator = c;
			return result;
		}
		
		/**
		 * Return whether the wellFormed routine returns true for the argument
		 * @param s robot to check.
		 * @return
		 */
		public boolean wellFormed(WiredRobot s) {
			return s.wellFormed();
		}

		
	}

}