
public class Agent {
	int x;
	int y;
	int health;
	int bombRange;
	int trapCount;
	int lastAction;
	int stepCount;
	int healthUpgradeCount;
	public Agent(int x,int y,int health,int bombRange,int trapCount){
		this.x=x;
		this.y=y;
		this.health=health;
		this.bombRange=bombRange;
		this.trapCount=trapCount;
		this.healthUpgradeCount=0;
	}
	public String toString() {
		return "{\"x\"="+x+", \"y\"="+y+", \"health\"="+health+", \"bombRange\"="+bombRange+", \"trapCount\"="+trapCount+" }";
	}
	public void update(int stepCount,int lastAction,int x,int y,int health,int healthUpgradeCount,int bombRange,int trapCount ) {
		this.x=x;
		this.y=y;
		this.health=health;
		this.bombRange=bombRange;
		this.trapCount=trapCount;
		this.healthUpgradeCount=healthUpgradeCount;
	}
	
}
