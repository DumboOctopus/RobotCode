package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// joystick.getRawButton(2): while true arm collapse
// joystick.getRawButton(3): move up ladder
// joystick.getRawButton(4): move down ladder

public class Controller {
	//=== Buttons ===\\
	// TODO: decide these
	public static final int CANCEL_TASK = 1;
	public static final int EXTEND_LADDER = 3;
	public static final int RETRACT_LADDER = 4;
	public static final int LADDER_UP = 6;
	public static final int LADDER_DOWN = 8;
	public static final int CLOSE_CLAW = 2;
	public static final int COIL = 7;
	
	private Joystick joystick;
	
	// horizontal / lateral movement = k * ln(|x| + 1 - dead zone) + C
	// C = minimum velocity
	// k = (max velocity - C) / ln(1 + 1 - dead zone)		
	private double c = 0.1;
	private double deadZone = 0.2;
	private double max = 0.8;
	private double k = (max - c) / Math.log(2 - deadZone);
			
	
	public Controller() {
		joystick = new Joystick(0);
	}
	
	//=== Joystick ===\\
	
	public double getHorizontalMovement() {
		double x = joystick.getX();
		return Math.abs(x) >= deadZone ? 
			   Math.signum(x) * (Math.log(Math.abs(x) + 1 - deadZone) + c)	// TODO: k?
			   :
			   0;
	}
	
	// joystick.getY() appears to be inverted, thus a negative sign is applied to the raw value
	public double getLateralMovement() {
		double y = -joystick.getY();
		return Math.abs(y) >= deadZone ? 
			   k * Math.signum(y) * (Math.log(Math.abs(y) + 1 - deadZone) + c)
			   :
			   0;
	}
	
	// positive = clockwise
	public double getRotation() {
		return 0.3 * joystick.getZ();
	}
	
	//=== Buttons ===\\
	
	public boolean extendLadder() {
		return joystick.getRawButton(EXTEND_LADDER);
	}
	
	public boolean retractLadder() {
		return joystick.getRawButton(RETRACT_LADDER);
	}
	
	public boolean ladderUp() {
		return joystick.getRawButton(LADDER_UP);
	}
	
	public boolean ladderDown() {
		return joystick.getRawButton(LADDER_DOWN);
	}
	
	public boolean coil() {
		return joystick.getRawButton(COIL);
	}
	
//	public boolean pressedExtendLadder(){
//		return joystick.getRawButtonPressed(EXTEND_LADDER);
//	}
//	
//	public boolean pressedRetractLadder(){
//		return joystick.getRawButtonPressed(RETRACT_LADDER);
//	}
	
	public boolean cancelTask() {
		return joystick.getRawButton(CANCEL_TASK);
	}
}