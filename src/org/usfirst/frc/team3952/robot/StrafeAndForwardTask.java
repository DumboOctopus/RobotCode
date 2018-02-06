package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class StrafeAndForwardTask extends Task {

	private static double MAX_SPEED = 0.3;
	
	private MecanumDrive drive;
	private Encoder rightEncoder, leftEncoder;
	
	private boolean started = false;
	private double initialEncoderReading;
	
	private double forwardDist, horizontalDist, endDist;
	private double[] speedVector;
	
	public StrafeAndForwardTask(Robot robot, double forwardDist, double horizontalDist){
		this.drive = robot.getDrive();
		rightEncoder = robot.getRightEncoder();
		leftEncoder = robot.getLeftEncoder();
		this.forwardDist = forwardDist;
		this.horizontalDist = horizontalDist;
		/**
		 * 1.58 ft/rotation if only going forwards. 1.31 ft/rotation when strafing
		 * 
		 *  rotationsNeededToGoForward = forwardDist/1.58
		 *  rotationsNeededToGoSideways = horizontalDist/1.31
		 *  totalRot = forwardDist/1.58 + horizontalDist/1.31
		 *  
		 * Every 1 rotation, the encoders should read 1.58 ft 
		 * 
		 * 	distanceEncodersShouldRead = 1.58 * totalRot
		 * 
		 * which gives us this equation
		 */
		this.endDist = 1.58 * (forwardDist /1.58 + horizontalDist /1.31);
		
		
		double magnitude = Math.sqrt(forwardDist * forwardDist + horizontalDist * horizontalDist);
		speedVector = new double[]{MAX_SPEED*horizontalDist/magnitude, MAX_SPEED*forwardDist/magnitude};
	}
	
	
	@Override
	public boolean run() {
		//starting
		if(!started){
			initialEncoderReading = readEncoders();
			started = true;
		}
		
		
		if(readEncoders() - initialEncoderReading >= endDist - 0.1){
			drive.driveCartesian(0, 0, 0);
			return true;
		}
		
		//driveCartesian(horizontal, vertical, rot)
		drive.driveCartesian(speedVector[0], speedVector[1], 0);
		
		return false;
	}
	
	
	private double readEncoders(){
		return rightEncoder.getDistance();
	}

	@Override
	public void cancel() {
		drive.driveCartesian(0,0,0);

	}

	@Override
	public String toString() {
		return String.format("Strafe and Forward Task <%.2f, %.2f>", horizontalDist, forwardDist) ;
	}

}
