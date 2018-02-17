package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;

// Encoders using no units
/**
 * Notes:
 * 	The climber cord will be handled by mech
 * 	The motor has so much torque, it will go at the same speed regardless of weight change
 */
public class Ladder {
	private static final int SWITCH_ENC = 1500, SCALE_ENC = 4800;
	
	private Talon ladder, coiler, claw;
	private Encoder encoder;
	private DigitalInput topLimit, frameBottomLimit, armBottomLimit, clawSwitch;
	
	private int pos;
	
	public Ladder(Talon ladder, Talon coiler, Talon claw, Encoder encoder, DigitalInput topLimit,
		DigitalInput frameBottomLimit, DigitalInput armBottomLimit, DigitalInput clawSwitch) {
		this.ladder = ladder;
		this.coiler = coiler;
		this.claw = claw;
		this.encoder = encoder;
		this.topLimit = topLimit;
		this.frameBottomLimit = frameBottomLimit;
		this.armBottomLimit = armBottomLimit;
		this.clawSwitch = clawSwitch;
	}

	public void extendLadder() {	
		if(!topLimit.get()){
			ladder.set(0.65);
		} else {
			ladder.set(0);
//			pulseIfNotMoving();
		}
		
	}

	public void retractLadder() {
		// TODO: technically only armBottomLimit.get() needs to be checked
		// claim(under normal circumstances):
		// if(armBottomLimit.get())
		//     assert frameBottomLimit.get();
		// proof: 
		// assume that armBottomLimit.get() but !frameBottomLimit.get()
		// the frame is not at the bottom
		// the arm cannot be lower than the frame(i.e. under normal circumstances)
		// !armBottomLimit.get()
		// since armBottomLimit.get() and !armBottomLimit.get() cannot both be true
		// thus by proof of contradiction, the assumption of (that armBottomLimit() but !frameBottomLimit.get()) == false
		// thus when armBottomLimit.get(), !frameBottomLimit.get()
		if(!(frameBottomLimit.get() && armBottomLimit.get())){
			ladder.set(-0.4);
		} else {
			ladder.set(0);
		}
		
	}
	
	public boolean setPos(int newPos){
		if(newPos == 0){ // if its all the way down
			// TODO: same as above
			if(frameBottomLimit.get() && armBottomLimit.get()){ // now we at the bottom
				pos = 0;
				pulseIfNotMoving();
				encoder.reset();
				return true;
			} else {  // if we aren't at the bottom
				retractLadder();
				return false;
			}
		}
		
		
		if(newPos == 1){ // if it's switch
			int diff = SWITCH_ENC - (int)encoder.getDistance(); // < 0 = downward
			if(Math.abs(diff) < 100){
				pos = 1;
				pulseIfNotMoving();
				return true;
			} else if(diff > 0){
				extendLadder();
				return false;
			} else {
				retractLadder();
				return false;
			}
			
		}
		
		
		if(newPos == 2){ // if it's scale
			int diff = SCALE_ENC - (int)encoder.getDistance(); // < 0 = downward
			if(Math.abs(diff) < 100){
				pos = 2;
				pulseIfNotMoving();
				return true;
			} else if(diff > 0){
				extendLadder();
				return false;
			} else {
				retractLadder();
				return false;
			}
			
		}
		
		if(newPos == 3){ // go up
			if(topLimit.get()){ // now we at the top
				pos = 3;
				pulseIfNotMoving();
				return true;
			} else{  // if we aren't at the top
				extendLadder();
				return false;
			}
		}
		
		return true;
	}
	 
	// TODO: testing required, has to go counter-clockwise
	public void coil() {
		coiler.set(0.2);
	}
	
	// TODO: testing required
	public void openClaw() {
		if(!clawSwitch.get())
			claw.set(-0.1);
		else
			claw.set(0);
	}
	
	// TODO: testing required
	public void closeClaw() {
		claw.set(0);
	}
	
	public void safety(){
		if(clawSwitch.get()){
			claw.set(0);
		}
		
		// TODO: same as above
		if(armBottomLimit.get() && frameBottomLimit.get() && ladder.get() < 0){
			ladder.set(0);
		}
		
		if(topLimit.get() && ladder.get() > 0){
			ladder.set(0);
		}
		
		coiler.set(0);
	}
	
	public void pulseIfNotMoving(){
		int rand = (int)(2 * Math.random()); // [0, 1) -> [0, 2) -> 0 or 1
		// there is a 50% chance the ladder will be at 0.05 power, 50% chance it will be at 0.
		ladder.set(0.08 * rand); // so about half the time it should pulse on
	}
	
	public int getPos(){
		return pos;
	}
}