package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// Encoders using no units
/**
 * Notes:
 * 	The climber cord will be handled by mech
 * 	The motor has so much torque, it will go at the same speed regardless of weight change
 */
public class Ladder {
	private static final int SWITCH_ENC = 1500, SCALE_ENC = 4800;
	public static final double CLOCKWISE = 1;
	public double ladderSpeedUp = 0.65, ladderSpeedDown = 0.4;
	
	private Talon ladder, coiler, claw;
	private Encoder encoder;
	private DigitalInput topLimit, armBottomLimit, clawOpeningLimit, clawClosingLimit;
	
	private int pos;
	
	public Ladder(Talon ladder, Talon coiler, Talon claw, Encoder encoder, DigitalInput topLimit,
		DigitalInput armBottomLimit, DigitalInput clawOpeningLimit, DigitalInput clawClosingLimit) {
		this.ladder = ladder;
		this.coiler = coiler;
		this.claw = claw;
		this.encoder = encoder;
		this.topLimit = topLimit;
		this.armBottomLimit = armBottomLimit;
		this.clawOpeningLimit = clawOpeningLimit;
		this.clawClosingLimit = clawClosingLimit;
	}

	public void extendLadder() {	
		if(!topLimit.get()){
			ladder.set(ladderSpeedUp);
		} else {
			ladder.set(0);
		}
	}

	public void retractLadder() {
		if(!armBottomLimit.get()){
			ladder.set(-ladderSpeedDown);
		} else {
			ladder.set(0);
		}
		
	}
	
	public void toggleLadder(){
		if(ladderSpeedUp > 0.6){
			ladderSpeedUp = 0.5;
		} else{
			ladderSpeedUp = 0.65;
		}
		
		if(ladderSpeedDown > 0.35 ){
			ladderSpeedDown = 0.33;
		} else{
			ladderSpeedDown = 0.4;
		}
	}
	
	public boolean setPos(int newPos){
		if(newPos == 0){ // if its all the way down
			if(armBottomLimit.get()) { // now we at the bottom
				pos = 0;
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
				ladder.set(0);
//				pulseIfNotMoving();
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
				ladder.set(0);
//				pulseIfNotMoving();
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
				ladder.set(0);
//				pulseIfNotMoving();
				return true;
			} else{  // if we aren't at the top
				extendLadder();
				return false;
			}
		}
		
		return true;
	}
	 
	// TODO: testing required, has to go clockwise
	public void coil() {
		coiler.set(-0.4);
	}
	
	// TODO: testing required
	public void openClaw() {
		//System.out.println("We made it to clawOpen");
		if(clawOpeningLimit.get()) {
			claw.set(-CLOCKWISE);
			//System.out.println("claw.set(-1)");
		}else {
			//System.out.println("clow close 0");
			claw.set(0);
		}
	}
	
	// TODO: testing required
	public void closeClaw() {
		//System.out.println("We made it to closeCLaw");
		if(clawClosingLimit.get() && System.currentTimeMillis() - Robot.startMillis <= 380) {
			claw.set(CLOCKWISE);
			//System.out.println("claw.set(1)");
		}else {
			//System.out.println("clow close 0");
			claw.set(0);
		}
	}
	
	public void closeClawUnsafe() {
		claw.set( CLOCKWISE);
	}
	public void openClawUnsafe() {
		claw.set(-CLOCKWISE );
	}
	
	public void safety(){
		// TODO: test which way is open
		if((!clawOpeningLimit.get() && claw.get() < 0) || (!clawClosingLimit.get() && claw.get() > 0)){
			claw.set(0);
		}
		
		// TODO: same as above
		if(armBottomLimit.get() && ladder.get() < 0){
			ladder.set(0);
		}
		
		if(topLimit.get() && ladder.get() > 0){
			ladder.set(0);
		}
		
		//coiler.set(0);
	}
	
	public int getPos(){
		return pos;
	}
}