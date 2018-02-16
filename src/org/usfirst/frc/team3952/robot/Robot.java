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
 * 
 * TODO: 
 * 	See Controller
 * 	See Ladder
 * 	Clean up Garbage in Robot.
 * 
 * 
 */
public class Robot extends IterativeRobot {
	
	private Controller controller;

	private MecanumDrive drive;
	private Ladder ladder;
	
	private Task currentTask;
	private Encoder rightEncoder, leftEncoder, ladderEncoder;
	private ADXRS450_Gyro gyro;
	//private Ladder ladder;
	
	private Talon frontLeft, frontRight, rearLeft, rearRight;
	private Talon ladderT, coiler, claw;
	
	private Queue<Task> autonomousQueue;
	private SendableChooser<String> autonomousChooser;
	
	private DigitalInput topLimit, frameBottomLimit, armBottomLimit;
	
	
	@Override
	public void robotInit() {
		controller = new Controller();
		
		frontLeft = new Talon(1);
		frontRight = new Talon(0);
		rearLeft = new Talon(3);
		rearRight = new Talon(2);
		frontRight.setInverted(true);
		rearRight.setInverted(true);
		ladderT = new Talon(6);		
		coiler = new Talon(5);    
		claw = new Talon(4);
		
		//init drive train
		drive = new MecanumDrive(frontLeft,
								 rearLeft, 
								 frontRight, 
								 rearRight);
							
		//Init encoders
		rightEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k2X); //we can also try k4 for more accuracy.
		rightEncoder.setDistancePerPulse(0.0078);
				
		leftEncoder = new Encoder(1, 0, false, Encoder.EncodingType.k2X);
		leftEncoder.setDistancePerPulse(0.00677);
		
		ladderEncoder = new Encoder(4, 5, false, Encoder.EncodingType.k2X);
		ladderEncoder.setDistancePerPulse(1); //WE ARE NOT GOING TO CALIBRATE KEKEKEK.
		
		ladder = new Ladder(ladderT, coiler, claw, ladderEncoder, topLimit, frameBottomLimit, armBottomLimit);
		
		gyro = new ADXRS450_Gyro();
		autonomousQueue = new LinkedList<>();
		currentTask = new TeleopTask(this);
		//gyro.calibrate(); is this necessary? no
		
		
		//smart dashboard selecting autonomous.
		autonomousChooser = new SendableChooser<>();
		autonomousChooser.addObject("Starting Left", "L");
		autonomousChooser.addObject("Starting Middle", "M");
		autonomousChooser.addDefault("Starting Right", "R");
		SmartDashboard.putData("Autonomous Initial Position", autonomousChooser);
	}
	
	
	//=====================================DISABLED=================================//
	
	@Override
	public void disabledInit() {}

	//=================================TELOP=====================================///

	@Override
	public void teleopInit() {
	}
	
	@Override
	public void teleopPeriodic() {
		
		if(controller.extendLadder()){
			ladder.extendLadder();
		} else if(controller.retractLadder()){
			ladder.retractLadder();
		} else{
			ladder.pulseIfNotMoving(); //so if we don't want it move, it won't
		}
		
		//Tasks Canceling
		if(controller.cancelTask()) {
			currentTask.cancel();
			currentTask = new MultiTask(new TeleopTask(this), null);
		}
		
		
		//Task running
		if(currentTask.run()){
			currentTask = new MultiTask(new TeleopTask(this), null);
		}
		
		
		//Smart Dashboard
		SmartDashboard.putString("Current Task", currentTask.toString());
		SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
		SmartDashboard.putString("Gyro Rate", "" + gyro.getRate());
		SmartDashboard.putString("Encoders right", "" + rightEncoder.getDistance());
		SmartDashboard.putString("Encoders left", ""+ leftEncoder.getDistance());
		SmartDashboard.putString("Front Left", "" + frontLeft.get());
		SmartDashboard.putString("Front Right", "" + frontRight.get());
		SmartDashboard.putString("Rear Left", "" + rearLeft.get());
		SmartDashboard.putString("Rear Right", "" + rearRight.get());
		
		SmartDashboard.putString("Ladder Encoder", "" + ladderEncoder.getDistance());	
	}
	
	
	//=====================================AUTONOMOUS======================================//
	
	/**
	 * This will run every time you press enable in auto
	 */
	@Override
	public void autonomousInit(){
		String stuff = DriverStation.getInstance().getGameSpecificMessage(); //ex: LRL
		String ourSwitchPos = stuff.substring(0, 1);
		String scalePos = stuff.substring(1, 2);
		String ourPosition = autonomousChooser.getSelected(); //L, R, M
		
		
		//need to adapt chris's code
		//setting up queue
		autonomousQueue.add(new MoveForwardTask(this, 2));
		//autonomousQueue.add(new TurnTask(this, 90));
		//autonomousQueue.add(new TurnTask(this, -90));
		//autonomousQueue.add(new TurnTask(this, 360));
		//autonomousQueue.add(
		//      new MultiTask(new TurnTask(this, 90), new LadderUpTask(this, 4)
		//);
		//autonomousQueue.add(new TurnTask(this, 90));
		
	}
	
	@Override
	public void autonomousPeriodic(){
		SmartDashboard.putString("Autonomous Queue", autonomousQueue.toString());
		if(!autonomousQueue.isEmpty()){
			if(autonomousQueue.peek().run()){
				autonomousQueue.poll();
			}
			SmartDashboard.putString("Current Task", currentTask.toString());
			SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
			SmartDashboard.putString("Encoders right", "" + rightEncoder.getDistance());
			SmartDashboard.putString("Encoders left", "" + leftEncoder.getDistance());
		}
		
	}
	
	//==============================TEST=============================//
	@Override
	public void testPeriodic(){
		teleopPeriodic();
	}
	
	
	//=============================GETTERS=============================//
	
	public Controller getController() {
		return controller;
	}
	
	public MecanumDrive getDrive() {
		return drive;
	}
	
	public Encoder getRightEncoder(){
		return rightEncoder;
	}
	
	public Encoder getLeftEncoder(){
		return leftEncoder;
	}
	
	public ADXRS450_Gyro getGyro(){
		return gyro;
	}
}