package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import java.util.*;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * rear left 		3	
 * rear right 		2
 * front right		0
 * front left	 	1
 * ladder 			6
 * climber			4, 5
 * 
 * Tasks:
 * 	-Ladder Code Test
 * 	-Claw Code
 * 	-Driving focus
 * 		- Ask Drivers if they want manuall control ladder? or nah
 * 
 * 	-Autonomous later.
 * 	
 * 
 */
public class Robot extends IterativeRobot {
	//=== Drive ===\\
	
	private Controller controller;
	private Talon frontLeft, frontRight, rearLeft, rearRight;
	private Encoder rightEncoder, leftEncoder;
	private ADXRS450_Gyro gyro;
	private MecanumDrive drive;
	
	//=== Ladder, Claw, & Winch ===\\
	
	private Talon ladderT, coiler, claw;
	private Encoder ladderEncoder;
	private DigitalInput topLimit, frameBottomLimit, armBottomLimit, clawSwitch;
	private Ladder ladder;
	private Servo servo;
	private boolean dropClaw;
	
	//=== Task System ===\\
	
	private Task currentTask;
	private Queue<Task> autonomousQueue;
	private SendableChooser<String> autonomousChooser;
	
	@Override
	public void robotInit() {
		//=== Drive Initialization ===\\
		
		controller = new Controller();
		
		frontLeft = new Talon(1);
		frontRight = new Talon(0);
		rearLeft = new Talon(3);
		rearRight = new Talon(2);			
		
		// Initialize Encoders
		rightEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k2X); // We can also try k4 for more accuracy.
		rightEncoder.setDistancePerPulse(0.0078);		
		leftEncoder = new Encoder(1, 0, false, Encoder.EncodingType.k2X);
		leftEncoder.setDistancePerPulse(0.00677);
		
		gyro = new ADXRS450_Gyro();
		
		// Initialize Drive Train
		
		drive = new MecanumDrive(frontLeft,
								 rearLeft, 
								 frontRight, 
								 rearRight);
		
		//=== Ladder, Claw, & Winch Initialization ===\\
		
		ladderT = new Talon(6);		
		coiler = new Talon(4);    
		claw = new Talon(9);
		
		ladderEncoder = new Encoder(4, 5, false, Encoder.EncodingType.k2X);
		ladderEncoder.setDistancePerPulse(1);	// We are not going to calibrate
		
		topLimit = new DigitalInput(9);
		frameBottomLimit = new DigitalInput(7);
		armBottomLimit = new DigitalInput(8);
		clawSwitch = new DigitalInput(10);
		
		ladder = new Ladder(ladderT, coiler, claw, ladderEncoder, topLimit, frameBottomLimit, armBottomLimit, clawSwitch);
		
		servo = new Servo(8);
		dropClaw = true;
		
		//=== Task System Initialization===\\
		
		autonomousQueue = new LinkedList<>();
		currentTask = new TeleopTask(this);
//		gyro.calibrate(); // Not necessary
		
		// SmartDashboard selecting autonomous
		autonomousChooser = new SendableChooser<>();
		autonomousChooser.addObject("Starting Left", "L");
		autonomousChooser.addObject("Starting Middle", "M");
		autonomousChooser.addDefault("Starting Right", "R");
		SmartDashboard.putData("Autonomous Initial Position", autonomousChooser);
	}
	
	
	//=== Disabled ===\\
	
	@Override
	public void disabledInit() {}

	//=== Teleop ===\\

	@Override
	public void teleopInit() {}
	
	@Override
	public void teleopPeriodic() {
		if(controller.extendLadder()){
			ladder.extendLadder();
		} else if(controller.retractLadder()){
			ladder.retractLadder();
		} else {
			//ladder.pulseIfNotMoving(); // So if we don't want it move, it won't
			ladderT.set(0);	// TODO: should be fine for now
		}
		
		if(controller.ladderUp()) {
			if(currentTask instanceof MultiTask){
				((MultiTask) currentTask).cancelSecondaryTask();
				((MultiTask) currentTask).setSecondaryTask(new MoveLadderTask(this, 1));
			}
		} else if(controller.ladderDown()) {
			if(currentTask instanceof MultiTask){
				((MultiTask) currentTask).cancelSecondaryTask();
				((MultiTask) currentTask).setSecondaryTask(new MoveLadderTask(this, -1));
			}
		}
		
		if(controller.coil()) {
			ladder.coil();
		}
		
		// TODO: Not using this because it is probably going to be buggy and problematic with the available testing time
//		if(controller.pressedExtendLadder()){
//			if(currentTask instanceof MultiTask){
//				((MultiTask) currentTask).cancelSecondaryTask();
//				((MultiTask) currentTask).setSecondaryTask(new MoveLadderTask(this, 1));
//			}
//		} else if(controller.pressedRetractLadder()){
//			if(currentTask instanceof MultiTask){
//				((MultiTask) currentTask).cancelSecondaryTask();
//				((MultiTask) currentTask).setSecondaryTask(new MoveLadderTask(this, -1));
//			}
//		}
		
		// Ladder safety
		ladder.safety();
		
		// Task Canceling
		if(controller.cancelTask()) {
			currentTask.cancel();
			currentTask = new MultiTask(new TeleopTask(this), null);
		}
		
		// Task running
		if(currentTask.run()){
			currentTask = new MultiTask(new TeleopTask(this), null);
		}
		
		// SmartDashboard
		SmartDashboard.putString("Current Task: ", currentTask.toString());
		
		// Realistic not needed for now
//		SmartDashboard.putString("Front Left: ", "" + frontLeft.get());
//		SmartDashboard.putString("Front Right: ", "" + frontRight.get());
//		SmartDashboard.putString("Rear Left: ", "" + rearLeft.get());
//		SmartDashboard.putString("Rear Right: ", "" + rearRight.get());

		SmartDashboard.putString("Left Encoder: ", "" + leftEncoder.getDistance());
		SmartDashboard.putString("Right Encoder: ", "" + rightEncoder.getDistance());
		
		SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
//		SmartDashboard.putString("Gyro Rate: ", "" + gyro.getRate());
		
		SmartDashboard.putString("Top Limit: ", "" + topLimit.get());
		SmartDashboard.putString("Ladder Encoder: ", "" + ladderEncoder.getDistance());	
	}
	
	
	//==== Autonomous ===//
	
	/**
	 * This will run every time you press enable in auto
	 */
	@Override
	public void autonomousInit(){
		String stuff = DriverStation.getInstance().getGameSpecificMessage(); // e.g. LRL
		String ourSwitchPos = stuff.substring(0, 1);
		String scalePos = stuff.substring(1, 2);
		String ourPosition = autonomousChooser.getSelected(); // L, R, M
		
		// Adapt Chris's code
	}
	
	@Override
	public void autonomousPeriodic(){
		// Immediately after the game starts, drop down the claw(can this be put in autonomousInit()?)
		if(dropClaw) {
			servo.setAngle(90);	// TODO: still requires testing
			dropClaw = false;
		}
		
		SmartDashboard.putString("Autonomous Queue: ", autonomousQueue.toString());
		if(!autonomousQueue.isEmpty()){
			if(autonomousQueue.peek().run()){
				autonomousQueue.poll();
			}
			
			SmartDashboard.putString("Current Task: ", currentTask.toString());

			SmartDashboard.putString("Right Encoder: ", "" + rightEncoder.getDistance());
			SmartDashboard.putString("Left Encoder: ", "" + leftEncoder.getDistance());
			SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
			
		}
	}
	
	//=== Test ===\\
	
	@Override
	public void testPeriodic(){
		teleopPeriodic();
	}
	
	//=== Getters ===\\
	
	public Controller getController() {
		return controller;
	}
	
	public MecanumDrive getDrive() {
		return drive;
	}
	
	public Encoder getLeftEncoder(){
		return leftEncoder;
	}
	
	public Encoder getRightEncoder(){
		return rightEncoder;
	}
	
	public ADXRS450_Gyro getGyro(){
		return gyro;
	}
	
	public Ladder getLadder() {
		return ladder;
	}
}