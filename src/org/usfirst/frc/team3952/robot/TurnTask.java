package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * Status: Tested and working good enough but is a bit not precise
 */
public class TurnTask extends Task {
	
	private ADXRS450_Gyro gyro;
	private MecanumDrive drive;
	private double startingAngle;
	private double degrees;
	private boolean flag;		// set starting angle on first frame

	private long lastMillis;	// use System.millis() to do local linearization	
	
	/**
	 * Constructs a Turn Task task to turn degrees degrees
	 * @param robot the robot
	 * @param degrees negative = left, positive = right
	 */
	public TurnTask(Robot robot, double degrees){
		drive = robot.getDrive();
		gyro = robot.getGyro();
		this.degrees = degrees;
		flag = true;
	}
	
	public boolean run(){
		// Initialization
		if(flag) {
			flag = false;
			startingAngle = gyro.getAngle() % 360;
			lastMillis = System.currentTimeMillis();
		}
		
		long nowMillis = System.currentTimeMillis();
		// check if we are done
		if(differenceAngle(gyro.getAngle() + gyro.getRate() * (nowMillis - lastMillis) / 1000.0, startingAngle + degrees) < 1.0){
			drive.driveCartesian(0, 0, 0);
			return true;
		}
		
		// move
		if(degrees < 0){
			drive.driveCartesian(0, 0, -0.3);
		} else if (degrees > 0){
			drive.driveCartesian(0, 0, 0.3);
		} 
		
		// make sure to change last millis
		lastMillis = nowMillis;
		return false;
	}
	
	public void cancel(){
		drive.driveCartesian(0, 0, 0);
	}
	
	public String toString(){
		return "TurnTask";
	}
	
	private static double differenceAngle(double a1, double a2){
		return Math.abs(a1 % 360 - a2 % 360);
	}
}
