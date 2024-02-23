import java.util.Random;

import edu.uwm.cs.junit.EfficiencyTestCase;
import edu.uwm.cs351.WiredRobot;
import edu.uwm.cs351.WiredRobot.FunctionalPart;

public class TestEfficiency extends EfficiencyTestCase {
	
	WiredRobot robot;
	Random r;
	
	@Override
	public void setUp() {
		robot = new WiredRobot();
		r = new Random();
		try {
			assert 1/robot.getPart(null,0).hashCode() == 42 : "OK";
			assertTrue(true);
		} catch (NullPointerException ex) {
			System.err.println("Assertions must NOT be enabled to use this test suite.");
			System.err.println("In Eclipse: remove -ea from the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
	}

	private static final int POWER = 20;
	private static final int MAX_LENGTH = 2 << POWER;
	
	private static final String[] FUNCTIONS = {
			"arm", "head", "leg", "screen", "speaker", "tail"
	};
	
	protected int size() {
		int count = 0;
		for (FunctionalPart p = robot.getFirst(); p != null; p = p.getNext()) {
			++count;
		}
		return count;
	}
	
	public void test0() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			robot.addPart("arm", new FunctionalPart());
		}
		assertEquals(MAX_LENGTH, size());
	}
	
	public void test1() {
		robot.setComparator(RandomTest.byFunction);
		FunctionalPart begin = new FunctionalPart();
		FunctionalPart end = new FunctionalPart();
		robot.addPart("arm", begin);
		for (int i=0; i < MAX_LENGTH-2; ++i) {
			robot.addPart("head", new FunctionalPart());
		}
		robot.addPart("leg", end);
		assertSame(begin, robot.getFirst());
	}
	
	public void test2() {
		FunctionalPart begin = new FunctionalPart();
		FunctionalPart end = new FunctionalPart();
		robot.addPart("arm", begin);
		for (int i=0; i < MAX_LENGTH-2; ++i) {
			robot.addPart("head", new FunctionalPart());
		}
		robot.addPart("leg", end);
		robot.setComparator(RandomTest.byFunction);
		assertSame(begin, robot.getFirst());
	}
	
	public void test3() {
		int n = FUNCTIONS.length;
		FunctionalPart[] saved = new FunctionalPart[n];
		for (int i=0; i < MAX_LENGTH; ++i) {
			FunctionalPart p = new FunctionalPart();
			saved[i%n] = p;
			robot.addPart(FUNCTIONS[i%n], p);
		}
		for (int i=0; i < MAX_LENGTH/n; ++i) { // getPart is O(n) in this case
			assertSame(saved[i%n], robot.getPart(FUNCTIONS[i%n],0));
		}
	}
	
	public void test4() {
		robot.setComparator(RandomTest.byFunction);
		FunctionalPart begin = new FunctionalPart();
		FunctionalPart end = new FunctionalPart();
		robot.addPart("arm", begin);
		for (int i=0; i < MAX_LENGTH-2; ++i) {
			robot.addPart("head", new FunctionalPart());
		}
		robot.addPart("leg", end);
		assertSame(begin, robot.removePart(null));
		for (int i=0; i < MAX_LENGTH-2; ++i) {
			assertNotNull(robot.removePart("head"));
		}
		assertSame(end, robot.removePart(null));
	}
	
	public void test5() {
		robot.setComparator(RandomTest.byFunction);
		FunctionalPart begin = new FunctionalPart();
		FunctionalPart end = new FunctionalPart();
		robot.addPart("arm", begin);
		for (int i=0; i < MAX_LENGTH-2; ++i) {
			robot.addPart("head", new FunctionalPart());
		}
		robot.addPart("leg", end);
		for (int i=0; i < MAX_LENGTH-2; ++i) {
			assertNotNull(robot.removePart("head"));
		}
		assertSame(end, robot.removePart("leg"));
		assertSame(begin, robot.removePart("arm"));
	}
	
	public void test6() {
		robot.setComparator(RandomTest.youngerFirst);
		for (int i=0; i < MAX_LENGTH; ++i) {
			robot.addPart("arm", new FunctionalPart());
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			robot.setComparator(RandomTest.youngerFirst);
		}
	}
	
	public void test7() {
		robot.setComparator(RandomTest.youngerFirst);
		for (int i=0; i < MAX_LENGTH; ++i) {
			robot.addPart("arm", new FunctionalPart());
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			robot.setComparator(null);
		}
	}
}
