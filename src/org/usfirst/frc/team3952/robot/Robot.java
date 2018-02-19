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
 * climber			4
 * claw				5
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
	private DigitalInput topLimit, armBottomLimit, clawOpeningLimit, clawClosingLimit;
	private Ladder ladder;
	private Servo servo;
	private boolean dropClaw, clawWillOpen;
	
	//=== Task System ===\\
	
	private Task currentTask;
	private Queue<Task> autonomousQueue;
	private SendableChooser<String> autonomousChooser;
	
	private boolean isStart = true;
	private boolean autonomousInited = false;
	public static long startMillis;
	public static boolean shouldStop = false;
	
	
	@Override
	public void robotInit() {
		
		System.out.println("entering init");
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
		claw = new Talon(5);
		
		ladderEncoder = new Encoder(4, 5, false, Encoder.EncodingType.k2X);
		ladderEncoder.setDistancePerPulse(1);	// We are not going to calibrate
		
		topLimit = new DigitalInput(6);
//		frameBottomLimit = new DigitalInput(8);
		armBottomLimit = new DigitalInput(7);
		clawOpeningLimit = new DigitalInput(8);
		clawClosingLimit = new DigitalInput(9);
		
		ladder = new Ladder(ladderT, coiler, claw, ladderEncoder, topLimit, armBottomLimit, clawOpeningLimit, clawClosingLimit);
		
		servo = new Servo(8);
		dropClaw = true;
		
		
		
		
		
		//=== Task System Initialization===\\
		
		autonomousQueue = new LinkedList<>();
		currentTask = new MultiTask(new TeleopTask(this), null);
//		gyro.calibrate(); // Not necessary
		
		// SmartDashboard selecting autonomous
		autonomousChooser = new SendableChooser<>();
		autonomousChooser.addObject("Starting Left", "L");
		autonomousChooser.addObject("Starting Middle", "M");
		autonomousChooser.addDefault("Starting Right", "R");
		SmartDashboard.putData("Autonomous Initial Position", autonomousChooser);
		
		clawWillOpen = true;
		
		startMillis = System.currentTimeMillis();
		shouldStop = false;

	}
	
	
	//=== Disabled ===\\
	
	@Override
	public void disabledInit() {}

	//=== Teleop ===\\

	@Override
	public void teleopInit() {}
	
	
	/** called every ~20 ms*/
	@Override
	public void teleopPeriodic() {
		
		//---unsafe (not using limit switches or timers) claw------------//
//		if(controller.openClaw()) {
//			ladder.openClawUnsafe();
//		} else if(controller.closeClaw()){
//			ladder.closeClawUnsafe();
//		} else {
//			claw.set(0);
//		}
		
		//------------------------ controller halfing speed-------//
		if(controller.toggleSpeed()){
			controller.toggleTheSpeed();
			ladder.toggleLadder();
		}
		SmartDashboard.putString("controllerSpeed", controller.max + ":   " + controller.maxx);
		SmartDashboard.putString("ladder stuffs", ladder.ladderSpeedUp + "  :" + ladder.ladderSpeedDown);
		
		
		//------- ladder ----------------------------------------------//
		if(controller.extendLadder()){
			ladder.extendLadder();
		} else if(controller.retractLadder()){
			ladder.retractLadder();
		} else {
			ladderT.set(0);	// TODO: should be fine for now
		}

		//------climber---------------------------------------------//
		if(controller.coil()) {
			coiler.set(-0.2);
		} else {
			coiler.set(0);
		}
		
		//--------claw using toggling-----------------------------------//
		if(controller.triggerClaw()) {
			//if we aren't opening claw, we can go
			//if we are opening, we can only go if clawOpening is Pressed (clawOpeningLimit.get() == false)
			if(!clawWillOpen || clawWillOpen && !clawOpeningLimit.get() ) 
				clawWillOpen = !clawWillOpen;
			startMillis = System.currentTimeMillis();
			isStart = false;
		}	
		if(!isStart) {
			if(clawWillOpen) {
				ladder.openClaw();
			} else {
				ladder.closeClaw();
			}
		}
		
//		if(controller.joystick.getRawButton(1)){
//			claw.set(-1);
//		} else if(controller.joystick.getRawButton(2)){
//			claw.set(0.4);
//		} else{
//			claw.set(0);
//		}
		
		// Ladder safety
		//ladder.safety();
		
		// ---------------Task Canceling---------------------------------///
		if(controller.cancelTask()) {
			currentTask.cancel();
			currentTask = new MultiTask(new TeleopTask(this), null);
		}
		
		//---------- Task running----------------------------------------//
		if(currentTask.run()){
			currentTask = new MultiTask(new TeleopTask(this), null);
		}
		
		// ---------------SmartDashboard----------------------------------//
		displayOnSmartDashboard();
	}
	
	
	//==== Autonomous ===//
	
	/**
	 * This will run every time you press enable in auto
	 */
	@Override
	public void autonomousInit(){
		
		
		//
	//	Servo servo =new Servo(1);
	//	servo.set(0.5);
		//creates servo and then turns it 45 degrees to the left from a 0-1 scale;
		//TEMPORARY!!!!
		SmartDashboard.putString("Autonomous Status", "Init Starting");
//		autonomousQueue.add(new MoveForwardTask(this, 6));
//		autonomousQueue.add(new TurnTask(this, -20));
//		autonomousQueue.add(new MoveForwardTask(this, 3));
		SmartDashboard.putString("Autonomous Status", "Init Ending");
	
	}
	
	@Override
	public void autonomousPeriodic(){
		// Immediately after the game starts, drop down the claw(can this be put in autonomousInit()?)
//		if(dropClaw) {
//			servo.setAngle(90);	// TODO: still requires testing
//			dropClaw = false;
//		}
		SmartDashboard.putString("asdf",DriverStation.getInstance().getGameSpecificMessage());
		//runs only once when we get real data
		if(!autonomousInited && !DriverStation.getInstance().getGameSpecificMessage().equals("")){
			String stuff = DriverStation.getInstance().getGameSpecificMessage(); // e.g. LRL
			
			String switchPos = stuff.substring(0, 1);
			String scalePos = stuff.substring(1, 2);
			//we don't care about their switch POS
			
			String ourPosition = autonomousChooser.getSelected(); // L, R, M
			SmartDashboard.putString("In Game Specific Message", stuff);
		//	autonomousQueue.add(new MoveForwardTask(this, 3));	// Move forward 7 in = 7ft to move pass the line
									// 8.5 because of calibration
			
			
//			//middle position with left scale open
//			if(ourPosition.equals("M") && scalePos.equals("L")){
//
//				autonomousQueue.add(new TurnTask(this,-90));
//				autonomousQueue.add(new MoveForwardTask(this, 4.5));
//				autonomousQueue.add(new TurnTask(this,90));
//				autonomousQueue.add(new MoveForwardTask(this,21));
//				ladder.setPos(2);
//				autonomousQueue.add(new TurnTask(this, 90));
//				ladder.openClaw();
//			}
//			//middle position with right scale open
//			if(ourPosition.equals("M") && scalePos.equals("R")){
//				autonomousQueue.add(new TurnTask(this, 90));
//				autonomousQueue.add(new MoveForwardTask(this, 8.9));
//				autonomousQueue.add(new TurnTask(this,-90));
//				autonomousQueue.add(new MoveForwardTask(this, 21));
//				ladder.setPos(2);
//				autonomousQueue.add(new TurnTask(this, -90));
//				ladder.openClaw();
//			}
//			//left position with left switch
//			if(ourPosition.equals("L") && switchPos.equals("L")){
//				autonomousQueue.add(new MoveForwardTask(this, 10.3));
//				ladder.setPos(1);
//				autonomousQueue.add(new TurnTask(this, 90));
//				ladder.openClaw();
//			}
//			// left position with right switch
//			if(ourPosition.equals("L") && switchPos.equals("R")){
//				autonomousQueue.add(new TurnTask(this,90));
//				autonomousQueue.add(new MoveForwardTask(this,18.7));
//				autonomousQueue.add(new TurnTask(this,-90));
//				autonomousQueue.add(new MoveForwardTask(this, 10.3));
//				ladder.setPos(1);
//				autonomousQueue.add(new TurnTask(this,-90));	
//				ladder.openClaw();			
//			}	
//			//right position with right switch
//			if(ourPosition.equals("R") && switchPos.equals("R")){
//				autonomousQueue.add(new MoveForwardTask(this, 10.3));
//				ladder.setPos(1);
//				autonomousQueue.add(new TurnTask(this, -90));
//				ladder.openClaw();
//			}
//			// right position with left switch
//			if(ourPosition.equals("R") && switchPos.equals("L")){
//				autonomousQueue.add(new TurnTask(this,-90));
//				autonomousQueue.add(new MoveForwardTask(this,18.7));
//				autonomousQueue.add(new TurnTask(this,90));
//				autonomousQueue.add(new MoveForwardTask(this, 10.3));
//				ladder.setPos(1);
//				autonomousQueue.add(new TurnTask(this,90));
//				ladder.openClaw();			
//			}	
//			//just so we don't insert everything into queue again
			autonomousInited = true;
		}
		
		SmartDashboard.putString("Autonomous Queue: ", autonomousQueue.toString());
		if(!autonomousQueue.isEmpty()){
			if(autonomousQueue.peek().run()){
				autonomousQueue.poll();
			}
			displayOnSmartDashboard();
			//SmartDashboard.putString("Current Task: ", autonomousQueue.peek().toString());
			
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
	
	
	//========================SMART DASHBOARD STUFFS========================================//
	public void displayOnSmartDashboard(){
		
		// Realistic not needed for now
//		SmartDashboard.putString("Front Left: ", "" + frontLeft.get());
//		SmartDashboard.putString("Front Right: ", "" + frontRight.get());
//		SmartDashboard.putString("Rear Left: ", "" + rearLeft.get());
//		SmartDashboard.putString("Rear Right: ", "" + rearRight.get());
//		SmartDashboard.putString("Gyro Rate: ", "" + gyro.getRate());

		//actually needed now:		
		
		SmartDashboard.putString("Current Task: ", currentTask.toString());
		
		SmartDashboard.putString("Left Encoder: ", "" + leftEncoder.getDistance());
		SmartDashboard.putString("Right Encoder: ", "" + rightEncoder.getDistance());
		SmartDashboard.putNumber("Left Encoder Pulses", leftEncoder.getRaw());
		SmartDashboard.putNumber("Right Encoder pulses", rightEncoder.getRate());
		
		
		SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
		
		SmartDashboard.putString("Top Limit: ", "" + topLimit.get());
		SmartDashboard.putString("Arm Bottom Limit: ", "" + armBottomLimit.get());
		
		SmartDashboard.putString("Claw Opened Max: ", "" + !clawOpeningLimit.get());
		SmartDashboard.putString("Claw Closed Max: ", "" + !clawClosingLimit.get());
		SmartDashboard.putString("Claw Opening: ", "" + clawWillOpen);
		SmartDashboard.putString("Claw Power: ", "" + claw.get() + " " + (claw.get() < 0? "Opening": "Closing"));
		SmartDashboard.putString("Ladder Encoder: ", "" + ladderEncoder.getDistance());	
		
	}
}