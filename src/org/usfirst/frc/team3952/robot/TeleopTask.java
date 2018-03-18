package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopTask extends Task {
	private Controller controller;
	private MecanumDrive drive;
	private Ladder ladder;
	private Climber climber;
	
	public TeleopTask(Robot robot) {
		controller = robot.getController();
		drive = robot.getDrive();
		ladder = robot.getLadder();
		climber = robot.getClimber();
	}
	
	@Override
	public boolean run() {
		double hor = controller.getHorizontalMovement(), 
			   lat = controller.getLateralMovement(), 
			   rot = controller.getRotation();
		drive.driveCartesian(hor, lat, rot); //recall that there is a 4th parameter, gyro. Add if wanted
		
		//------- ladder ----------------------------------------------//
		if(controller.extendLadder()) {
			ladder.extendLadder();
		} else if(controller.retractLadder()) {
			ladder.retractLadder();
		} else {
			ladder.stopLadder();
		}

		//------climber---------------------------------------------//
		if(controller.coil()) {
			climber.climb();
		} else {
			climber.stop();
		}
		
		//--------claw using toggling-----------------------------------//
		
		//the limit switches are both dead so not happening....
//		if(controller.joystick.getRawButton(2)) {
//			ladder.openClawUnsafe();
//		}else {
//			ladder.stopClaw();
//			if(controller.triggerClaw()) {
//				//if we aren't opening claw, we can go
//				//if we are opening, we can only go if clawOpening is Pressed (clawOpeningLimit.get() == false)
//				if(!clawWillOpen || clawWillOpen && !clawOpeningLimit.get() ) 
//					clawWillOpen = !clawWillOpen;
//				startMillis = System.currentTimeMillis();
//			}	
//			if(clawWillOpen) {
//				ladder.openClaw();
//			} else {
//				ladder.closeClaw();
//			}
//		}
		
		if(controller.unsafeOpenClaw()) {	// VERY IMPORTANT: 2 for Controller, 3 for BadController
			ladder.openClawUnsafe();
		} else if(controller.unsafeCloseClaw()) {
			ladder.closeClawUnsafe();
		} else {
			ladder.stopClaw();
		}
		
		SmartDashboard.putString("Teleop Horizontal Movement: ", "" + hor);
		SmartDashboard.putString("Teleop Lateral Movement: ", "" + lat);
		SmartDashboard.putString("Teleop Rotation: ", "" + rot);
		return false;
	}
	
	@Override
	public void cancel() {
		drive.driveCartesian(0, 0, 0);
		ladder.stopLadder();
		ladder.stopClaw();
		climber.stop();
	}
	
	@Override
	public String toString() {
		return "Teleop";
	}
}