import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.WiredRobot;
import edu.uwm.cs351.WiredRobot.FunctionalPart;
import edu.uwm.cs351.WiredRobot.Spy.Part;
import junit.framework.TestCase;

public class TestInvariant extends TestCase {
	public static Comparator<FunctionalPart> nonDiscrimination = (p1,p2) -> 0;
	public static Comparator<FunctionalPart> byFunctionThenId = (p1,p2) -> {
		int c = p1.getFunction().compareTo(p2.getFunction());
		if (c != 0) return c;
		return p1.getId() - p2.getId();
	};
	
	protected WiredRobot.Spy spy;
	protected int reports;
	protected WiredRobot r;
    
    protected void assertReporting(boolean expected, Supplier<Boolean> test) {
            reports = 0;
            Consumer<String> savedReporter = spy.getReporter();
            try {
                    spy.setReporter((String message) -> {
                            ++reports;
                            if (message == null || message.trim().isEmpty()) {
                                    assertFalse("Uninformative report is not acceptable", true);
                            }
                            if (expected) {
                                    assertFalse("Reported error incorrectly: " + message, true);
                            }
                    });
                    assertEquals(expected, test.get().booleanValue());
                    if (!expected) {
                            assertEquals("Expected exactly one invariant error to be reported", 1, reports);
                    }
                    spy.setReporter(null);
            } finally {
                    spy.setReporter(savedReporter);
            }
    }
    
    protected void assertWellFormed(boolean expected, WiredRobot r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}
    
    @Override
    protected void setUp() {
		spy = new WiredRobot.Spy();
	}
    
    // tests of null values/list creation
    public void testA01() {
		r = spy.create(null, null);
		assertWellFormed(false, r);
	}
    
    public void testA02() {
    	Part d0 = spy.newPart(null, null);
		r = spy.create(d0, null);
		assertWellFormed(false, r);
	}
    
    public void testA03() {
		r = spy.create(null, nonDiscrimination);
		assertWellFormed(false, r);
	}
    
    public void testA04() {
    	Part d0 = spy.newPart(null, null);
		r = spy.create(d0, nonDiscrimination);
		assertWellFormed(true, r);
	}
    
    public void testA05() {
    	Part d0 = spy.newPart("1", null);
		r = spy.create(d0, nonDiscrimination);
		assertWellFormed(false, r);
	}
    
    // tests of Cycles
    public void testB01() { 
		Part d0 = spy.newPart(null, null);
		
		spy.setNext(d0, d0);
		r = spy.create(d0, nonDiscrimination);
		assertWellFormed(false, r);
	}
    
    public void testB02() { 
    	Part f2 = spy.newPart("2" ,null);
		Part f1 = spy.newPart("1", f2);
		Part d0 = spy.newPart(null, f1);
		
		spy.setNext(f2, f1);
		r = spy.create(d0, nonDiscrimination);
		assertWellFormed(false, r);
	}
    
    public void testB03() {
    	Part f2 = spy.newPart("2" ,null);
		Part f1 = spy.newPart("1", f2);
		Part d0 = spy.newPart(null, f1);
		
		spy.setNext(f2, d0);
		r = spy.create(d0, nonDiscrimination);
		assertWellFormed(false, r);
	}
    
    public void testB04() {
		Part f2 = spy.newPart("2" ,null);
		Part f1 = spy.newPart("1", f2);
		Part d0 = spy.newPart(null, f1);
		
		spy.setNext(f2, d0);
		r = spy.create(d0, nonDiscrimination);
		assertWellFormed(false, r);
	}
    
    public void testB05() {
    	Part f3 = spy.newPart("3", null);
    	Part f2 = spy.newPart("2", f3);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	spy.setNext(f1, f1);
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(false, r);
    }
    
    public void testB06() {
    	Part f3 = spy.newPart("3", null);
    	Part f2 = spy.newPart("2", f3);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	spy.setNext(f2, f1);
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(false, r);
    }
    
    public void testB07() {
    	Part f3 = spy.newPart("3", null);
    	Part f2 = spy.newPart("2", f3);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	spy.setNext(d0, d0);
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(false, r);
    }
    
    // Tests of function fields in all parts
    public void testC01() {
    	Part f1 = spy.newPart("1", null);
    	Part d0 = spy.newPart(null, f1);
    	
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(true, r);
    }
    
    public void testC02() {
    	Part f1 = spy.newPart(null, null);
    	Part d0 = spy.newPart(null, f1);
    	
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(false, r);
    }
    
    public void testC03() {
    	Part f2 = spy.newPart("2", null);
    	Part f1 = spy.newPart(null, null);
    	Part d0 = spy.newPart(null, f2);
    	
    	spy.setNext(f1, f2);
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(true, r);
    }
    
    public void testC04() {
    	Part f4 = spy.newPart("4", null);
    	Part f3 = spy.newPart("3", f4);
    	Part f2a = spy.newPart(null, f3);
    	Part f2 = spy.newPart("2", f3);
    	Part f1 = spy.newPart("1", f2a);
    	Part d0 = spy.newPart(null, f1);
    	
    	spy.setNext(f2, d0);
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(false, r);
    }
    
    public void testC05() {
    	Part f4 = spy.newPart("4", null);
    	Part f3 = spy.newPart("3", f4);
    	Part f2a = spy.newPart(null, f3);
    	Part f2 = spy.newPart("2", f3);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	spy.setNext(f2a, f2);
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(true, r);
    }
    
    public void testC06() {
    	Part f4 = spy.newPart("4", null);
    	Part f3 = spy.newPart("3", f4);
    	Part f2a = spy.newPart("2", f3);
    	Part f2 = spy.newPart("2", f2a);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(true, r);
    }
    
    // Tests of Comparator
    public void testD01() {
    	Part f1 = spy.newPart("1", null);
    	Part d0 = spy.newPart(null, f1);
    	
    	r = spy.create(d0, byFunctionThenId);
    	assertWellFormed(true, r);
    }
    
    public void testD02() {
    	Part f2 = spy.newPart("2", null);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(true, r);
    }
    
    public void testD03() {
    	Part f2 = spy.newPart("2", null);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	r = spy.create(d0, byFunctionThenId);
    	assertWellFormed(true, r);
    }
    
    public void testD04() {
    	Part f1 = spy.newPart("1", null);
    	Part f2 = spy.newPart("2", f1);
    	Part d0 = spy.newPart(null, f2);
    	
    	r = spy.create(d0, byFunctionThenId);
    	assertWellFormed(false, r);
    }
    
    public void testD05() {
    	Part f1 = spy.newPart("1", null);
    	Part f2 = spy.newPart("2", f1);
    	Part d0 = spy.newPart(null, f2);
    	
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(true, r);
    }
    
    public void testD06() {
    	Part f5 = spy.newPart("5", null);
    	Part f4 = spy.newPart("4", f5);
    	Part f3 = spy.newPart("3", f4);
    	Part f2 = spy.newPart("2", f3);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	r = spy.create(d0, byFunctionThenId);
    	assertWellFormed(true, r);
    }
    
    public void testD07() {
    	Part f5 = spy.newPart("5", null);
    	Part f4 = spy.newPart("4", f5);
    	Part f3 = spy.newPart("3", f4);
    	Part f2 = spy.newPart("2", f3);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	spy.setNext(d0, f2);
    	spy.setNext(f2, f1);
    	spy.setNext(f1, f3);
    	r = spy.create(d0, byFunctionThenId);
    	assertWellFormed(false, r);
    }
    
    public void testD08() {
    	Part f5 = spy.newPart("5", null);
    	Part f4 = spy.newPart("4", f5);
    	Part f3 = spy.newPart("3", f4);
    	Part f2 = spy.newPart("2", f3);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    	
    	spy.setNext(d0, f2);
    	spy.setNext(f2, f1);
    	spy.setNext(f1, f3);
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(true, r);
    }
    
    public void testD09() {
    	Part f5 = spy.newPart("5", null);
    	Part f4 = spy.newPart("4", f5);
    	Part f3 = spy.newPart("3", f4);
    	Part f2a = spy.newPart("2", f3);
    	Part f2 = spy.newPart("2", f2a);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    
    	r = spy.create(d0, nonDiscrimination);
    	assertWellFormed(true, r);
    }
    
    public void testD10() {
    	Part f5 = spy.newPart("5", null);
    	Part f4 = spy.newPart("4", f5);
    	Part f3 = spy.newPart("3", f4);
    	Part f2a = spy.newPart("2", f3);
    	Part f2 = spy.newPart("2", f2a);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    
    	r = spy.create(d0, byFunctionThenId);
    	assertWellFormed(false, r);
    }
    
    public void testD11() {
    	Part f5 = spy.newPart("5", null);
    	Part f4 = spy.newPart("4", f5);
    	Part f3 = spy.newPart("3", f4);
    	Part f2a = spy.newPart("2", f3);
    	Part f2 = spy.newPart("2", f2a);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    
    	spy.setNext(f1, f2a);
    	spy.setNext(f2a, f2);
    	spy.setNext(f2, f3);
    	
    	r = spy.create(d0, byFunctionThenId);
    	assertWellFormed(true, r);
    }
    
    public void testD12() {
    	Part f5a = spy.newPart("5", null);
    	Part f5 = spy.newPart("5", null);
    	Part f4 = spy.newPart("4", f5a);
    	Part f3a = spy.newPart("3", f4);
    	Part f3 = spy.newPart("3", f3a);
    	Part f2a = spy.newPart("2", f3);
    	Part f2 = spy.newPart("2", f2a);
    	Part f1 = spy.newPart("1", f2);
    	Part d0 = spy.newPart(null, f1);
    
    	spy.setNext(f5a, f5);
    	
    	spy.setNext(f1, f2a);
    	spy.setNext(f2a, f2);
    	spy.setNext(f2, f3);
    	
    	spy.setNext(f2, f3a);
    	spy.setNext(f3a, f3);
    	spy.setNext(f3, f4);
    	
    	r = spy.create(d0, byFunctionThenId);
    	assertWellFormed(true, r);
    }
}