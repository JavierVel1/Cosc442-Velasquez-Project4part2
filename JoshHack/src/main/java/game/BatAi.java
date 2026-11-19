package game;

import java.util.Random;

public class BatAi extends CreatureAi {

	public BatAi(Creature creature) {
		super(creature);
	}

	public BatAi(Creature creature, Random random) {
		super(creature, random);
	}

	public void onUpdate(){
		wander();
		wander();
	}
}
