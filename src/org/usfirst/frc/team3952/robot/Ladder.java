package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// Encoders using no units
/**
 * TODO:
 * 		Ask zenal if coiler needs ratchet.
 * 		Make coiler go up when need be (aka when stage 2 moves up)
 * 		Calibrate encoders????? 
 * 			Even if we calibrate them, its not going to 100 % accurate bc the inches/rot 
 * 			changes as inches increses.
 * 			For now: NOT CALIBRATE
 *		ALSO, we have programmatic access to the limit switch.
 * 		How To figure out constants??
 * 			Step 1: figure out ideal encoder change rate.
 * 			Step 2: Simulate PIDController on our computers to figure out a general idea of constants
 * 			Step 3: Test and refine on actual. 
 * 				Step 3.1: Make sure it goes constant steady speed on constant mass phases
 * 				Step 3.2: Test it under transition phases (inc mass, dec mass).
 *		
 */
public class Ladder {
	public boolean movingLadder = false; //constantly set to false inside Telop Periodic
	
	private Talon ladder, coiler, claw;
	private Encoder encoder;
	private PIDController pid;
	private static final double P = 1, I = 1, D = 1;
	private static final double idealEncoderRate = 1;
	
	
	public Ladder(Talon ladder, Talon coiler, Talon claw, Encoder encoder) {
		this.ladder = ladder;
		this.coiler = coiler;
		this.claw = claw;
		
		this.pid = new PIDController(P, I, D, new EncoderRatePIDSource(), new MoveLadderPIDOutput());
		throw new ArithmeticException("Set P, I, D, idealEncoder Rate consts!!");
	}
	
	public void disablePID() {
		pid.disable();
	}
	
	public void extendLadder() { 
		if(!pid.isEnabled()) { 
			pid.enable();
			pid.setSetpoint(idealEncoderRate);
		} else if(!close(pid.getSetpoint(), idealEncoderRate)) {
			pid.setSetpoint(idealEncoderRate);
		}
		movingLadder = true;
		// use pid thing
	}
	
	public void retractLadder() {
		if(!pid.isEnabled()) {
			pid.enable();
			pid.setSetpoint(-idealEncoderRate);
		} else if(!close(pid.getSetpoint(), -idealEncoderRate)) {
			pid.setSetpoint(-idealEncoderRate);
		}
		movingLadder = true;
		// use pid thing
	}
	
	public void openClaw() {
		claw.set(-0.1);		// Set
	}
	
	public void closeClaw() {
		claw.set(0);
	}
	
	static boolean close(double a, double b) {
		return Math.abs(a - b) < 1;
	}
	
	public class EncoderRatePIDSource implements PIDSource {
		double lastEncoderReading = encoder.getDistance();
		long lastRecordedTime = System.nanoTime() / 1000;
		
		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kRate;
		}
		
		@Override
		public double pidGet() {
			double value = (encoder.getDistance() - lastEncoderReading) / ((double)(System.nanoTime() / 1000 - lastRecordedTime) / 1000);	// inches/millisecond
			lastEncoderReading = encoder.getDistance();
			lastRecordedTime = System.nanoTime() / 1000;
			
			return value;
		}
		
		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {}
	}
	
	class MoveLadderPIDOutput implements PIDOutput {
		@Override
		public void pidWrite(double output) {
			ladder.set(Math.min(0.5, output));		// limit speed to 0.5
			SmartDashboard.putString("PID Ladder Output", (int)output + "." + (int)(output * 100) % 100);	// or just ("" + output).substring(0, 5 + ("" + output).indexOf("."))
		}
	}
}