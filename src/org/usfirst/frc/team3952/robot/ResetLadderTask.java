package org.usfirst.frc.team3952.robot;

public class ResetLadderTask extends Task {
	private Ladder ladder;
	
	public ResetLadderTask(Robot robot) {
		ladder = robot.getLadder();
	}
	
	@Override
	public boolean run() {
		if(!ladder.atBottom()) ladder.retractLadder();
		return ladder.atBottom();
	}

	@Override
	public void cancel() {
		ladder.stopLadder();
	}

	@Override
	public String toString() {
		return "Reset Ladder Task";
	}
}
