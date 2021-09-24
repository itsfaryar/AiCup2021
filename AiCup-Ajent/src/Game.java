
public class Game {
	int h;
	int w;
	Agent pl1;
	Agent pl2;
	int vision;
	int bombDelay;
	int maxBombRange;
	int deadzoneStartingStep;
	int deadzoneExpansionDelay;
	int maxStep;
	
	public Game(int h,int w,Agent pl,int vision,int bombDelay,int maxBombRange,int deadzoneStartingStep,int deadzoneExpansionDelay,int maxStep) {
		this.h=h;
		this.w=w;
		this.pl1=pl;
		this.vision=vision;
		this.bombDelay=bombDelay;
		this.maxBombRange=maxBombRange;
		this.deadzoneStartingStep=deadzoneExpansionDelay;
		this.deadzoneExpansionDelay=deadzoneExpansionDelay;
		this.maxStep=maxStep;
		this.pl2=new Agent(-1, -1, -1, -1, -1);
	}
	public String toString() {
		return "{\"h\"="+h+",\n\"w\"="+w+",\n\"visoin\"="+vision+",\n\"bombDelay\"="+bombDelay+",\n\"maxBombRange\"="+maxBombRange+" "+",\n\"deadzoneStartingStep\"="+deadzoneStartingStep+",\n\"deadzoneExpansionDelay\"="+deadzoneExpansionDelay+",\n\"maxStep\"="+maxStep+",\n\"pl1\"="+pl1+",\n\"pl2\"="+pl2+"}";
	}
	
}
