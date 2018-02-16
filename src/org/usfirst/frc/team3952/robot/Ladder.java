package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// Encoders using no units
/**
 * Notes:
 * 	The climber cord will be handled by mech
 * 	The motor has so much torque, it will go at the same speed regardless of weight change
 * 
 */
public class Ladder {
	
	private Talon ladder, coiler, claw;
	private Encoder encoder;
	private DigitalInput topLimit, frameBottomLimit, armBottomLimit;
	// Encoder values: 6ft = 4800
	
	
	public Ladder(Talon ladder, Talon coiler, Talon claw, Encoder encoder, DigitalInput topLimit,
			DigitalInput frameBottomLimit, DigitalInput armBottomLimit) {
		super();
		this.ladder = ladder;
		this.coiler = coiler;
		this.claw = claw;
		this.encoder = encoder;
		this.topLimit = topLimit;
		this.frameBottomLimit = frameBottomLimit;
		this.armBottomLimit = armBottomLimit;
	}
	

	public void extendLadder() {
		if(!topLimit.get()){
			ladder.set(0.65);
		}
	}

	public void retractLadder() {
		if(!(frameBottomLimit.get() && armBottomLimit.get())){
			ladder.set(-0.4);
		}
	}
	
	public void pulseIfNotMoving(){
		int rand = (int) Math.random()*2; // [0,1) -> [0, 2) -> 0 or 1
		//there is a 50% chance the ladder will be at 0.05 power, 50% chance it will be at 0.
		ladder.set(0.08*rand); //so about half the time it should pulse on
	}
	
	public void openClaw() {
		claw.set(-0.1);		// Set
	}
	
	public void closeClaw() {
		claw.set(0);
	}
	
	public Encoder getEncoder() {
		return encoder;
	}
	
	static boolean close(double a, double b) {
		return Math.abs(a - b) < 1;
	}
	
}