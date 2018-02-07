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
		rightEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k2X); //we can also try k4 for more accuracy.
		rightEncoder.setDistancePerPulse(0.0078);
		//rightEncoder.setDistancePerPulse(1.23 * 3/500.0);
		//rightEncoder.setDistancePerPulse(1.26 * 0.011747);
		//20 pulses per rotation
		leftEncoder = new Encoder(1, 0, false, Encoder.EncodingType.k2X);
		leftEncoder.setDistancePerPulse(0.00677);
		//leftEncoder.setDistancePerPulse(1.26 * 0.011747);//1.23 * 3/500.0);
		
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
		if(!ladder.movingLadder) {
			ladder.disablePID();
		}
		ladder.movingLadder = false;
		
		if(controller.cancelTask()) {
			currentTask.cancel();
			currentTask = new MultiTask(new TeleopTask(this), null);
		}
		
		if(currentTask.run()){
			currentTask = new MultiTask(new TeleopTask(this), null);
		}
		
		
		
		SmartDashboard.putString("Current Task", currentTask.toString());
		SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
		SmartDashboard.putString("Gyro Rate", "" + gyro.getRate());
		SmartDashboard.putString("Encoders right", "" + rightEncoder.getDistance());
		SmartDashboard.putString("Encoders left", ""+ leftEncoder.getDistance());
		
		SmartDashboard.putString("Front Left", "" + frontLeft.get());
		SmartDashboard.putString("Front Right", "" + frontRight.get());
		SmartDashboard.putString("Rear Left", "" + rearLeft.get());
		SmartDashboard.putString("Rear Right", "" + rearRight.get());
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