package org.usfirst.frc.team3952.robot;

public class GraduallyGoTO {
	
	private double value;
	private double changePerMillis;
	
	private long lastMillis;
	
	public GraduallyGoTO(double value, double changePerMillis) {
		this.value = value;
		this.changePerMillis = changePerMillis;
		lastMillis = System.currentTimeMillis();
	}

	public double goTo(double newVal){
		long nextMillis = System.currentTimeMillis();
		if( newVal > value){
			value += changePerMillis*(nextMillis - lastMillis);
			if(value > newVal)
				value = newVal;
		} else if(newVal < value){
			value -= changePerMillis*(nextMillis - lastMillis);
			if(value < newVal)
				value = newVal;
		}
		lastMillis = nextMillis;
		return value;
	}
	
	

}
