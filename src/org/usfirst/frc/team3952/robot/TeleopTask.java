package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class TeleopTask extends Task {
	private Controller controller;
	private MecanumDrive drive;
	
	public TeleopTask(Robot robot) {
		controller = robot.getController();
		drive = robot.getDrive();
	}
	
	@Override
	public boolean run() {
		drive.driveCartesian(controller.getLateralMovement(), 
							 controller.getHorizontalMovement(), 
							 controller.getRotation());
		return false;
	}
	
	@Override
	public void cancel() {
		drive.driveCartesian(0, 0, 0);		// needed?
	}
	
	@Override
	public String toString() {
		return "Teleop";
	}
}