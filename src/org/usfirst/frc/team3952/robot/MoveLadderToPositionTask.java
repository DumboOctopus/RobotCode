package org.usfirst.frc.team3952.robot;

public class MoveLadderToPositionTask extends Task {
	
	private Ladder ladder;
	private int pos;	// direction, destination position
	
	public MoveLadderToPositionTask(Robot robot, int dir) {
		ladder = robot.getLadder();
		this.pos = ladder.getPos() + dir;
	}
	
	@Override
	public boolean run() {
		return ladder.setPos(pos);
	}

	@Override
	public void cancel() {}

	@Override
	public String toString() {
		return "Move Ladder To Position Task(" + (dir > 0 ? "up)" : "down)");
	}

}
