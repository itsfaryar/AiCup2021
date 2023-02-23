import java.util.Scanner;

public class Core {
	private static Game g;

	public static void main(String[] args) {
		Scanner sysin=new Scanner(System.in);
		if(sysin.next().equals("init")) {
			g=new Game(sysin.nextInt(), sysin.nextInt(), new Agent(sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt()), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt());
			System.err.println(g);
			System.out.println("init confirm");
		}
		else {
			return;
		}
		while(true) {
			if(!sysin.hasNextInt()) {
				if(sysin.next().equals("term")) {
					int lastStepC=sysin.nextInt();
					int res=sysin.nextInt();
					System.err.println("resualt: "+lastStepC+" "+res);
					break;
				}
			}
			g.pl.update(sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt(), sysin.nextInt());
			if(sysin.nextInt()==1) {
				g.updateOtherPlayer(sysin.nextInt(), sysin.nextInt(), sysin.nextInt());
			}
			else {
				g.StepsFromlastUpdateOtherPlayer++;
			}
			int n=sysin.nextInt();
			for(int i=0;i<n;i++) {
				g.updateTile(sysin.nextInt(), sysin.nextInt(), sysin.nextInt());
			}
			if(sysin.next().equals("EOM")) {
				System.err.println("====================");
				g.showMap();
				System.err.println("====================");
				System.out.println(g.doAnAction());
			}
			
		}
		
		
	}
}
