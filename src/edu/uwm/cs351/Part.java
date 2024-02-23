package edu.uwm.cs351;

/**
 * A part of a robot.
 * Each instance is unique.
 * Unlike the ADT from Homework #1,
 * it has a model and a unique id
 * which are put together for a serial number
 */
public class Part {
	public static final String DEFAULT_MODEL = "P";
	private static int unique = 0;
	
	private final String model; // never null
	private final int id = ++unique;
	
	/**
	 * Create a part with the given model
	 * @param model must not be null
	 */
	public Part(String model) {
		if (model == null) throw new NullPointerException("null is not a serial number");
		this.model = model;
	}
	
	/**
	 * Create a part with a default model
	 */
	public Part() {
		this(DEFAULT_MODEL);
	}
	
	@Override // implementation
	public String toString() {
		return "Part(" + getSerial() + ")";
	}
	
	/**
	 * Return the serial number of this part.
	 */
	public String getSerial() {
		return model + "-" + id;
	}
	
	/**
	 * Return the unique id of the part.
	 * @return a unique integer
	 */
	public int getId() {
		return id;
	}
}
