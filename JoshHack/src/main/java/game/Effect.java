package game;

public class Effect implements Cloneable {
	protected int duration;
	
	public boolean isDone() { return duration < 1; }

	public Effect(int duration){
		this.duration = duration;
	}
	
	public Effect(Effect other){
		this.duration = other.duration; 
	}

	public Effect copy() {
		try {
			return (Effect) super.clone();
		} catch (CloneNotSupportedException e) {
			return new Effect(this);
		}
	}
	
	public void update(Creature creature){
		duration--;
	}
	
	public void start(Creature creature){
		
	}
	
	public void end(Creature creature){
		
	}
}
