package org.usfirst.frc.team3952.robot;

public class MultiTask extends Task {

	private Task t1, t2;
	
	public MultiTask(Task t1, Task t2){
		this.t1 = t1;
		this.t2 = t2;
		
	}
	
	public void setPrimaryTask(Task t1) {
		this.t1 = t1;
	}
	
	public void setSecondaryTask(Task t2) {
		if(this.t2 != null) this.t2.cancel();
		this.t2 = t2;
	}
	
	public void cancelPrimaryTask() {
		if(t1 != null) t1.cancel();
		t1 = null;
	}
	
	public void cancelSecondaryTask() {
		if(t2 != null) t2.cancel();
		t2 = null;
	}
	
	@Override
	public boolean run() {
		if(t1 != null && t1.run()) t1 = null;
		if(t2 != null && t2.run()) t2 = null;
		
		return t1 == null && t2 == null;
		// The Great One-Lining Magic
		// return (t1 = (t1 != null && t1.run()) ? null : t1) == null && (t2 = (t2 != null && t2.run()) ? null : t2) == null;
	}
	

	@Override
	public void cancel() {
		if(t1 != null)
		t1.cancel();
		if(t2 != null)
		t2.cancel();
	}

	@Override
	public String toString() {
		return "MultiTask: " + t1 + ", " + t2;
	}

}
