import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
public class Node {
	private Node parent;
	private World world;
	private int  n;
	private float evaluate;
	private ArrayList<Node> children;
	private float c=0.707f;
	private String move; //move lead to this node

	public Node(Node parent,World world, String move) {
		super();
		this.parent=parent;
		this.world = world;
		this.evaluate=this.calculate_evaluate(move);
		this.n = 0;
		this.children=new ArrayList<Node>();
		this.move=move;

	}



	public String getMove() {
		return move;
	}
	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public float getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(float evaluate) {
		this.evaluate = evaluate;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public float UCT() {
		if(this.n==0)
			return Float.MAX_VALUE;
		double retVal= this.c*Math.sqrt(Math.log(this.parent.n)/this.n);
		return (float)(this.evaluate+retVal);
	}

	public boolean isExpanded() {
		return this.world.getAvailMoves().isEmpty();
	}

	public void addChild(Node node) {

		this.children.add(node);
	}



	public int calculate_evaluate(String move) {
		int score0=0;
		int score1=0;
		int kingFound=0;
		int x1=Integer.parseInt(move.charAt(0)+"");
		int y1=Integer.parseInt(move.charAt(1)+"");
		int x2=Integer.parseInt(move.charAt(2)+"");
		int y2=Integer.parseInt(move.charAt(3)+"");


		for(int i=0; i<this.world.getRows(); i++) {
			for(int j=0; j<this.world.getColumns(); j++ ) {

				//evaluation based in pawns for white
				if((world.getBoard()[i][j].charAt(0)+"").equals("W")) {
					//if that pawn is in the last row then give boost in that move
					if(i==0)
						score0+=200;
					switch(world.getBoard()[i][j].charAt(1)+"") {
					case "K":
						score0+=200;
						kingFound+=10;
						break;
					case"R":
						score0+=100;
						break;
					case"P":
						score0+=60;
						break;
					}


				}
				//evaluation based in pawns for black
				if((world.getBoard()[i][j].charAt(0)+"").equals("B")) {
					//if that pawn is in the last row then give boost in that move
					if(i==6)
						score1+=200;
					switch(world.getBoard()[i][j].charAt(1)+"") {
					case "K":
						score1+=200;
						kingFound+=200;
						break;
					case"R":
						score1+=100;
						break;
					case"P":
						score1+=60;
						break;
					}
				}

			}
		}

		//if it is a cross move then it means that in the last position you ate sbd (and we know that this move was made by the world.getColor);
		if(x1!=x2&&y1!=y2 ) {
			if(this.world.getMyColor()==0) 
				score0+=150;
			else
				score1+=150;
		}
		if(kingFound==10 && this.world.getMyColor()==0||kingFound==200 && this.world.getMyColor()==1)//if there is only my king
			return Integer.MAX_VALUE;
		if(kingFound==10 && this.world.getMyColor()==1||kingFound==200 && this.world.getMyColor()==0) //if my king didn't found
			return Integer.MIN_VALUE;
		return world.getMyColor()==0 ? 3*(score0-score1) : 3*(score1-score0); //for every case if score>0 then we win else we lose
	}




}
