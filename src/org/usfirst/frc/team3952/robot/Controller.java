package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;

// joystick.getRawButton(2): while true arm collapse
// joystick.getRawButton(3): move up ladder
// joystick.getRawButton(4): move down ladder

public class Controller {
	public static final boolean SMOOTH = false;
	public static final double LERP = 0.25;
	public static final double DELTA = 0.02;
	
	public static final int COLLAPSE_ARM = 2;
	public static final int EXTEND_LADDER = 3;
	public static final int RETRACT_LADDER = 4;
	public static final int CANCEL_TASK = 1;					// set to a reasonable value
	public static final int CANCEL_BACKGROUND_TASK = 1;		// set to a reasonable value
	
	private Joystick joystick;
	
	private double horizontalMovement;
	private double lateralMovement;
	private double rotation;
	
	public Controller() {
		joystick = new Joystick(0);
		
		horizontalMovement = 0;
		lateralMovement = 0;
		rotation = 0;
	}
	
	public double getHorizontalMovement() {
		if(SMOOTH) return (horizontalMovement = constraint(lerp(horizontalMovement, joystick.getX())));
		//else return (horizontalMovement = joystick.getX());
		else { 
			
//			double m = Math.abs(joystick.getX());
//			return (joystick.getX() > 0? 0.5: -0.5) * (0.35/Math.pow(0.5,3)*Math.pow(+m-0.5, 3)+ 0.3*m+0.35);
		
			return joystick.getX();
		}
	}
	
	// joystick.getY() appears to be inverted, thus a negative sign is applied to the raw value
	public double getLateralMovement() {
		if(SMOOTH) return (lateralMovement = constraint(lerp(lateralMovement, -joystick.getY())));
		else { //return (lateralMovement = -joystick.getY());
		
//			double m = Math.abs(joystick.getY());
//			return (joystick.getY() > 0? -0.5: 0.5) * (0.35/Math.pow(0.5,3)*Math.pow(+m-0.5, 3)+ 0.3*m+0.35);
		
			return -joystick.getY();
		}
	}
	
	// positive = clockwise
	public double getRotation() {
		//if(SMOOTH) return (rotation = constraint(lerp(rotation, 0.5 * joystick.getZ())));
		//else return (rotation = 0.5 * joystick.getZ());
		if(joystick.getRawButton(5)){
			return 0.25;
		} else if(joystick.getRawButton(4)){
			return -0.25;
		}
		return 0.0;
		
	}
	
	public boolean extendLadder() {
		return joystick.getRawButton(EXTEND_LADDER);
	}
	
	public boolean retractLadder() {
		return joystick.getRawButton(RETRACT_LADDER);
	}
	
	public boolean cancelTask() {
		return joystick.getRawButton(CANCEL_TASK);
	}
	
	public boolean cancelBackgroundTask() {
		return joystick.getRawButton(CANCEL_BACKGROUND_TASK);
	}
	
	private static double lerp(double value, double destination) {
		return value + LERP * (destination - value);
	}
	
	private static double constraint(double value) {
		value = Math.max(-1.0, Math.min(1.0, value));	// constraints the value to [-1.0, 1.0]
		if(Math.abs(value - 0) < DELTA) value = 0;		// set value to 0 if close enough to 0
		return value;
	}
}