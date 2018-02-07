package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// ENCODER USING INCHES

public class Ladder {
	public boolean movingLadder = false;
	
	private Talon ladder, coiler, claw;
	private Encoder encoder;
	private PIDController pid;
	
	public Ladder(Talon ladder, Talon coiler, Talon claw, Encoder encoder) {
		this.ladder = ladder;
		this.coiler = coiler;
		this.claw = claw;
		this.pid = new PIDController("P constant", "I Constant", "D Constant", new EncoderRatePIDSource(), new MoveLadderPIDOutput());		// compile error? set the constants
	}
	
	public void disablePID() {
		pid.disable();
	}
	
	public boolean extendLadder() { 
		if(!pid.isEnabled()) { 
			pid.enable();
			pid.setSetpoint("insert value here");
		} else if(pid.getSetpoint() != "correct value") {
			pid.setSetpoint("correct value");
		}
		movingLadder = true;
		// use pid thing
	}
	
	public void retractLadder() {
		if(!pid.isEnabled()) {
			pid.enable();
			pid.setSetpoint("insert value here");
		} else if(pid.getSetpoint() != "correct value") {
			pid.setSetpoint("correct value");
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
	
	class EncoderRatePIDSource implements PIDSource {
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