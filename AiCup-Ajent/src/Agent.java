
public class Agent {
	int x;
	int y;
	int health;
	int bomb_range;
	int trap_c;
	public Agent(int x,int y,int health,int bomb_range,int trap_c){
		this.x=x;
		this.y=y;
		this.health=health;
		this.bomb_range=bomb_range;
		this.trap_c=trap_c;
	
	}
	public String toString() {
		return "{\"x\"="+x+", \"y\"="+y+", \"health\"="+health+", \"bomb_range\"="+bomb_range+", \"trap_c\"="+trap_c+" }";
	}
	
}
