import java.util.ArrayList;
import java.util.Random;

public class MCTS {

	private Node root;
	private int depth;
	private int revisions;


	public MCTS(Node root, int depth, int rev) {
		this.root = root;
		this.depth=depth;
		this.revisions=rev;
	}

	public String monteCarloMove() {
		int i=0;
		String retval="";
		while(i<=revisions) {
			retval=this.searchForLeaf(root).getMove();
			i++;
		}
		return retval;
	}

	public Node searchForLeaf(Node node) {
		node.setN(node.getN()+1);
		if(node.isExpanded()) { //if all the children have been expanded then choose the
			Node nextNode=this.returnMaximumUCT(node);
			if(nextNode==null) {//node is a win situation, because there are no other available moves optional check
				return node;
			}
			searchForLeaf(nextNode);
			node.setEvaluate(-nextNode.getEvaluate());
			return nextNode;
		}else {
			//expansion part
			String nextMove=node.getWorld().getAvailMoves().get(0); //take the first unexplored child (because it has uct=infinity)
			node.getWorld().getAvailMoves().remove(0);
			//perform the move in a copy of the board
			String[][] newBoard=node.getWorld().copyBoard(node.getWorld().getBoard());

			int x1=Integer.parseInt(nextMove.charAt(0)+"");
			int y1=Integer.parseInt(nextMove.charAt(1)+"");
			int x2=Integer.parseInt(nextMove.charAt(2)+"");
			int y2=Integer.parseInt(nextMove.charAt(3)+"");

			String pawn=newBoard[x1][y1];
			newBoard[x2][y2]=pawn;
			newBoard[x1][y1]=" ";
			//the new move just performed
			//create the new node
			World newWorld=new World(newBoard, Math.abs(node.getWorld().getMyColor()-1));
			if(newWorld.getMyColor()==0)
				newWorld.whiteMoves();
			else
				newWorld.blackMoves();

			Node newNode=new Node(node, newWorld, nextMove);
			newNode.setN(1);
			//in the end just inform node for his new child;
			node.addChild(newNode);
			float evaluation=this.rollout(newNode, this.depth); 
			newNode.setEvaluate((evaluation)); 
			return newNode;
		}
	}


	//returns the child with maximum uct
	public Node returnMaximumUCT(Node node) {
		Node nextNode=null;
		float max=Float.NEGATIVE_INFINITY;
		for(Node n: node.getChildren()) {
			if(n.UCT()>max) {
				max=n.UCT();
				nextNode=n;
			}
		}
		return nextNode;
	}


	//we create a different world every time because we don't want to make changes in the first state

	public int rollout(Node node, int depth) {
		String nextMove="";
		if(depth==0) {
			//return node.calculate_evaluate(node.getMove());
			return node.getWorld().evaluate(node.getWorld().getMyColor());
		}
			
		//choose randomly next move
		if(node.getWorld().getAvailableMoves().size()<=0)
			return 0;
		else if (node.getWorld().getAvailableMoves().size()==1)
			nextMove=node.getWorld().getAvailableMoves().get(0);
		else
			nextMove=node.getWorld().getAvailableMoves().get(new Random().nextInt(node.getWorld().getAvailableMoves().size()-1));
		//decode next move
		int x1=Integer.parseInt(nextMove.charAt(0)+"");
		int y1=Integer.parseInt(nextMove.charAt(1)+"");
		int x2=Integer.parseInt(nextMove.charAt(2)+"");
		int y2=Integer.parseInt(nextMove.charAt(3)+"");
		//perform next move
		String[][] tmpBoard=node.getWorld().copyBoard(node.getWorld().getBoard());
		String pawn=tmpBoard[x1][y1];
		tmpBoard[x1][y1]=" ";
		tmpBoard[x2][y2]=pawn;
		//create new world with different color every time
		World newWorld=new World(tmpBoard, Math.abs(node.getWorld().getMyColor()-1));
		//upgrade the available moves
		if(newWorld.getMyColor()==0)
			newWorld.whiteMoves();
		else
			newWorld.blackMoves();
		
		Node newNode=new Node(node, newWorld, nextMove);
		return rollout(newNode,depth-1);
	}


/**
	public static void main(String[] args) {
		World world=new World();
		world.setMyColor(0);
		world.whiteMoves();
		Node root=new Node(null,world,"0000");
		MCTS mcts=new MCTS(root,5,10000);
		long start=System.currentTimeMillis();
		mcts.monteCarloMove();
		long end=System.currentTimeMillis();
		System.out.println("time spent: "+ (end-start)/1000);
		System.out.println("end");
	}  
*/

}
