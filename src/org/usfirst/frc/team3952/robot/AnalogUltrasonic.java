package org.usfirst.frc.team3952.robot;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//=== Not Used ===\\

public class AnalogUltrasonic {
	private AnalogInput sensor; //the actual sensor
	private CircularQueue cq; //stores past 10 values.
	//units cm/what ever sensor gives
	private static final double EMF_TO_CM = 3.5; //Determined Experimentally CHANGE IF HAS ISSUES
	
	
	public AnalogUltrasonic(int pin){
		sensor = new AnalogInput(pin);
		cq = new CircularQueue(10);
		Thread t = new UpdateSensorDataThread();
		t.start();
	}
	
	
	public double getDistance(){
		return cq.average();
	}
	
	private class UpdateSensorDataThread extends Thread{
		
		@Override
		public void run(){
			while(true){
				try{
					cq.add(EMF_TO_CM* sensor.getVoltage());
					Thread.sleep(10); //update every .1 seconds
				} catch(InterruptedException e){
					System.out.println("Update Ultrasonic Sensor Thread died");
				}
			}
			
		}
	
	}
	
	private class CircularQueue{
		private double[] values;
		private int index = 0;
		private boolean filled = false;
		
		public CircularQueue(int size){
			values = new double[size];
		}
		
		public synchronized void add(double value){
			
			values[index] = value;
			index++;
			if(index >= values.length){
				filled = true;
				index %= values.length;
			}
		}
		
		public synchronized double average(){
			double av = 0.0;
			int max = (filled? values.length:index);
			
			for(int i = 0; i < max; i++){
				av += values[i];
			}
			return av/max;
		}
		
		public synchronized String toString(){
			String out = "";
			for(double elem: values){
				out += elem + " ";
			}
			return out;
		}

	}	
}