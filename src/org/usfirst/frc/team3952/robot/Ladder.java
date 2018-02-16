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
	private DigitalInput topLimit, frameBottomLimit, armBottomLimit, clawSwitch;
	
	private int pos; 
	private static final int SWITCH_ENC = 1500, SCALE_ENC = 4800;
	// Encoder values: 6ft = 4800
	
	
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
			pulseIfNotMoving();
		}
		
	}

	public void retractLadder() {
		
		if(!(frameBottomLimit.get() && armBottomLimit.get())){
			ladder.set(-0.4);
		}
		
	}
	
	public void pulseIfNotMoving(){
		int rand = (int)(2 * Math.random()); // [0, 1) -> [0, 2) -> 0 or 1
		//there is a 50% chance the ladder will be at 0.05 power, 50% chance it will be at 0.
		ladder.set(0.08*rand); //so about half the time it should pulse on
	}
	
	
	public int getPos(){return pos;}
	
	public boolean setPos(int newPos){
		if(newPos == 0){ // if its all the way down
			if(frameBottomLimit.get() && armBottomLimit.get()){ // now we at the bottom
				pos = 0;
				pulseIfNotMoving();
				encoder.reset();
				return true;
			} else {  //if we aren't at the bottom
				retractLadder();
				return false;
			}
		}
		
		
		if(newPos == 1){ //if its switch
			int diff = SWITCH_ENC - (int)encoder.getDistance(); // < 0 = downward
			if(Math.abs(diff) < 100){
				//we done bois
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
		
		
		if(newPos == 2){ //if its switch
			int diff = SCALE_ENC - (int)encoder.getDistance(); // < 0 = downward
			if(Math.abs(diff) < 100){
				//we done bois
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
		
		if(newPos == 3){ // we wanna go UP
			if(topLimit.get()){ // now we at the top
				pos = 3;
				pulseIfNotMoving();
				return true;
			} else{  //if we aren't at the top yet bro
				extendLadder();
				return false;
			}
		}
		
		// WHY NO SWITCH CASE
		
		return true;
	}
	
	
	public void openClaw() {
		if(!clawSwitch.get())
			claw.set(-0.1);		// Set
		else
			claw.set(0);
	}
	
	public void closeClaw() {
		claw.set(0);
	}
	
	
	public void safety(){
		if(clawSwitch.get()){
			claw.set(0);
		}
		
		if(armBottomLimit.get() && frameBottomLimit.get() && ladder.get() < 0){
			ladder.set(0);
		}
		
		if(topLimit.get() && ladder.get() > 0){
			ladder.set(0);
		}
		
	}
	
	public Encoder getEncoder() {
		return encoder;
	}
	
	static boolean close(double a, double b) {
		return Math.abs(a - b) < 1;
	}
	
}