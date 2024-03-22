/**
 * @author Mehrdad Sabetzadeh, University of Ottawa
 *
 */
public class Simulator {

	/**
	 * Length of car plate numbers
	 */
	public static final int PLATE_NUM_LENGTH = 3;

	/**
	 * Number of seconds in one hour
	 */
	public static final int NUM_SECONDS_IN_1H = 3600;

	/**
	 * Maximum duration a car can be parked in the lot
	 */
	public static final int MAX_PARKING_DURATION = 8 * NUM_SECONDS_IN_1H;

	/**
	 * Total duration of the simulation in (simulated) seconds
	 */
	public static final int SIMULATION_DURATION = 24 * NUM_SECONDS_IN_1H;

	/**
	 * The probability distribution for a car leaving the lot based on the duration
	 * that the car has been parked in the lot
	 */
	public static final TriangularDistribution departurePDF = new TriangularDistribution(0, MAX_PARKING_DURATION / 2,
			MAX_PARKING_DURATION);

	/**
	 * The probability that a car would arrive at any given (simulated) second
	 */
	private Rational probabilityOfArrivalPerSec;

	/**
	 * The simulation clock. Initially the clock should be set to zero; the clock
	 * should then be incremented by one unit after each (simulated) second
	 */
	private int clock;

	/**
	 * Total number of steps (simulated seconds) that the simulation should run for.
	 * This value is fixed at the start of the simulation. The simulation loop
	 * should be executed for as long as clock < steps. When clock == steps, the
	 * simulation is finished.
	 */
	private int steps;

	/**
	 * Instance of the parking lot being simulated.
	 */
	private ParkingLot lot;

	/**
	 * Queue for the cars wanting to enter the parking lot
	 */
	private Queue<Spot> incomingQueue;

	/**
	 * Queue for the cars wanting to leave the parking lot
	 */
	private Queue<Spot> outgoingQueue;

	/**
	 * @param lot   is the parking lot to be simulated
	 * @param steps is the total number of steps for simulation
	 */
	public Simulator(ParkingLot lot, int perHourArrivalRate, int steps) {
		
		this.lot = lot;
		this.steps = steps;
		this.clock = 0;
		this.probabilityOfArrivalPerSec = new Rational(perHourArrivalRate, NUM_SECONDS_IN_1H);
		
		this.incomingQueue = new LinkedQueue<Spot>();
		this.outgoingQueue = new LinkedQueue<Spot>();
		
	}


	/**
	 * Simulate the parking lot for the number of steps specified by the steps
	 * instance variable
	 * NOTE: Make sure your implementation of simulate() uses peek() from the Queue interface.
	 */
	public void simulate() {
	
		//throw new UnsupportedOperationException("This method has not been implemented yet!");

		if(clock != 0){
			throw new IllegalStateException("The clock is invalid");
		}

		this.clock=0;

		Spot incomingToProcess = null;

		while (clock < steps) {

			if (RandomGenerator.eventOccurred(probabilityOfArrivalPerSec)){
				Car newCar = new Car(RandomGenerator.generateRandomString(PLATE_NUM_LENGTH));
				incomingQueue.enqueue(new Spot(newCar, clock));
			}
			
			 if (!incomingQueue.isEmpty()) {
				incomingToProcess = incomingQueue.peek();
				if (lot.attemptParking(incomingToProcess.getCar(), this.clock)){
					Spot parkedCar = incomingQueue.dequeue();
					incomingToProcess = null;
					lot.park(parkedCar.getCar(),this.clock);
				}
			}

			//processDeparture();

			for (int i = 0; i < lot.getOccupancy(); i++){ 
				Spot spot = lot.getSpotAt(i);

				if (spot != null) {
					int duration = clock - spot.getTimestamp();

					boolean willLeave = false;
					willLeave = RandomGenerator.eventOccurred(departurePDF.pdf(duration));

					if (duration == MAX_PARKING_DURATION || willLeave) {
						outgoingQueue.enqueue(lot.remove(i));
					}
				}
			}

			if (!outgoingQueue.isEmpty()) {
				//Spot leaving = 
				outgoingQueue.dequeue();
			}

			clock++;
		}

	
	}

	public int getIncomingQueueSize() {
		
		return incomingQueue.size();
			
	}
}