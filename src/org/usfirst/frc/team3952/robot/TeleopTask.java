package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopTask extends Task {
	private Controller controller;
	private MecanumDrive drive;
	
	public TeleopTask(Robot robot) {
		controller = robot.getController();
		drive = robot.getDrive();
		
	}
	
	@Override
	public boolean run() {
		double hor = controller.getHorizontalMovement(), 
			   lat = controller.getLateralMovement(), 
			   rot = controller.getRotation();
		drive.driveCartesian(hor, 
							 lat, 
							 rot);
		
		SmartDashboard.putString("Teleop Horizontal Movement: ", "" + hor);
		SmartDashboard.putString("Teleop Lateral Movement: ", "" + lat);
		SmartDashboard.putString("Teleop Rotation: ", "" + rot);
		return false;
	}
	
	@Override
	public void cancel() {
		drive.driveCartesian(0, 0, 0);
	}
	
	@Override
	public String toString() {
		return "Teleop";
	}
}