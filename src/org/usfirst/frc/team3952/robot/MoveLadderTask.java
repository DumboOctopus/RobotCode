package org.usfirst.frc.team3952.robot;

public class MoveLadderTask extends Task {
	
	private Ladder ladder;
	private int pos;	// destination position
	
	public MoveLadderTask(Robot robot, int dir) {
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
		return "Move Ladder To Position Task(Position " + (pos + 1) + ")";
	}

}
