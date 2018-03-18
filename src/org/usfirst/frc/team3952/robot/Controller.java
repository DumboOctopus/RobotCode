package org.usfirst.frc.team3952.robot;

/**
 * Represents all controllers.
 * These are the methods all controller classes must have in order to fit into our code.
 * If you decide controllers should all have another method, add it in here, not just in the instances.
 * 
 *
 */
public interface Controller {
	
	public double getHorizontalMovement();
	public double getLateralMovement();
	public double getRotation();
	public boolean extendLadder();
	public boolean retractLadder();
	public boolean coil();
	
	//we used to have safe versions too but mech kek'd
	public boolean unsafeOpenClaw();
	public boolean unsafeCloseClaw();
	
	//just in case we need this lader.
	public boolean triggerClaw();

}
