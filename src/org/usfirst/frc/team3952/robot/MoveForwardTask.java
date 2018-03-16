package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.*;


/**
 * Status: Untested
 * 
 *
 */
public class MoveForwardTask extends Task {
	private static final double CORRECTION_CONSTANT = 0.05;
	
	private MecanumDrive drive;
	private Encoder leftEncoder, rightEncoder;
	private double initialDistance;
	private double distance;
	private boolean nudge;
	
	private boolean started = false;
	
	private ADXRS450_Gyro gyro;
	private double initialGyro;
	//private GraduallyGoTo ggt;
	
	public MoveForwardTask(Robot robot, double distance, boolean nudge) {
		drive = robot.getDrive();
		leftEncoder = robot.getLeftEncoder();
		rightEncoder = robot.getRightEncoder();
		this.distance = distance;
		this.nudge = nudge;
		this.gyro = robot.getGyro();
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
	
	// TODO: neil says to use both encoders so it goes straight
	//		 but it this wouldnt really do anything since any moving that happens in this method is
	//		 driveCartesian(0, 0.4, 0), which is supposed to be straight
	//		 so the fix should be if one encoder reads value smaller than the other the robot should rotate while moving
	@Override
	public boolean run() {
		if(!started){
			// just because the left encoder is so much more accurate than right.
			// not anymore. using both now.
			initialDistance = (leftEncoder.getDistance() + rightEncoder.getDistance()) / 2;
			initialGyro = gyro.getAngle();
			started = true;
		}
		
		double currentDistance = (leftEncoder.getDistance() + rightEncoder.getDistance()) / 2;
		if(currentDistance >= initialDistance + distance - 0.1) {
			drive.driveCartesian(0, 0, 0);
			return true;
		} 
	
		//double diff = leftEncoder.getRate() - rightEncoder.getRate();
		double diff = initialGyro - gyro.getAngle();
		if(Math.abs(diff) < 1) diff = 0;
		drive.driveCartesian(0, nudge? 0.3: 0.4, diff * CORRECTION_CONSTANT);		// we will need to recalibrate this later.
		
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