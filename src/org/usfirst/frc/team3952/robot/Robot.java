package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import java.util.*;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;


/**
 * rear left 0
 * rear right 1
 * front right 2
 * front left 3
 * 
 */
public class Robot extends IterativeRobot {
	
	private Controller controller;

	private MecanumDrive drive;
	private Task currentTask, backgroundTask;
	private Encoder rightEncoder, leftEncoder, ladderEncoder;
	private ADXRS450_Gyro gyro;
	private Ladder ladder;
	
	private Talon frontLeft, frontRight, rearLeft, rearRight;
	private Talon ladderT, coiler, claw;
	
	private Queue<Task> autonomousQueue;
	private SendableChooser<String> autonomousChooser;
	
	@Override
	public void robotInit() {
		controller = new Controller();
		
		// init Talons
		frontLeft = new Talon(3);
		rearLeft = new Talon(0);
		frontRight = new Talon(2);
		rearRight = new Talon(1);
		frontRight.setInverted(true);
		rearRight.setInverted(true);
		
		ladderT = new Talon(-1);		// set to reasonable value
		coiler = new Talon(-1);
		claw = new Talon(-1);
		
		//init drive train
		drive = new MecanumDrive(frontLeft,
								 rearLeft, 
								 frontRight, 
								 rearRight);
								 
		backgroundTask = new NullTask();
		rightEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k4X); //we can also try k4 for more accuracy.
		//rightEncoder.setDistancePerPulse(1.23 * 3/500.0);
		rightEncoder.setDistancePerPulse(1.26 * 0.011747);
		//20 pulses per rotation
		leftEncoder = new Encoder(1, 0, false, Encoder.EncodingType.k2X);
		leftEncoder.setDistancePerPulse(1.26 * 0.011747);//1.23 * 3/500.0);
		ladderEncoder = new Encoder(parameter, parameter, parameter, parameter);	// idk what parameters go here
		
		ladder = new Ladder(ladderT, coiler, claw, ladderEncoder);
		
		gyro = new ADXRS450_Gyro();
		autonomousQueue = new LinkedList<>();
		currentTask = new TeleopTask(this);
		//gyro.calibrate(); is this necessary? no
		
		autonomousChooser = new SendableChooser<>();
		autonomousChooser.addObject("Starting Left", "L");
		autonomousChooser.addObject("Starting Middle", "R");
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
		
		
		if(controller.cancelTask()) {
			currentTask.cancel();
			currentTask = new TeleopTask(this);
		}
		if(controller.cancelBackgroundTask()) {
			backgroundTask.cancel();
			//NOTE: Null Task never returns true!!!
			backgroundTask = new NullTask();
		}
		
		
		boolean done = currentTask.run();
		//try {Thread.sleep(2000);} catch(InterruptedException ignored) {}
		if(done){
			currentTask = new TeleopTask(this);
		}
		
		
		//IMPORTANT NOTE: Null task.run never returns true.
		boolean backDone = backgroundTask.run();
		if(backDone){
			backgroundTask = new NullTask();
		}
		
		
		
		SmartDashboard.putString("Current Task", currentTask.toString());
		SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
		SmartDashboard.putString("Right Encoder teleop: ", "" + rightEncoder.getDistance());
		SmartDashboard.putString("Left Encoder teleop", ""+ leftEncoder.getDistance());
		
		SmartDashboard.putString("Front Left", "" + frontLeft.get());
		SmartDashboard.putString("Front Right", "" + frontRight.get());
		SmartDashboard.putString("Rear Left", "" + rearLeft.get());
		SmartDashboard.putString("Rear Right", "" + rearRight.get());
	}
	
	
	//=====================================AUTONOMOUS======================================//
	
	
	@Override
	public void autonomousInit(){
		String stuff = DriverStation.getInstance().getGameSpecificMessage(); //ex: LRL
		String ourSwitchPos = stuff.substring(0, 1);
		String scalePos = stuff.substring(1, 2);
		String ourPosition = autonomousChooser.getSelected(); //L, R, M
		
		
		//need to adapt chris's code
		//setting up queue
		//autonomousQueue.add(new MoveForwardTask(this, 2));
		//autonomousQueue.add(new TurnTask(this, 90));
		//autonomousQueue.add(new TurnTask(this, -90));
		//autonomousQueue.add(new TurnTask(this, 360));
		//autonomousQueue.add(
		//      new MultiTask(new TurnTask(this, 90), new LadderUpTask(this, 4)
		//);
		// first 5.58
		//second 4.5
		// third 5.8
		// fourth 5.7
		autonomousQueue.add(new MoveForwardTask(this, 3));
	}
	
	@Override
	public void autonomousPeriodic(){
		if(!autonomousQueue.isEmpty()){
			if(autonomousQueue.peek().run()){
				autonomousQueue.poll();
			}
			SmartDashboard.putString("Current Task", currentTask.toString());
			SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
			SmartDashboard.putString("Encoders right autonomous", "" + rightEncoder.getDistance());
			SmartDashboard.putString("Encoders left autonomous", "" + leftEncoder.getDistance());
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