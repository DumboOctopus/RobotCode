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
	public static final int EXTEND_LADDER = 4;
	public static final int RETRACT_LADDER = 3;
	public static final int CANCEL_TASK = 1;					// set to a reasonable value
	public static final int CANCEL_BACKGROUND_TASK = 1;		// set to a reasonable value
	
	private Joystick joystick;
	
	private double horizontalMovement;
	private double lateralMovement;
	private double rotation;
	
	
	
	//TODO: lateral means side to side lol
	// getLateral or Horizontal Movement = kln(|y| + 1 - deadzone) + C
	// C = determined
	// k = (MAX-C)/ln(1 + 1 - deadzone)		
	private double Cy = 0.1;
	private double deadzoney = 0.2;
	private double MAXy = 0.8;
	private double ky = (MAXy-Cy)/Math.log(2 - deadzoney);
			
	
	public Controller() {
		joystick = new Joystick(0);
		
		horizontalMovement = 0;
		lateralMovement = 0;
		rotation = 0;
	}
	
	public double getHorizontalMovement() {
//		if(SMOOTH) return (horizontalMovement = constraint(lerp(horizontalMovement, joystick.getX())));
//		else return (horizontalMovement = Math.abs(joystick.getX()) > 0.3? joystick.getX(): 0.0 );
		double x = joystick.getX();
		//maybe add +/- C
		return Math.abs(x) >= 0.2 ? 
				Math.signum(x) * ( Math.log(Math.abs(x) + 1 - 0.2) + 0.1 ): 
				0;
//		else { 
//			
//			double m = Math.abs(joystick.getY());
//			return (joystick.getY() > 0? -0.5: 0.5) * (0.35/Math.pow(0.5,3)*Math.pow(+m-0.5, 3)+ 0.3*m+0.35);
//		}
	}
	
	// joystick.getY() appears to be inverted, thus a negative sign is applied to the raw value
	public double getLateralMovement() {
//		if(SMOOTH) return (lateralMovement = constraint(lerp(lateralMovement, -joystick.getY())));
//		else { return (lateralMovement = Math.abs(joystick.getY()) > 0.3? -joystick.getY(): 0.0);
		
		
		return Math.abs(joystick.getY()) >= deadzoney ? 
				ky * Math.signum(-joystick.getY()) * (Math.log(Math.abs(joystick.getY()) + 1 - deadzoney) + Cy) : 
				0;
	}
	
	// positive = clockwise
	public double getRotation() {
		//if(SMOOTH) return (rotation = constraint(lerp(rotation, 0.5 * joystick.getZ())));
		//else return (rotation = 0.5 * joystick.getZ());
		return 0.3 * joystick.getZ(); //nerf the rotating
//		if(joystick.getRawButton(5)){
//			return 0.25;
//		} else if(joystick.getRawButton(4)){
//			return -0.25;
//		}
//		return 0.0;
		
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