package cmsc433;

import java.util.concurrent.Semaphore;

/**
 * Machines are used to make different kinds of Food. Each Machine type makes
 * just one kind of Food. Each machine type has a count: the set of machines of
 * that type can make that many food items in parallel. If the machines are
 * asked to produce a food item beyond its count, the requester blocks. Each
 * food item takes at least item.cookTime10S seconds to produce. In this
 * simulation, use Thread.sleep(item.cookTime10S) to simulate the actual cooking
 * time.
 */
public class Machines {

	public enum MachineType {
		sodaMachines, fryers, grillPresses, ovens
	};

	// Converts Machines instances into strings based on MachineType.
	public String toString() {
		switch (machineType) {
			case sodaMachines:
				return "Soda Machines";
			case fryers:
				return "Fryers";
			case grillPresses:
				return "Grill Presses";
			case ovens:
				return "Ovens";
			default:
				return "INVALID MACHINE TYPE";
		}
	}

	public final MachineType machineType;
	public final Food machineFoodType;
	public Semaphore machineSema;

	// YOUR CODE GOES HERE...



	/**
	 * The constructor takes at least the name of the machines, the Food item they
	 * make, and their count. You may extend it with other arguments, if you wish.
	 * Notice that the constructor currently does nothing with the count; you must
	 * add code to make use of this field (and do whatever initialization etc. you
	 * need).
	 */
	public Machines(MachineType machineType, Food foodIn, int countIn) {
		this.machineType = machineType;
		this.machineFoodType = foodIn;
		machineSema = new Semaphore(countIn);
		// ovenSema = new Semaphore(countIn);
		// grillSema = new Semaphore(countIn);
		// sodaSema = new Semaphore(countIn);

		// YOUR CODE GOES HERE...

	}

	/**
	 * This method is called by a Cook in order to make the Machines' food item. You
	 * can extend this method however you like, e.g., you can have it take extra
	 * parameters or return something other than Object. You will need to implement
	 * some means to notify the calling Cook when the food item is finished.
	 */
	public void makeFood(Cook c, Food f, int orderNumber) throws InterruptedException {
		// YOUR CODE GOES HERE...
		//Simulation.logEvent(SimulationEvent.cookStartedFood(c,this.machineFoodType, on));
		Thread foodThread = new Thread(new CookAnItem(this.machineFoodType.cookTime10S, this, orderNumber, c));
		foodThread.start();

		// if (last) {
		// 	foodThread.join();
		// }
		// synchronized (machineSema) {
		// 	machineSema.wait();
		// }

		// Simulation.logEvent(SimulationEvent.cookFinishedFood(c, machineFoodType, orderNumber));
		

	}

	// THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {

		public final int time, orderNumber;
		public Machines curr;
		public Cook cook;
		

		public CookAnItem(int time, Machines c, int o, Cook cc) {
			this.time = time;
			this.curr  = c;
			this.orderNumber = o;
			this.cook = cc;
		}
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				//System.out.println("permits: " + machineSema.availablePermits());
				machineSema.acquireUninterruptibly();
				Simulation.logEvent(SimulationEvent.machinesCookingFood(curr, machineFoodType));
				//System.out.println("permits: " + machineSema.availablePermits());
				 Thread.sleep(time);
				machineSema.release();
				
				Simulation.logEvent(SimulationEvent.machinesDoneFood(curr, machineFoodType));
				Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, machineFoodType, orderNumber));

				synchronized (cook.name) {
					
					cook.name.notify();
				}
				// synchronized(machineSema) {
				// 	machineSema.notify();
				// }

			} catch(InterruptedException e) { }
		}
	}
}
