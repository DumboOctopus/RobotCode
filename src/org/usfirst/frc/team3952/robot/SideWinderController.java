package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// joystick.getRawButton(2): while true arm collapse
// joystick.getRawButton(3): move up ladder
// joystick.getRawButton(4): move down ladder

public class SideWinderController implements Controller{
	
	
	public static final int EXTEND_LADDER = 3;
	public static final int RETRACT_LADDER = 4;
	public static final int TRIGGER_CLAW = 1;
	public static final int COIL = 7, COIL2 = 5;
	public static final int TOGGLE_SPEED = 8;
	public static final int USE_UNSAFE_CLAW_OPEN = 5;
	
	public Joystick joystick;
	
	// horizontal / lateral movement = k * ln(|x| + 1 - dead zone) + C
	// C = minimum velocity
	// k = (max velocity - C) / ln(1 + 1 - dead zone)		
	public double c = 0.1;
	public double deadZone = 0.2;
	public double max = 0.8;
	public double k = (max - c) / Math.log(2 - deadZone);
	
	public double cx = 0.2;
	public double deadZonex = 0.2;
	public double maxx = 0.8;
	public double kx = (maxx - cx) / Math.log(2 - deadZonex);
	
	private double ct = 0.08;
	private double deadZonet = 0.08;
	private double maxt = 0.4;
	private double kt = (maxt - ct) / Math.log(2 - deadZonet);
	
	
	public SideWinderController() {
		joystick = new Joystick(0);
	}
	
	//=== STICK THINGY ===\\
	public double getHorizontalMovement() {
		double x = joystick.getX();
		return Math.abs(x) >= deadZonex ? 
			   kx * Math.signum(x) * (Math.log(Math.abs(x) + 1 - deadZonex) + cx)	// TODO: k?
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
		double t =  joystick.getZ();
		return Math.abs(t) >= deadZonet ? 
			   kt * Math.signum(t) * (Math.log(Math.abs(t) + 1 - deadZonet) + ct)
			   :
			   0;
		
	}
	
	//=== Buttons ===\\
	
	public boolean extendLadder() {
		return joystick.getRawButton(EXTEND_LADDER);
	}
	
	public boolean retractLadder() {
		return joystick.getRawButton(RETRACT_LADDER);
	}
	
	public boolean coil() {
		return joystick.getRawButton(COIL) && joystick.getRawButton(COIL2);
	}
	
	public boolean triggerClaw() {
		return joystick.getRawButtonReleased(TRIGGER_CLAW);
	}
	
	public boolean unsafeOpenClaw(){
		return joystick.getRawButton(USE_UNSAFE_CLAW_OPEN);
	}
	
	@Override
	public boolean unsafeCloseClaw() {
		return joystick.getTrigger();
	}

	
	//slow mode unused...
	public boolean toggleSpeed(){
		return joystick.getRawButtonPressed(TOGGLE_SPEED);
	}
	
	//actually toggles the speeds
	public void toggleTheSpeed(){
		if(maxx > 0.79 && max > 0.79){
			maxx = 0.5;
			max = 0.5;
		} else {
			maxx = 0.8;
			max = 0.8;
		}
		
		k = (max - c) / Math.log(2 - deadZone);
		kx = (maxx - cx) / Math.log(2 - deadZonex);
		
	}

}