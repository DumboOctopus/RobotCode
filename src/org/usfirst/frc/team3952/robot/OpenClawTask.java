package org.usfirst.frc.team3952.robot;

public class OpenClawTask extends Task {
	private Ladder ladder;
	private Robot r;
	
	public OpenClawTask(Robot robot){
		ladder = robot.getLadder();
		r = robot;
	}
	
	@Override
	public boolean run() {
		ladder.openClaw();
		return ladder.clawIsOpenedAllTheWayOrIsClosedAllTheWay();
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
