package org.usfirst.frc.team3952.robot;

public class OpenClawTask extends Task {
	private Ladder ladder;
	private boolean init = true;
	private long startTime;
	private Robot r;
	
	public OpenClawTask(Robot robot){
		ladder = robot.getLadder();
		r = robot;
	}
	
	@Override
	public boolean run() {
		if(init) {
			startTime = System.currentTimeMillis();
			init = false;
		}
		ladder.openClawUnsafe();
		if(System.currentTimeMillis() - startTime >= 1000) {
			ladder.stopClaw();
			return true;
		}
		return false;
	}

	@Override
	public void cancel() {
		//lol nothing
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Open Claw Task";
	}

}
