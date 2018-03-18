package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.Talon;

/**
 * Sipmle class to representing the climber (also known as coiler)
 *
 */
public class Climber {
	private Talon coiler;
	
	public Climber(Talon coiler){
		this.coiler = coiler;
	}
	
	public void climb(){
		coiler.set(-0.5);
	}
	
	public void breakString(){
		coiler.set(-0.3);
	}
	
	public void stop(){
		coiler.set(0);
	}
	
}
