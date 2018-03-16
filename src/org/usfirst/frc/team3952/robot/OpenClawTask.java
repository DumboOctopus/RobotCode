package org.usfirst.frc.team3952.robot;

public class ChangeClawTask extends Task {

	private boolean open;
	private Ladder ladder;
	private Robot r;
	
	public ChangeClawTask(Robot robot, boolean open){
		ladder = robot.getLadder();
		this.open = open;
		r = robot;
	}
	
	@Override
	public boolean run() {
		if(open){ //open
			ladder.openClaw();
		} else { //close
			ladder.closeClaw();
		}
		return ladder.clawIsOpenedAllTheWayOrIsClosedAllTheWay() == open && !(r.getClawWillOpen() ^ ladder.clawIsOpenedAllTheWayOrIsClosedAllTheWay());
	}

	@Override
	public void cancel() {
		//lol nothing
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Change Claw Task " + (open ? "Opening" : "Closing");
	}

}
