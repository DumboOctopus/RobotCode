package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;
import java.util.*;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class Robot extends IterativeRobot {
	
	private Controller controller;
	private MecanumDrive drive;
	private Task currentTask, backgroundTask;
	private Encoder rightEncoder, leftEncoder;
	private ADXRS450_Gyro gyro;
	
	private Queue<Task> autonomousQueue;
	
	
	@Override
	public void robotInit() {
		controller = new Controller();
		drive = new MecanumDrive(new Talon(0),			// Set these to correct pins(front left)
								 new Talon(0), 			// rear left
								 new Talon(0), 			// front right
								 new Talon(0));			// rear right
		currentTask = new TeleopTask(this);
		backgroundTask = new NullTask();
		rightEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k2X); //we can also try k4 for more accuracy.
		//20 pulses per rotation
		leftEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k2X);
		gyro = new ADXRS450_Gyro();
		autonomousQueue = new LinkedList<>();
		//gyro.calibrate();
		
	}
	
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
			//NOTE: Null Task never return true!!!
			backgroundTask = new NullTask();
		}
		
		
		boolean done = currentTask.run();
		if(done){
			currentTask = new TeleopTask(this);
		}
		
		
		/**IMPORTANT NOTE: Null task.run never returns true.**/
		boolean backDone = backgroundTask.run();
		if(backDone){
			backgroundTask = new NullTask();
		}
		
		
		SmartDashboard.putString("Current Task", currentTask.toString());
		//SmartDashboard.putString("Gyro: ", "" + gyro.getAngle());
		//SmartDashboard.putString("Encoders right: ", "" + rightEncoder.getDistance());
		//SmartDashboard.putString("Encoders left: ", "" + leftEncoder.getDistance());
	}
	
	@Override
	public void autonomousInit(){
		String stuff = DriverStation.getInstance().getGameSpecificMessage(); //ex: LRL
		String ourSwitchPos = stuff.substring(0, 1);
		String scalePos = stuff.substring(1, 2);
		String ourPosition = "L"; //"L", "R"
		
		
		//setting up queue
		autonomousQueue.add(new MoveForwardTask(this, 2));
		//autonomousQueue.add(new TurnTask(this, 90));
		//autonomousQueue.add(new TurnTask(this, -90));
		//autonomousQueue.add(new TurnTask(this, 57));
		//autonomousQueue.add(new TurnTask(this, 90));
	}
	
	@Override
	public void autonomousPeriodic(){
		if(!autonomousQueue.isEmpty()){
			if(autonomousQueue.peek().run()){
				autonomousQueue.poll();
			}
		}
		
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