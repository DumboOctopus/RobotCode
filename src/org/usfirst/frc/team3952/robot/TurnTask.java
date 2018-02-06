package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * Status: Tested and working good enough but is a bit impercise.
 */
public class TurnTask extends Task {
	
	private ADXRS450_Gyro gyro;
	private MecanumDrive drive;
	private double startingAngle;
	private double degrees;
	private boolean flag;		// set starting angle on first frame

	//next thing: using System.millis() to do local linearization like a boss
	private long lastMillis;
	
	
	/**
	 * Constructs a Turn Task task to turn degrees degrees
	 * @param robot the robot
	 * @param degrees negative = left, + = right
	 */
	public TurnTask(Robot robot, double degrees){
		drive = robot.getDrive();
		gyro = robot.getGyro();
		this.degrees = degrees;
		flag = true;
	}
	
	public boolean run(){
		
		//initialization stuffs
		if(flag) {
			flag = false;
			startingAngle = gyro.getAngle() % 360;
			lastMillis = System.currentTimeMillis();
		}
		
		
		long nowMillis = System.currentTimeMillis();
		//check if we are done
		if(differenceAngle(gyro.getAngle() + gyro.getRate() * (nowMillis - lastMillis)/1000.0, startingAngle + degrees) < 1.0){
			drive.driveCartesian(0, 0, 0);
			lastMillis = nowMillis; //just in case it doesn't stop so nowMillis - lastMillis doesn't become so huge, condition is false
			return true; //should stop after this
			
		}
		
		//move
		if(degrees < 0){
			drive.driveCartesian(0, 0, -0.25);
		} else if (degrees > 0){
			drive.driveCartesian(0, 0, 0.25);
		} 
		
		//make sure to change last millis
		lastMillis = nowMillis;
		return false;
	}
	
	public void cancel(){
		drive.driveCartesian(0, 0, 0);
	}
	
	public String toString(){
		return "TurnTask";
	}
	
	
	private double differenceAngle(double a1, double a2){
		return Math.abs(a1 % 360 - a2 % 360);
	}
}
