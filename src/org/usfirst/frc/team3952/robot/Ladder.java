	package org.usfirst.frc.team3952.robot;

import edu.wpi.first.wpilibj.*;

public class Ladder {
	private Talon ladder, coiler, claw;
	private Encoder encoder;
	
	public Ladder(Talon ladder, Talon coiler, Talon claw, Encoder encoder) {
		this.ladder = ladder;
		this.coiler = coiler;
		this.claw = claw;
		this.encoder = encoder;
	}
	
	public boolean extendLadder() { 	// TODO
		boolean ladderIsExtendedAllTheWay = true;		// get this probably with the switch thing
		boolean clawIsExtendedAllTheWay = true;			// encoder?
		if(ladderIsExtendedAllTheWay) {
			ladder.set(0);
			coiler.set(0);
		} else {
			if(clawIsExtendedAllTheWay) {
				ladder.set(1);				// more power & set to a reasonable value
			} else {
				ladder.set(0.5);			// less power & set to a reasonable value
			}
			coiler.set(0.5);
		}
		return ladderIsExtendedAllTheWay;
	}
	
	public boolean retractLadder() {
		boolean ladderIsRetractedAllTheWay = true;		// get this probably with the switch thing
		if(ladderIsRetractedAllTheWay) {
			ladder.set(0);
			coiler.set(0);
		} else {
			ladder.set(-0.5);				// set to a reasonable value
			coiler.set(-0.5);				// set to a reasonable value
		}
		return ladderIsRetractedAllTheWay;
	}
	
	public boolean openClaw() {
		boolean clawIsOpenedAllTheWay = true;		// ???? how do you even kno if le claw es opnd all de wey
		if(clawIsOpenedAllTheWay) {
			claw.set(0);				// set to a reasonable value
		} else {
			claw.set(-1);
		}
		return clawIsOpenedAllTheWay;
	}
	
	public boolean closeClaw() {
		boolean clawIsClosedAllTheWay = true;		// ???? how do you even kno if le claw es closd all de wey
		if(clawIsClosedAllTheWay) {
			claw.set(0);
		} else {
			claw.set(1);				// set to a reasonable value
		}
		return clawIsClosedAllTheWay;
	}
}