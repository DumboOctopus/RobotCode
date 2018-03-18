package org.usfirst.frc.team3952.robot;

/**
 * The purpose of this task is the move the coiler
 * so that it breaks the string that binds the claw up. 
 *
 */
public class DropClawTask extends Task {
	
	private Climber climber;
	private long startTime = -1;
	
	public DropClawTask(Robot r){
		climber = r.getClimber();
	}
	
	@Override
	public boolean run() {
		if(startTime == -1) {//means it hasn't initied yet
			startTime = System.currentTimeMillis();
		}
		
		//1 second until string breaks.
		if(System.currentTimeMillis() - startTime <= 1000){
			climber.breakString();
			return false;
		} else{
			climber.stop();
			return true;
		}
	}

	@Override
	public void cancel() {
		climber.stop();
	}

	@Override
	public String toString() {
		return "Drop Claw Task";
	}

}
