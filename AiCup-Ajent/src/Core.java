import java.util.Scanner;

public class Core {
	private static Game g;
	public static void main(String[] args) {
		Scanner sysin=new Scanner(System.in);
		if(sysin.next().equals("init")) {
			g=new Game(sysin.nextInt(), sysin.nextInt(), new Agent(sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt()), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt());
			System.err.println(g);
		}
		else {
			return;
		}
		while(true) {
			
		}
	}
}
