public class CapacityOptimizer {
	private static final int NUM_RUNS = 10;

	private static final double THRESHOLD = 5.0d;

	private static int final_size;

	public static int getOptimalNumberOfSpots(int hourlyRate) {
		

		if (hourlyRate <= 0){
			throw new IllegalArgumentException("The given hourly rate is invalid");
		}
		
		boolean flag = true;
		int n = 1;

		
		while(flag){
			final_size = 0;

			System.out.println("==== Setting lot capacity to: " + n + " ====");

			for(int i = 0; i < NUM_RUNS; i++){
				ParkingLot lot = new ParkingLot(n);

				Simulator simulation = new Simulator(lot, hourlyRate, Simulator.SIMULATION_DURATION);
				
				long startClock = System.currentTimeMillis();

				simulation.simulate();

				long endClock = System.currentTimeMillis();

				long timeSpent = endClock - startClock;
				System.out.println("Simulation run "+ (i + 1) + " ("+ timeSpent + "ms) ; Queue length at the end of simulation run: "+ simulation.getIncomingQueueSize());
				final_size += simulation.getIncomingQueueSize();

			}
			
			double sum = final_size / NUM_RUNS; 
			if( sum <= THRESHOLD){
				return n;
			}
			else{
				n = n + 1;
				System.out.println(" ");
			}
		}

		return -1;	
	
	}

	public static void main(String args[]) {
	
		StudentInfo.display();

		long mainStart = System.currentTimeMillis();

		if (args.length < 1) {
			System.out.println("Usage: java CapacityOptimizer <hourly rate of arrival>");
			System.out.println("Example: java CapacityOptimizer 11");
			return;
		}

		if (!args[0].matches("\\d+")) {
			System.out.println("The hourly rate of arrival should be a positive integer!");
			return;
		}

		int hourlyRate = Integer.parseInt(args[0]);

		int lotSize = getOptimalNumberOfSpots(hourlyRate);

		System.out.println();
		System.out.println("SIMULATION IS COMPLETE!");
		System.out.println("The smallest number of parking spots required: " + lotSize);

		long mainEnd = System.currentTimeMillis();

		System.out.println("Total execution time: " + ((mainEnd - mainStart) / 1000f) + " seconds");

	}
}