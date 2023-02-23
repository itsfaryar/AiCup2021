import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;



public class Game {
	public static final int STATE_UNDEF = -1;
	public static final int STATE_ZONE = 0;
	public static final int STATE_DEADZONE = 1;
	public static final int STATE_FIRE = 2;
	public static final int STATE_BOX = 4;
	
	public static final int STATE_WALL = 8;
	public static final int STATE_WALL_IN_DEADZONE = 9;
	public static final int STATE_BOMB = 16;
	public static final int STATE_BOMB_RANGE_UP = 32;
	public static final int STATE_HEALTH_UP = 64;
	public static final int STATE_TRAP_UP = 128;
	public static final int STATE_PLAYER = 256;
	
	public static final int STATE_PLAYER_IN_DEADZONE = 257;
	public static final int STATE_PLAYERWITHBOMB = 272;
	
	public static final int  ACTION_GO_LEFT=0;
	public static final int  ACTION_GO_RIGHT=1;
	public static final int  ACTION_GO_UP=2;
	public static final int  ACTION_GO_DOWN=3;
	public static final int  ACTION_STAY=4;
	public static final int  ACTION_PLACE_BOMB=5;
	public static final int  ACTION_TRAP_LEFT=6;
	public static final int  ACTION_TRAP_RIGHT=7;
	public static final int  ACTION_TRAP_UP=8;
	public static final int  ACTION_TRAP_DOWN=9;
	public static final int  ACTION_INIT=10;
	public static final int  ACTION_NO_ACTION=11;
	
	public int h;
	public int w;
	public Agent pl;
	public int vision;
	public int bombDelay;
	public double bombDelayPerStep;
	public int maxBombRange;
	public int deadzoneStartingStep;
	public int deadzoneExpansionDelay;
	public int maxStep;
	public int map[][];
	ArrayList<ArrayList<Integer>> adj;
	ArrayList<Position>boxes;
	ArrayList<Position>items;
	public int otherPl_x;
	public int otherPl_y;
	public int otherPl_health;
	public int StepsFromlastUpdateOtherPlayer=0;
	public int v;
	public int bomb_ticker=0;
	public enum Status{SEARCHING,GO_FOR_BOX,RUN_FROM_BOMB,Go_FOR_ITEM,PLACE_BOMB,GOTO_CENTER}
	public Status state=Status.SEARCHING;
	
	public Position next_box_pos;
	public Position next_box_near_pos;
	public Position nearToCenter;
	public boolean is_pathToCenter=false;
	public boolean gotocenter=true;
	public boolean goingToCenter=false;
	int index_path_Cent;
	LinkedList<Integer> pathToCenter= new LinkedList<Integer>();;
	public class Position {
		int x;
		int y;
		public Position(int x,int y) {
			this.x=x;
			this.y=y;
		}
		public Position() {
		
		}
		public boolean equals(Position p) {
			if(p.x==x && p.y==y)return true;
			else return false;
		}
		public String toString() {
			return "("+x+","+y+")";
		}
	};

	private int getdist(Position p,int s_x,int s_y) {
		return (int) Math.pow((s_x-p.x),2) + (int) Math.pow((s_y-p.y),2);
	}
	
	private int getdist(int x,int y) {
		return (int) Math.pow((pl.x-x),2) + (int) Math.pow((pl.y-y),2);
	}
	public class Sortbyroll implements Comparator<Position>
	{
	    // Used for sorting in ascending order of
	    // roll number
	    public int compare(Position p1,Position p2)
	    {
	        int p1_d=getdist(p1,pl.x,pl.y);
	        int p2_d=getdist(p2,pl.x,pl.y);
	        if(p1_d==p2_d) {
	        	return 0;
	        }
	        else  if(p1_d>p2_d) {
	        	return 1;
	        }
	        else {
	        	return -1;
	        }
	    }
	};
	public Game(int h,int w,Agent pl,int vision,int bombDelay,int maxBombRange,int deadzoneStartingStep,int deadzoneExpansionDelay,int maxStep) {
		this.h=h;
		this.w=w;
		this.pl=pl;
		this.vision=vision;
		this.bombDelay=bombDelay;
		this.maxBombRange=maxBombRange;
		this.deadzoneStartingStep=deadzoneExpansionDelay;
		this.deadzoneExpansionDelay=deadzoneExpansionDelay;
		this.maxStep=maxStep;
		this.otherPl_x=-1;
		this.otherPl_y=-1;
		this.otherPl_health=pl.health;
		map=new int[h][w];
		v=w*h;
		adj= new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<h;i++) {
			
			for(int j=0;j<w;j++) {
				adj.add(new ArrayList<Integer>());
				map[i][j]=STATE_UNDEF;
			}
		}
		boxes=new ArrayList<Position>();
		items=new ArrayList<Position>();
		bombDelayPerStep=Math.ceil((double)( bombDelay)/2);
	}
	public String toString() {
		return "{\"h\"="+h+",\n\"w\"="+w+",\n\"visoin\"="+vision+",\n\"bombDelay\"="+bombDelay+",\n\"maxBombRange\"="+maxBombRange+" "+",\n\"deadzoneStartingStep\"="+deadzoneStartingStep+",\n\"deadzoneExpansionDelay\"="+deadzoneExpansionDelay+",\n\"maxStep\"="+maxStep+",\n\"pl1\"="+pl+"}";
	}
	
	public void updateOtherPlayer(int x,int y,int health) {
		this.otherPl_x=x;
		this.otherPl_y=y;
		this.otherPl_health=health;
		this.StepsFromlastUpdateOtherPlayer=0;
	}
	public int findNearestFromCenter() {
		Position p=new Position(h/2,w/2);
		Position t=new Position(p.x,p.y);
		boolean flag=true;
		boolean notFound=true;
		
		int i=0;
		while(notFound) {
			
				if(p.x-i>0) {
					if( map[p.x-i][p.y]==STATE_ZONE) {
				
						if(flag) {
							t.x=p.x-i;
							flag=false;
						}
						else if(getdist(p.x-i,p.y)<getdist(t,pl.x,pl.y)) {
							t.x=p.x-i;
							
						}
						notFound=false;
					}
				
				}
				if(p.x+i<h) {
					if(map[p.x+i][p.y]==STATE_ZONE) {
					if(flag) {
						t.x=p.x+i;
						flag=false;
					}
					else if(getdist(p.x+i,p.y)<getdist(t,pl.x,pl.y)) {
						t.x=p.x+i;
						
					}
					notFound=false;
					}
				
				}
				if(p.y-i>0) {
					if(map[p.x][p.y-i]==STATE_ZONE) {
					if(flag) {
						t.y=p.y-i;
						flag=false;
					}
					
					notFound=false;
				}
					
				}
				if(p.y+i<w ) {
					if(map[t.x][p.y+i]==STATE_ZONE) {
					if(flag) {
						t.y=p.y+i;
						flag=false;
					}
					else if(getdist(p.x,p.y+i)<getdist(t,pl.x,pl.y)) {
						t.y=p.y+i;
						
					}
					notFound=false;
					}
					
					}
				
				
				i++;
				}
				System.err.println("Nearest to center: "+t);
				nearToCenter=t;
				//return printShortestDistance(convertTo1D(pl.x, pl.y),convertTo1D(p.x,p.y));
				return printShortestDistance(convertTo1D(pl.x, pl.y),convertTo1D(nearToCenter.x,nearToCenter.y));
			
		
	}
	public int findBox( Position x)
    {
        for(int i=0;i<boxes.size();i++) {
        	if(boxes.get(i).equals(x)) {
        		return i;
        	}
        }
        return -1;
    }
	public int findItem( Position x)
    {
        for(int i=0;i<items.size();i++) {
        	if(items.get(i).equals(x)) {
        		return i;
        	}
        }
        return -1;
    }
	private void addEdge( int i, int j)
    {
	 	if(adj.get(i).indexOf(j)==-1) {
	        adj.get(i).add(j);
	        adj.get(j).add(i);
        
	 	}
    }
	private void deletetile( int pos)
    {
	 	for(int i=0;i<adj.get(pos).size();i++) {
	 		int t=adj.get(pos).get(i);
	 		int index=adj.get(t).indexOf(pos);
	 		adj.get(t).remove(index);
	 	}
	 	adj.get(pos).clear();
    }
	public void updateTilesAroundPlayer() {
		int x=pl.x;
		int y=pl.y;
		if(x-1>0 && (map[x-1][y]==STATE_ZONE || map[x-1][y]==STATE_TRAP_UP || map[x-1][y]==STATE_HEALTH_UP || map[x-1][y]==STATE_BOMB_RANGE_UP )) {
			addEdge(convertTo1D(x, y),convertTo1D(x-1, y));
		}
		else if(x+1<h && (map[x+1][y]==STATE_ZONE || map[x+1][y]==STATE_TRAP_UP || map[x+1][y]==STATE_HEALTH_UP || map[x+1][y]==STATE_BOMB_RANGE_UP )) {
			addEdge(convertTo1D(x, y),convertTo1D(x+1, y));
		}
		else if(y-1>0 && (map[x][y-1]==STATE_ZONE || map[x][y-1]==STATE_TRAP_UP || map[x][y-1]==STATE_HEALTH_UP || map[x][y-1]==STATE_BOMB_RANGE_UP )) {
			addEdge(convertTo1D(x, y),convertTo1D(x, y-1));
		}
		else if(y+1<w && (map[x][y+1]==STATE_ZONE || map[x][y+1]==STATE_TRAP_UP || map[x][y+1]==STATE_HEALTH_UP || map[x][y+1]==STATE_BOMB_RANGE_UP )) {
			addEdge(convertTo1D(x, y),convertTo1D(x, y+1));
		}
	}
	public void updateTile(int x,int y, int state) {


		if(state==STATE_BOX ){
			if(findBox(new Position(x, y))==-1) {
				boxes.add(new Position(x, y));
				Collections.sort(boxes, new Sortbyroll());
				for(int i=0;i<boxes.size();i++) {
					System.err.println("Box: "+i+" "+boxes.get(i)+" | ");
				}
			}
			
		}
		else if(state==STATE_TRAP_UP || state==STATE_HEALTH_UP || state==STATE_BOMB_RANGE_UP){
			if(findItem(new Position(x, y))==-1) {
				items.add(new Position(x, y));
				Collections.sort(boxes, new Sortbyroll());
				for(int i=0;i<items.size();i++) {
					System.err.println("item: "+i+" "+items.get(i)+" | ");
				}
			}
			
		}
		else if(state==STATE_DEADZONE ){
			deletetile(convertTo1D(x, y));
			
		}
		
		if(map[x][y]==STATE_BOX && state!=STATE_BOX) {
			System.err.println("this box is out"+x+","+y);
			int p_t=findBox(new Position(x,y));
			boxes.remove(p_t);
		}
			this.map[x][y]=state;
		if(state==STATE_ZONE || state==STATE_BOMB_RANGE_UP ||state==STATE_HEALTH_UP ||state==STATE_TRAP_UP ) {
			if(x-1>0 && (map[x-1][y]==STATE_ZONE || map[x-1][y]==STATE_TRAP_UP || map[x-1][y]==STATE_HEALTH_UP || map[x-1][y]==STATE_BOMB_RANGE_UP )) {
				addEdge(convertTo1D(x, y),convertTo1D(x-1, y));
			}
			if(x+1<h && (map[x+1][y]==STATE_ZONE || map[x+1][y]==STATE_TRAP_UP || map[x+1][y]==STATE_HEALTH_UP || map[x+1][y]==STATE_BOMB_RANGE_UP )) {
				addEdge(convertTo1D(x, y),convertTo1D(x+1, y));
			}
			if(y-1>0 && (map[x][y-1]==STATE_ZONE || map[x][y-1]==STATE_TRAP_UP || map[x][y-1]==STATE_HEALTH_UP || map[x][y-1]==STATE_BOMB_RANGE_UP )) {
				addEdge(convertTo1D(x, y),convertTo1D(x, y-1));
			}
			if(y+1<w && (map[x][y+1]==STATE_ZONE || map[x][y+1]==STATE_TRAP_UP || map[x][y+1]==STATE_HEALTH_UP || map[x][y+1]==STATE_BOMB_RANGE_UP )) {
				addEdge(convertTo1D(x, y),convertTo1D(x, y+1));
			}
		}
		
	}
	public void showMap() {
		for(int i=0;i<h;i++) {
			for(int j=0;j<w;j++) {
				System.err.print(map[i][j]+" ");
			}
			System.err.println();
		}
	}
	public int moveToTile(int x,int y) {
		int action=ACTION_STAY;
		if (pl.x == x && pl.y == y)
		{
			
			System.err.println( "stay");
			action=ACTION_STAY;
		}
		else if (pl.x > x && pl.y>y)
		{
			if(pl.y>0 && map[pl.x][pl.y-1]==STATE_ZONE) {
				action= ACTION_GO_LEFT;
			}
			else if(pl.x-1>0 && map[pl.x-1][pl.y]==STATE_ZONE) {
				action= ACTION_GO_UP;
				
			}
			
		}
		else if (pl.x > x && pl.y<y)
		{
			if(pl.y+1<w && map[pl.x][pl.y+1]==STATE_ZONE) {
				action= ACTION_GO_RIGHT;
			}
			else if(pl.x-1>0 && map[pl.x-1][pl.y]==STATE_ZONE) {
				action= ACTION_GO_UP;
				
			}
			
		}
		else if (pl.x < x && pl.y>y)
		{
			if(map[pl.x][pl.y-1]==STATE_ZONE) {
				action= ACTION_GO_LEFT;
			}
			else if(map[pl.x+1][pl.y]==STATE_ZONE) {
				action= ACTION_GO_DOWN;
				
			}
			
		}
		else if (pl.x < x && pl.y<y)
		{
			if(map[pl.x][pl.y+1]==STATE_ZONE) {
				action= ACTION_GO_RIGHT;
			}
			else if(map[pl.x+1][pl.y]==STATE_ZONE) {
				action= ACTION_GO_DOWN;
				
			}
			
		}
		else if (pl.x < x )
		{
			action=ACTION_GO_DOWN;
		}
		else if (pl.x > x )
		{
			action=ACTION_GO_UP;
		}
		else if (pl.y < y )
		{
			action=ACTION_GO_RIGHT;
		}
		else if (pl.y > y )
		{
			action=ACTION_GO_LEFT;
		}
		
		
		return action;
	}
	public boolean dontGoBack(int a) {
		if(pl.lastAction==ACTION_GO_DOWN && a==ACTION_GO_UP)return false;
		if(pl.lastAction==ACTION_GO_UP && a==ACTION_GO_DOWN)return false;
		if(pl.lastAction==ACTION_GO_LEFT && a==ACTION_GO_RIGHT)return false;
		if(pl.lastAction==ACTION_GO_RIGHT && a==ACTION_GO_LEFT)return false;
		
		return true;
	}
	public int playerJustMove() {
		
		int dist=Integer.MAX_VALUE;
		int action=ACTION_STAY;
		int pos1d=convertTo1D(pl.x, pl.y);
		for(int i=0;i<adj.get(pos1d).size();i++) {
			Position p_tmp=convertTo2D(adj.get(pos1d).get(i));
			int tmp=getdist(p_tmp,h/2,w/2);
			int a_tmp= moveToTile(p_tmp.x, p_tmp.y);
			
			if(tmp<dist && dontGoBack(action)) {
				action=a_tmp;
			}
		}
		return action;
	}
	public  void findPathToNearestBox(int i) {
		Position p=boxes.get(i);
		Position t=new Position(p.x,p.y);
		boolean flag=true;
		if(p.x-1>0 && map[p.x-1][p.y]==STATE_ZONE) {
			if(flag) {
				t.x=p.x-1;
				flag=false;
			}
			
		}
		if(p.x+1<h && map[p.x+1][p.y]==STATE_ZONE) {
			if(flag) {
				t.x=p.x+1;
				flag=false;
			}
			else if(getdist(p.x+1,p.y)<getdist(t,pl.x,pl.y)) {
				t.x=p.x+1;
				
			}
			
		}
		if(p.y-1>0 && map[p.x][p.y-1]==STATE_ZONE) {
			if(flag) {
				t.y=p.y-1;
				flag=false;
			}
			else if(getdist(p.x,p.y-1)<getdist(t,pl.x,pl.y)) {
				t.y=p.y-1;
			}
			
		}
		if(p.y+1<w && map[t.x][p.y+1]==STATE_ZONE) {
			if(flag) {
				t.y=p.y+1;
				flag=false;
			}
			else if(getdist(p.x,p.y+1)<getdist(t,pl.x,pl.y)) {
				t.y=p.y+1;
				
			}
			
		}
		System.err.println("Nearest to Box: "+t);
		next_box_pos=p;
		next_box_near_pos=t;
		//return printShortestDistance(convertTo1D(pl.x, pl.y),convertTo1D(p.x,p.y));
	}
	public boolean isBoxAround() {
		if(pl.x-1>0 && map[pl.x-1][pl.y]==STATE_BOX )return true;
		if(pl.x+1<h && map[pl.x+1][pl.y]==STATE_BOX )return true;
		if(pl.y-1>0 && map[pl.x][pl.y-1]==STATE_BOX )return true;
		if(pl.y+1<w && map[pl.x][pl.y+1]==STATE_BOX)return true;
			
		return false;
	}
	public int doAnAction() {
		
			
		
		updateTilesAroundPlayer();
		
		
			
		int action;
		System.err.println("Player: ("+pl.x+","+pl.y+")");
		System.err.println("--------------------------");
		System.err.println(" deadzoneStartingStep: "+deadzoneStartingStep);
		if(pl.stepCount<= (deadzoneStartingStep)-3) {
			if(state==Status.SEARCHING) {
				
				if(items.size()>0) {
					state=Status.Go_FOR_ITEM;
					goingToCenter=false;
				}
				else if(isBoxAround()) {
					goingToCenter=false;
					state=Status.PLACE_BOMB;
				}
				
				else if(boxes.size()>0 ) {
					goingToCenter=false;
						state=Status.GO_FOR_BOX;
						
				}
				
				
			}
			
			if(state==Status.GO_FOR_BOX) {
				findPathToNearestBox(0);
				System.err.println("lock for : "+next_box_pos);
				/*if(pl.x==next_box_near_pos.x && pl.y==next_box_near_pos.y) {
					boxes.remove(findBox(next_box_pos));
					action=ACTION_PLACE_BOMB;
					state=Status.RUN_FROM_BOMB;
					bomb_ticker=0;
					items.add(next_box_pos);
				}*/
				//else {
				
			
					int t= printShortestDistance(convertTo1D(pl.x, pl.y),convertTo1D(next_box_near_pos.x,next_box_near_pos.y));
					
					if(t==-1) {
						action=moveToTile(h/2, w/2);
						
						System.err.println("here 1 "+action);
					}
					else {
						Position p=convertTo2D(t);
						action=moveToTile(p.x, p.y);
					}
					state=Status.SEARCHING;
				//}
			}
			else if(state==Status.PLACE_BOMB) {
				
				action=ACTION_PLACE_BOMB;
				state=Status.RUN_FROM_BOMB;
			}
			else if(state==Status.RUN_FROM_BOMB) {
				
				int t= printShortestDistance(convertTo1D(pl.x, pl.y),convertTo1D(pl.x+1,pl.y-1));
				Position p=convertTo2D(t);
				action=moveToTile(p.x, p.y);
				if(bomb_ticker>=bombDelayPerStep) {
					state=Status.SEARCHING;
					bomb_ticker=0;
				}
				bomb_ticker++;
				System.err.println("bobm ticker"+bomb_ticker);
			}
			else if(state==Status.Go_FOR_ITEM) {
				Position p=items.get(0);
				if(pl.x==p.x && pl.y==p.y) {
					items.remove(0);
					state=Status.SEARCHING;
				}
				int t= printShortestDistance(convertTo1D(pl.x, pl.y),convertTo1D(p.x,p.y));
				p=convertTo2D(t);
				
				action=moveToTile(p.x, p.y);
				
			}
			
			else {
				int t=printShortestDistance(convertTo1D(pl.x, pl.y),convertTo1D(h/2,w/2));
			
				Position p=convertTo2D(t);
				action=moveToTile(p.x, p.y);
			}
			if(action==ACTION_STAY) {
				action=moveToTile(h/2, w/2);
			}
		}
		else {
			System.err.println("RUUUUUUUUUNNNNNN!!!!");
			int t=findNearestFromCenter();
			Position p=convertTo2D(t);
			action=moveToTile(p.x, p.y);
		}
			System.err.println("Action: "+action);
		
		return action;
	}
	public int convertTo1D(int x,int y) {
		return ((x)*w)+y;
	}
	public Position convertTo2D(int pos) {
		Position p = new Position();
		p.y=pos%w;
		p.x=((int)(pos/w));
		return p;
	}
	 
	 
	private int printShortestDistance(int s, int dest){
			// predecessor[i] array stores predecessor of
			// i and distance array stores distance of i
			// from s
			int pred[] = new int[v];
			int dist[] = new int[v];
			System.err.println("test");
			if (BFS( s, dest, pred, dist) == false) {
			   System.err.println("Given source and destination" +
			                                "are not connected");
			   return -1;
			}
			
			// LinkedList to store path
			LinkedList<Integer> path = new LinkedList<Integer>();
			int crawl = dest;
			path.add(crawl);
			while (pred[crawl] != -1) {
			   path.add(pred[crawl]);
			   crawl = pred[crawl];
			}
			
			// Print distance
			System.err.println("Shortest path length is: " + dist[dest]);
			
			// Print path
			System.err.println("Path is ::");
			for (int i = path.size() - 1; i >= 0; i--) {
			   System.err.print(path.get(i) + " ");
			}
			int h=path.size()-1;
			while(path.get(h)==convertTo1D(pl.x, pl.y)){
				h--;
			}
			return path.get(h);
}
	private boolean ShortestDistanceToCenter(int s, int dest){
		// predecessor[i] array stores predecessor of
		// i and distance array stores distance of i
		// from s
		int pred[] = new int[v];
		int dist[] = new int[v];
		System.err.println("test");
		if (BFS( s, dest, pred, dist) == false) {
		   System.err.println("Given source and destination" +
		                                "are not connected");
		   return false;
		}
		
		// LinkedList to store path
		 pathToCenter = new LinkedList<Integer>();
		int crawl = dest;
		pathToCenter.add(crawl);
		while (pred[crawl] != -1) {
			pathToCenter.add(pred[crawl]);
		   crawl = pred[crawl];
		}
		
		// Print distance
		System.err.println("Shortest path length is: " + dist[dest]);
		
		// Print path
		System.err.println("Path is ::");
		for (int i = pathToCenter.size() - 1; i >= 0; i--) {
		   System.err.print(pathToCenter.get(i) + " ");
		}
		return true;
		
}
	private boolean BFS(int src,int dest, int pred[], int dist[])
		{
			// a queue to maintain queue of vertices whose
			// adjacency list is to be scanned as per normal
			// BFS algorithm using LinkedList of Integer type
			LinkedList<Integer> queue = new LinkedList<Integer>();
			
			// boolean array visited[] which stores the
			// information whether ith vertex is reached
			// at least once in the Breadth first search
			boolean visited[] = new boolean[v];
			
			// initially all vertices are unvisited
			// so v[i] for all i is false
			// and as no path is yet constructed
			// dist[i] for all i set to infinity
			for (int i = 0; i < v; i++) {
				visited[i] = false;
				dist[i] = Integer.MAX_VALUE;
				pred[i] = -1;
			}
			
			// now source is first to be visited and
			// distance from source to itself should be 0
			visited[src] = true;
			dist[src] = 0;
			queue.add(src);
			
			// bfs Algorithm
			while (!queue.isEmpty()) {
				int u = queue.remove();
				for (int i = 0; i < adj.get(u).size(); i++) {
					if (visited[adj.get(u).get(i)] == false) {
						visited[adj.get(u).get(i)] = true;
						dist[adj.get(u).get(i)] = dist[u] + 1;
						pred[adj.get(u).get(i)] = u;
						queue.add(adj.get(u).get(i));
				
				// stopping condition (when we find
				// our destination)
					if (adj.get(u).get(i) == dest)
					  return true;
					}
				}
			}
			return false;
		}
		
	
}
