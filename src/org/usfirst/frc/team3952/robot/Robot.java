package org.usfirst.frc.team3952.robot;

import edu.wpi.cscore.UsbCamera;
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
 * 	-Limit switches
 * 	-Ladder reset
 *  -MoveForward Straighting test
 *  -
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
	private boolean dropClaw, stopCoiler;
	private long servoStartTime;
	
	//=== Task System ===\\
	
	private Task currentTask;
	private Queue<Task> autonomousQueue;
	private SendableChooser<String> autonomousChooser;

	private boolean clawWillOpen;
	private boolean autonomousInited = false;
	public static long startMillis;
//	public static boolean shouldStop = false;			// not used
		
	//=== Camera ===\\
	private UsbCamera camera;
	
	@Override
	public void robotInit() {
		
		System.out.println("Entering Init");
		//=== Drive Initialization ===\\
		controller = new Controller();
		
		frontLeft = new HandicappedTalon(1, 0);
		frontRight = new HandicappedTalon(0,0);
		rearLeft = new HandicappedTalon(3,0);
		rearRight = new HandicappedTalon(2,0);			
		
		// Initialize Encoders
		rightEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k1X); // We can also try k4 for more accuracy.
		rightEncoder.setDistancePerPulse(0.0078);		
		leftEncoder = new Encoder(1, 0, false, Encoder.EncodingType.k1X);
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
		
		servo = new Servo(7);
		
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
		
//		startMillis = System.currentTimeMillis();
//		shouldStop = false;
		
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(640, 480); //(160, 120)
		
		stopMotors();
	}
	//=== Disabled ===\\
	
	@Override
	public void disabledInit() {}

	//=== Teleop ===\\
	@Override
	public void teleopInit() {
		stopMotors();
		currentTask = new TeleopTask(this);
		autonomousQueue = new LinkedList<>();
		
//		dropClaw = true;								// don't move these into teleop
//		servoStartTime = System.nanoTime() / 1000000L;
//		servo.setPosition(0);
	}
	
	
	/** called every ~20 ms*/
	@Override
	public void teleopPeriodic() {
		
		servo.setPosition(0.515); //stop servo
//		if(dropClaw && System.nanoTime() / 1000000L - servoStartTime >= 5500) {	// stop servo after ~5 seconds
//			servo.setPosition(0.515);		// 0.515 makes it stop
//			dropClaw = false;
//		}
		
		
		//---unsafe (not using limit switches or timers) claw------------//
//		if(controller.openClaw()) {
//			ladder.openClawUnsafe();
//		} else if(controller.closeClaw()){
//			ladder.closeClawUnsafe();
//		} else {
//			claw.set(0);
//		}
		
		//------------------------ controller halving speed-------//
		if(controller.toggleSpeed()) {
			//temporary got rid of slow mode
			//controller.toggleTheSpeed();
			//ladder.toggleLadder();
		}
		SmartDashboard.putString("Controller Speed: ", controller.max + " / " + controller.maxx);
		SmartDashboard.putString("Ladder Speed: ", ladder.ladderSpeedUp + " / " + ladder.ladderSpeedDown);
		
		
		//------- ladder ----------------------------------------------//
		if(controller.extendLadder()) {
			ladder.extendLadder();
		} else if(controller.retractLadder()) {
			ladder.retractLadder();
		} else {
			ladderT.set(0);	// TODO: should be fine for now
		}

		//------climber---------------------------------------------//
		if(controller.coil()) {
			coiler.set(-0.5);
		} else {
			coiler.set(0);
		}
		
		//--------claw using toggling-----------------------------------//
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
		
		if(controller.joystick.getRawButton(2)) {	// VERY IMPORTANT: 2 for Controller, 3 for BadController
			ladder.openClawUnsafe();
		} else if(controller.joystick.getTrigger()) {
			ladder.closeClawUnsafe();
		} else {
			ladder.stopClaw();
		}
		
		// Ladder safety
		//ladder.safety();
		
		//---------- Task running----------------------------------------//
		if(currentTask.run()){
			currentTask.cancel();
			currentTask = new TeleopTask(this);
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
		dropClaw = true;								// don't move these into teleop
		stopCoiler = false;
		servoStartTime = System.nanoTime() / 1000000L;
		//servo.setPosition(0);
		
		//autonomousQueue.add(new ResetLadderTask(this));
		//autonomousQueue.add(new MoveForwardTask(this, 6, false));
		stopMotors();
		coiler.set(-0.3);
		//
	//	Servo servo =new Servo(1);
	//	servo.set(0.5);
		//creates servo and then turns it 45 degrees to the left from a 0-1 scale;
		//TEMPORARY!!!!
//		SmartDashboard.putString("Autonomous Status", "Init Starting");
//		autonomousQueue.add(new MoveForwardTask(this, 6));
//		autonomousQueue.add(new TurnTask(this, -20));
//		autonomousQueue.add(new MoveForwardTask(this, 3));
//		SmartDashboard.putString("Autonomous Status", "Init Ending");
		autonomousInited = false;
	}
	
	@Override
	public void autonomousPeriodic(){
//		if(dropClaw && System.nanoTime() / 1000000L - servoStartTime >= 12000) {	// stop servo after ~5 seconds
//			servo.setPosition(0.515);		// 0.515 makes it stop
//			dropClaw = false;
//		}		
		if(dropClaw && System.nanoTime() / 1000000L - servoStartTime >= 1000) {
			coiler.set(0);
			dropClaw = false;
			//stopCoiler = true;
		}/* else if(stopCoiler && System.nanoTime() / 1000000L - servoStartTime >= 2000) {
			coiler.set(0);
			stopCoiler = false;
		}*/
		
		/**
		 * To test at the field
		 * 	Drop claw again testing lo,l
		 * 	Cube in autonomous holding thingy
		 * 		Does it drop when claw drop
		 * 	Drive stragiht (MoveForward Task)
		 * 	Autonomous sequences.... 
		 * 
		 * 
		 * Tasks:
		 * 	Smart Dashboard fix
		 * 	
		 */
		
		// Immediately after the game starts, drop down the claw(can this be put in autonomousInit()?)
		SmartDashboard.putString("Game Specific Message: ",DriverStation.getInstance().getGameSpecificMessage());
		//runs only once when we get real data
		if(System.nanoTime() / 1000000L - servoStartTime >= 5000 && !autonomousInited/* && !DriverStation.getInstance().getGameSpecificMessage().equals("")*/){
			String stuff = DriverStation.getInstance().getGameSpecificMessage(); // e.g. LRL
			String switchPos = stuff.substring(0, 1);
			String scalePos = stuff.substring(1, 2);
			String ourPosition = "R"; // L, R, M //
			///String ourPosition = autonomousChooser.getSelected(); // L, R, M
			
//			SmartDashboard.putString("In Game Specific Message", stuff);
			
			//autonomousQueue.add(new MultiTask(new MoveForwardTask(this, 3, false), new MoveLadderTask(this, 1)));
			
			if(ourPosition.equals("L")){
				//autonomousQueue.add(new MoveLadderTask(this, 1));
				autonomousQueue.add(new MultiTask(new MoveForwardTask(this, 9, false), new MoveLadderTask(this, 1))); //17.7
				if(switchPos.equals("L")) {
					autonomousQueue.add(new TurnTask(this, 90));  //this is dangerous but we will try.
					autonomousQueue.add(new MoveForwardTask(this, 3, false));
					autonomousQueue.add(new MoveForwardTask(this, 2, true)); //do nudge;
					autonomousQueue.add(new OpenClawTask(this));
				}
			} else if(ourPosition.equals("M")){
				// we going backwards yay
				int isRight = 0; //change this to -1 if left.
				
				if(switchPos.equals("L")) isRight = -1; //we are going left
				if(switchPos.equals("R")) isRight = 1; //we are going right;
				
				autonomousQueue.add(new MultiTask(new MoveForwardTask(this, 4, false), new MoveLadderTask(this, 1))); //safewty firsat, 4.4
				autonomousQueue.add(new TurnTask(this, 90 * isRight)); 
				autonomousQueue.add(new MoveForwardTask(this, 3, false)); //9 for all the way
				autonomousQueue.add(new TurnTask(this, -90 * isRight)); 
				autonomousQueue.add(new MoveForwardTask(this, 4, false));
				autonomousQueue.add(new MoveForwardTask(this, 3, true));
			} else if(ourPosition.equals("R"))
			{
				//autonomousQueue.add(new MoveLadderTask(this, 1));
				autonomousQueue.add("R".equals(switchPos) ? new MultiTask(new QueuedTask(new MoveForwardTask(this, 9, false), new TurnTask(this, -90)), new MoveLadderTask(this, 1) : new MultiTask(new MoveForwardTask(this, 9, false), new TurnTask(this, -90)));
				//(new MultiTask(new MoveForwardTask(this, 9, false)), new MoveLadderTask(this, 1))); //17.7
				
				if(switchPos.equals("R")) {
					autonomousQueue.add(new MoveForwardTask(this, 3, false));
					autonomousQueue.add(new MoveForwardTask(this, 2, true)); //do nudge;
					autonomousQueue.add(new OpenClawTask(this));
				}
				//
				//
				//autonomousQueue.add(new MoveForwardTask(this, ))
			}
			
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
/*================================================Commented for testing==============================================================*\
		// Immediately after the game starts, drop down the claw(can this be put in autonomousInit()?)

		SmartDashboard.putString("Game Specific Message: ",DriverStation.getInstance().getGameSpecificMessage());
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
//				autonomousQueue.add(new MultiTask(new MoveForwardTask(this,21), new MoveLadderTask(this, 2)));
//				autonomousQueue.add(new TurnTask(this, 90));
//				autonomousQueue.add(new ChangeClawTask(this, true));
//			}
//			//middle position with right scale open
//			if(ourPosition.equals("M") && scalePos.equals("R")){
//				autonomousQueue.add(new TurnTask(this, 90));
//				autonomousQueue.add(new MoveForwardTask(this, 8.9));
//				autonomousQueue.add(new TurnTask(this,-90));
//				autonomousQueue.add(new MultiTask(new MoveForwardTask(this,21), new MoveLadderTask(this, 2)));
//				autonomousQueue.add(new TurnTask(this, -90));
//				autonomousQueue.add(new ChangeClawTask(this, true));
//			}
//			//left position with left switch
//			if(ourPosition.equals("L") && switchPos.equals("L")){
//				autonomousQueue.add(new MultiTask(new MoveForwardTask(this, 10.3), new MoveLadderTask(this, 1)));
//				autonomousQueue.add(new TurnTask(this, 90));
 * 
//				ladder.openClaw();
//			}
//			// left position with right switch
//			if(ourPosition.equals("L") && switchPos.equals("R")){
//				autonomousQueue.add(new TurnTask(this,90));
//				autonomousQueue.add(new MoveForwardTask(this,18.7));
//				autonomousQueue.add(new TurnTask(this,-90));
//				autonomousQueue.add(new MultiTask(new MoveForwardTask(this, 10.3), new MoveLadderTask(this, 1)));
//				autonomousQueue.add(new TurnTask(this,-90));	
//				ladder.openClaw();			
//			}	
//			//right position with right switch
//			if(ourPosition.equals("R") && switchPos.equals("R")){
//				autonomousQueue.add(new MultiTask(new MoveForwardTask(this, 10.3), new MoveLadderTask(this, 1)));
//				autonomousQueue.add(new TurnTask(this, -90));
//				ladder.openClaw();
//			}
//			// right position with left switch
//			if(ourPosition.equals("R") && switchPos.equals("L")){
//				autonomousQueue.add(new TurnTask(this, -90));
//				autonomousQueue.add(new MoveForwardTask(this, 18.7));
//				autonomousQueue.add(new TurnTask(this,90));
//				autonomousQueue.add(new MultiTask(new MoveForwardTask(this, 10.3), new MoveLadderTask(this, 1)));
//				autonomousQueue.add(new TurnTask(this,90));
//				ladder.openClaw();			
//			}	
//			//just so we don't insert everything into queue again
			autonomousInited = true;
		}
\*========================================================================================================================================*/
		
		
	}
	
	//=== Test ===\\
	@Override
	public void testInit(){
		autonomousQueue = new LinkedList<>();
		//autonomousQueue.add(new MoveLadderTask(this, 1));
		//autonomousQueue.add(new TurnTask(this, 180));
		//autonomousQueue.add(new TurnTask(this, -180));
		//autonomousQueue.add(new MultiTask(new MoveForwardTask(this, 3, false), new MoveLadderTask(this, 1)));
		autonomousQueue.add(new OpenClawTask(this));
	}
	
	
	@Override
	public void testPeriodic(){
		if(!autonomousQueue.isEmpty()){
			if(autonomousQueue.peek().run()){
				autonomousQueue.poll();
			}
			displayOnSmartDashboard();
			SmartDashboard.putString("Current Task: ", autonomousQueue.peek() == null ? "null" : autonomousQueue.peek().toString());
			
		}
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
	
	public boolean getClawWillOpen(){
		return clawWillOpen;
	}
	
	public Ladder getLadder() {
		return ladder;
	}
	
	//==================So the talons dont keep moving in back to back matches==========//
	
	public void stopMotors() {
		ladder.stopClaw();
		ladder.stopLadder();
		ladder.stopCoiling();
		drive.driveCartesian(0, 0, 0);
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
		
		SmartDashboard.putNumber("Left Encoder: ", leftEncoder.getDistance());
		SmartDashboard.putNumber("Right Encoder: ", rightEncoder.getDistance());
		
		SmartDashboard.putNumber("Gyro: ", ((int)gyro.getAngle() + 180) % 360 - 180);	// put angle in range [-180, 180]
		
		SmartDashboard.putBoolean("Ladder Top Limit: ", topLimit.get());
		SmartDashboard.putBoolean("Ladder Bottom Limit: ", armBottomLimit.get());
		
		SmartDashboard.putBoolean("Moving", clawWillOpen ^ ladder.clawIsOpenedAllTheWayOrIsClosedAllTheWay());
		SmartDashboard.putBoolean("Fully Open Close", ladder.clawIsOpenedAllTheWayOrIsClosedAllTheWay());
		SmartDashboard.putBoolean("Claw Will Open", clawWillOpen);
		
		SmartDashboard.putString("Claw Power: ", "" + claw.get() + " " + (claw.get() < 0 ? "Opening": "Closing"));
		SmartDashboard.putNumber("Ladder Encoder: ", ladderEncoder.getDistance());	
		
		SmartDashboard.putNumber("Ladder Pos", ladder.getPos());
		
		/**
		 * topLimit = new DigitalInput(9);
//		frameBottomLimit = new DigitalInput(8);
		armBottomLimit = new DigitalInput(7);
		clawOpeningLimit = new DigitalInput(8);
		clawClosingLimit = new DigitalInput(6); //was 9, changing for testing.
		 */
		SmartDashboard.putBoolean("9", clawClosingLimit.get());
		SmartDashboard.putBoolean("8", clawOpeningLimit.get());
		SmartDashboard.putBoolean("6", topLimit.get());
		
		
	}
}