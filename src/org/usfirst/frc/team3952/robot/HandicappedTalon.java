package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.Talon;

public class HandicappedTalon extends Talon {

	private double handicap;
	
	public HandicappedTalon(int channel, double handicap) {
		super(channel);
		// TODO Auto-generated constructor stub
		this.handicap = handicap;
	}
	
	@Override
	public void set(double speed){
		double rawSpeed = (speed + Math.signum(speed) * handicap);
		if(rawSpeed < -1) rawSpeed = -1;
		if(rawSpeed > 1) rawSpeed = 1;
		super.set(rawSpeed);
	}
	
	
}
