package org.usfirst.frc.team3952.robot;

public class CircularDeadzoneController extends Controller {
	public CircularDeadzoneController() {
		super();
	}
	
	public double getHorizontalMovement() {
		double x = joystick.getX();
		double y = -joystick.getY();
		return x * x + y * y >= deadZonex * deadZonex ? 
			   kx * Math.signum(x) * (Math.log(Math.abs(x) + 1 - Math.sqrt(deadZonex * deadZonex - y * y)) + cx)	// TODO: k?
			   :
			   0;
	}
	
	// joystick.getY() appears to be inverted, thus a negative sign is applied to the raw value
	public double getLateralMovement() {
		double x = joystick.getX();
		double y = -joystick.getY();
		return x * x + y * y >= deadZone * deadZone ? 
			   k * Math.signum(y) * (Math.log(Math.abs(y) + 1 - Math.sqrt(deadZone * deadZone - x * x)) + c)
			   :
			   0;
	}
}
