package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.*;


/**
 * Status: Untested
 * 
 *
 */
public class MoveForwardTask extends Task {
	private MecanumDrive drive;
	private Encoder leftEncoder, rightEncoder;
	private double initialDistance;
	private double distance;
	
	private boolean started = false;
	//private GraduallyGoTo ggt;
	
	public MoveForwardTask(Robot robot, double distance) {
		drive = robot.getDrive();
		leftEncoder = robot.getLeftEncoder();
		rightEncoder = robot.getRightEncoder();
		this.distance = distance;
		
		//ggt = new GraduallyGoTo(0, 0.009); 
		/**
		 * Observations:
		 * 	low speed, low acceleration: not much drift
		 * 	low speed, high acceleartion: drift
		 * 	
		 * 
		 * 	ggt = new GraduallyGoTO(0.33, 0.005); sig
		 * 							0.3,  0.005); low
		 * 							0.32, 0.005) sig ~10 dig
		 * 							0.27, 0.005) sig 
		 * 							0.25, 0.005) okay
		 */
		//ggt = new GraduallyGoTO(0.32, 0.005); // ok
	}
	
	// TODO: testing required
	@Override
	public boolean run() {
		if(!started){
			// just because the left encoder is so much more accurate than right.
			initialDistance = leftEncoder.getDistance();
			started = true;
		}
		
		double currentDistance = leftEncoder.getDistance();
		if(currentDistance >= initialDistance + distance - 0.1) {
			drive.driveCartesian(0, 0, 0);
			return true;
		} 
	
		drive.driveCartesian(0, 0.4, 0);		// we will need to recalibrate this later.
		return false;
	
	}
	
	@Override
	public void cancel() {
		drive.driveCartesian(0, 0, 0);
	}
	
	@Override
	public String toString() {
		double currentDistance = leftEncoder.getDistance();
		return "Move Forward: " + (int)distance + " feet(" + (int)(currentDistance - initialDistance) + " feet left)";
	}
}