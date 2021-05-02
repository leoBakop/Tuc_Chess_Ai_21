import java.util.ArrayList;
import java.util.Random;


public class World
{
	private String[][] board = null;
	private int rows = 7;
	private int columns = 5;
	private int myColor = 0; //0==white 1==black
	private ArrayList<String> availableMoves = null;
	private int rookBlocks = 3;		// rook can move towards <rookBlocks> blocks in any vertical or horizontal direction
	private int nTurns = 0;
	private int nBranches = 0;
	private int noPrize = 9;

	//new code by lui
	private int whiteScore;
	private int blackScore;
	private String currentMove; 
	private int alg; // 0 for random, 1 for Minimax , other for Monte Carlo
	private int counterMinMax=0; 
	private int CounterMonteCarlo=0;
	private int MonteCarloRev=0;
	private int MonteCarloRevCounter=0;
	private int depthMC=4;
	private int depthMinMax=4;
	
	
	public World()
	{
		board = new String[rows][columns];

		/* represent the board
		BP|BR|BK|BR|BP
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP
		 */

		// initialization of the board
		for(int i=0; i<rows; i++)
			for(int j=0; j<columns; j++)
				board[i][j] = " ";

		// setting the black player's chess parts

		// black pawns
		for(int j=0; j<columns; j++)
			board[1][j] = "BP";

		board[0][0] = "BP";
		board[0][columns-1] = "BP";

		// black rooks
		board[0][1] = "BR";
		board[0][columns-2] = "BR";

		// black king
		board[0][columns/2] = "BK";

		// setting the white player's chess parts

		// white pawns
		for(int j=0; j<columns; j++)
			board[rows-2][j] = "WP";

		board[rows-1][0] = "WP";
		board[rows-1][columns-1] = "WP";

		// white rooks
		board[rows-1][1] = "WR";
		board[rows-1][columns-2] = "WR";

		// white king
		board[rows-1][columns/2] = "WK";

		// setting the prizes
		for(int j=0; j<columns; j++)
			board[rows/2][j] = "P";

		availableMoves = new ArrayList<String>();

		//new code by lui
		this.blackScore=0;
		this.whiteScore=0;
		this.currentMove=" ";		
		alg=15;
	}

	//alternate constructor added by lui
	public World(String[][]board, int mycolor) {
		this.board=board;
		this.myColor=mycolor;
		availableMoves = new ArrayList<String>();
		if(mycolor==0)
			this.whiteMoves();
		else
			this.blackMoves();

	}

	public void setMyColor(int myColor)
	{
		this.myColor = myColor;
	}

	public String selectAction()
	{
		availableMoves = new ArrayList<String>();

		if(myColor == 0)		// I am the white player
			this.whiteMoves();
		else					// I am the black player
			this.blackMoves();

		// keeping track of the branch factor
		nTurns++;
		nBranches += availableMoves.size();

		//return this.selectRandomAction();
		//new code by lui
		if(alg==1) 
			return this.minMax();
		else if(alg==0)
		    return this.selectRandomAction();
		else 
			return this.MonteCarlo();
	}

	public void whiteMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));

				// if it there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;

				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));

				if(secondLetter.equals("P"))	// it is a pawn
				{
					if(i>=1)// check if it can move one vertical position ahead
						firstLetter = Character.toString(board[i-1][j].charAt(0)); //if added by lui

					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i-1) + Integer.toString(j);

						availableMoves.add(0,move);
					}

					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j-1].charAt(0));						
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i-1) + Integer.toString(j-1);

							availableMoves.add(0, move); //adding this move to the front--it s probably more useful --dont cut them pruning
						}											
					}

					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j+1].charAt(0));
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {

							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i-1) + Integer.toString(j+1);	

							availableMoves.add(0,move); //adding this move to the front--it s probably more useful --dont cut them pruning
						}
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i-(k+1)) + Integer.toString(j);

						availableMoves.add(move); // it was add (0, move)

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;

						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i+(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i) + Integer.toString(j-(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;

						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i) + Integer.toString(j+(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i-1) + Integer.toString(j);

							availableMoves.add(move);	
						}
					}

					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i+1) + Integer.toString(j);

							availableMoves.add(move);	
						}
					}

					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i) + Integer.toString(j-1);

							availableMoves.add(move);	
						}
					}

					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i) + Integer.toString(j+1);

							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}


	public void blackMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));

				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;

				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));

				if(secondLetter.equals("P"))	// it is a pawn
				{


					if(i<rows-1)// check if it can move one vertical position ahead
						firstLetter = Character.toString(board[i+1][j].charAt(0));

					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i+1) + Integer.toString(j);

						availableMoves.add(0,move);
					}

					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j-1].charAt(0));

						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i+1) + Integer.toString(j-1);

							availableMoves.add(0,move);
						}																	
					}

					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j+1].charAt(0));

						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i+1) + Integer.toString(j+1);

							availableMoves.add(0,move);
						}



					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i-(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;

						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i+(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i) + Integer.toString(j-(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;

						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i) + Integer.toString(j+(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i-1) + Integer.toString(j);

							availableMoves.add(move);	
						}
					}

					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i+1) + Integer.toString(j);

							availableMoves.add(move);	
						}
					}

					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i) + Integer.toString(j-1);

							availableMoves.add(move);	
						}
					}

					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i) + Integer.toString(j+1);

							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}

	private String selectRandomAction()
	{
	Random ran = new Random();
	int	x = ran.nextInt(availableMoves.size());
	return availableMoves.get(x);
	}

	public double getAvgBFactor()
	{
		return nBranches / (double) nTurns;
	}

	public void makeMove(int x1, int y1, int x2, int y2, int prizeX, int prizeY)
	{
		String chesspart = Character.toString(board[x1][y1].charAt(1));

		boolean pawnLastRow = false;

		// check if it is a move that has made a move to the last line
		if(chesspart.equals("P"))
			if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) )
			{
				board[x2][y2] = " ";	// in a case an opponent's chess part has just been captured
				board[x1][y1] = " ";
				pawnLastRow = true;
			}

		// otherwise
		if(!pawnLastRow)
		{
			board[x2][y2] = board[x1][y1];
			board[x1][y1] = " ";
		}

		// check if a prize has been added in the game
		if(prizeX != noPrize)
			board[prizeX][prizeY] = "P";
	}
	//-------------------------------------------------------------------------------------------------------------------------
	//our section
	public String minMax() {
		
		this.counterMinMax++;
		if(this.counterMinMax%12==0)
			this.depthMinMax++;
		
		String move=(String) this.miniMax(0, this.myColor, this.depthMinMax, Integer.MIN_VALUE, Integer.MAX_VALUE);
		if(move.length()!=4) {
			System.out.println("problem in minmax");
			return this.selectRandomAction();
		}
			
		return move;
	}


	private Object miniMax(int depth, int color, int maxDepth, int alpha, int beta) {
		

		if (depth==maxDepth) {
			return this.evaluate(Math.abs(color-1));  //with the previous color
		}

		if(color==this.myColor) { //my color is max
			int max=Integer.MIN_VALUE;
			String retMove="";
			ArrayList<String> currentAvailMoves=copyArrayList(this.availableMoves);
			String[][] tmp_board=this.copyBoard(board);


			for(String move:currentAvailMoves) {

				//perform the move
				int r1=Integer.parseInt(move.charAt(0)+"");
				int col1=Integer.parseInt(move.charAt(1)+"");
				int r2=Integer.parseInt(move.charAt(2)+"");
				int col2=Integer.parseInt(move.charAt(3)+"");

				//update the board
				String pawn=board[r1][col1];
				board[r1][col1]=" ";
				board[r2][col2]=pawn;

				//update new moves
				if(color==0) // if a am the white the next player is the black one
					this.blackMoves();
				else
					this.whiteMoves();

				this.currentMove=move;
				int tmpMax=(int)miniMax(depth+1, Math.abs(color-1), maxDepth,alpha,beta);

				if(tmpMax>max) {
					max=tmpMax;
					retMove=move;
				}
				alpha=Integer.max(alpha, tmpMax);
				
				

				//now i have to restore the previous values in board and avail moves
				board=copyBoard(tmp_board);
				availableMoves=copyArrayList(currentAvailMoves); //maybe this is not needed
				
				if(beta<=alpha)
					break;


			}
			if(depth==0)
				return retMove;

			return max;
		}

		if(color!=this.myColor) { //minimum
			int min=Integer.MAX_VALUE;
			String retMove=" ";
			ArrayList<String> currentAvailMoves=copyArrayList(this.availableMoves);
			String[][] tmp_board=this.copyBoard(board);
			for(String move:currentAvailMoves) {

				//perform the move
				int r1=Integer.parseInt(move.charAt(0)+"");
				int col1=Integer.parseInt(move.charAt(1)+"");
				int r2=Integer.parseInt(move.charAt(2)+"");
				int col2=Integer.parseInt(move.charAt(3)+"");
				//update the board
				String pawn=board[r1][col1];
				board[r1][col1]=" ";
				board[r2][col2]=pawn;

				//update new moves
				if(color==0) // if a am the white the next player is the black one
					this.blackMoves();
				else
					this.whiteMoves();
				this.currentMove=move;
				int tmpMin=(int)miniMax(depth+1, Math.abs(color-1), maxDepth, alpha, beta);
				if(tmpMin<min) {
					min=tmpMin;
					retMove=move;
				}
				beta=Integer.min(tmpMin, beta);
				
				//now i have to restore the previous values in board and avail moves
				board=copyBoard(tmp_board);
				availableMoves=copyArrayList(currentAvailMoves); //maybe this is not needed
				
				if(beta<=alpha)
					break;
				

			}
			if(depth==0)
				return retMove;
			return min;
		}

		return 0;
	}

	public int evaluate(int color) {
		
		int score1=0, score0=0;

		for(int i=0; i<this.rows; i++) {

			for(int j=0; j<this.columns; j++ ) {

				//evaluation based in pawns for white
				if((board[i][j].charAt(0)+"").equals("W")) {

					//if that pawn is in the last row then give boost to that move

					switch(board[i][j].charAt(1)+"") {

					case "K":
						
						if(i==0)
							score0+=100;
						if(i==1)
							score0+=100;
						if(i==2)
							score0+=100;
						if(i==3)
							score0+=100;
						if(i==4)
							score0+=100;
						if(i==5)
							score0+=100;
						if(i==6)
							score0+=100;
						break;

					case"R": //tower
						if(i==0)
							score0+=90;
						if(i==1)
							score0+=90;
						if(i==2)
							score0+=110;
						if(i==3)
							score0+=105;
						if(i==4)
							score0+=100;
						if(i==5)
							score0+=90;
						if(i==6)
							score0+=60;
						break;

					case"P": //pawn
						if(i==0)
							score0+=60;
						if(i==1)
							score0+=60;
						if(i==2)
							score0+=70;
						if(i==3)
							score0+=100;
						if(i==4)
							score0+=110;
						if(i==5)
							score0+=110;
						if(i==6)
							score0+=80;
						break;

					}
				}
				//evaluation based in pawns for black

				if((board[i][j].charAt(0)+"").equals("B")) {

					//if that pawn is in the last row then give boost in that move
					switch(board[i][j].charAt(1)+"") {
					case "K":
						
						if(i==6)
							score1+=100;
						if(i==5)
							score1+=100;
						if(i==4)
							score1+=100;
						if(i==3)
							score1+=100;
						if(i==2)
							score1+=100;
						if(i==1)
							score1+=100;
						if(i==0)
							score1+=100;
						break;

					case"R": //tower
						if(i==6)
							score1+=90;
						if(i==5)
							score1+=90;
						if(i==4)
							score1+=110;
						if(i==3)
							score1+=105;
						if(i==2)
							score1+=100;
						if(i==1)
							score1+=90;
						if(i==0)
							score1+=60;
						break;

					case"P": //pawn
						if(i==6)
							score1+=60;
						if(i==5)
							score1+=60;
						if(i==4)
							score1+=70;
						if(i==3)
							score1+=100;
						if(i==2)
							score1+=110;
						if(i==1)
							score1+=110;
						if(i==0)
							score1+=80;
						break;

					}

					//in case of presents
					//giati einai kakh idea na vriskeis polla dvra 

					if((board[i][j]).equals("P")) {

						if(color==0)
							score0-=50;
						else
							score1-=50;

					}
				}
			}

		}
		
		
		/**if(isMax&&color==0&&gameScore+10>0&&kingFound==1) //if i am a maximum player and i win then choose to live just one king (terminate the game)
			return Integer.MAX_VALUE;
		if(isMax&&color==1&&gameScore<0&&kingFound==1)	
			return Integer.MAX_VALUE;
		if((!isMax)&&color==0&&gameScore>0&&kingFound==1)
			return Integer.MIN_VALUE;
		if((!isMax)&&color==1&&gameScore<0&&kingFound==1)
			return Integer.MIN_VALUE;  */

		if(color==0)
			return score0-score1;
		else
			return score1-score0;
	}




	public ArrayList<String> copyArrayList(ArrayList<String> al){
		ArrayList<String> retVal=new ArrayList<String>();
		for(int i=0; i<al.size(); i++) {
			retVal.add(al.get(i));
		}
		return retVal;
	}


	public String[][] copyBoard(String[][] board){
		String[][] retVal=new String[this.rows][this.columns];
		for(int i=0; i<this.rows; i++) {
			for(int j=0; j<this.columns; j++) {
				retVal[i][j]=board[i][j];
			}
		}

		return retVal;
	}


	public String MonteCarlo() {
		this.CounterMonteCarlo++;
		if(this.CounterMonteCarlo%15==0)
			this.depthMC++;
		if(this.MonteCarloRevCounter%5==0)
			this.MonteCarloRev+=1000;
		Node root=new Node(null, this, "0000"); //null move
		MCTS m=new MCTS(root,this.depthMC, 10000+this.MonteCarloRev);
		return m.monteCarloMove();
	}


	public void setWhiteScore(int whiteScore) {
		this.whiteScore = whiteScore;
	}

	public void setBlackScore(int blackScore) {
		this.blackScore = blackScore;
	}

	public ArrayList<String> getAvailMoves(){
		return this.availableMoves;
	}

	public int getMyColor() {
		return myColor;
	}

	public String[][] getBoard() {
		return board;
	}

	public void setBoard(String[][] board) {
		this.board = board;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public ArrayList<String> getAvailableMoves() {
		return availableMoves;
	}

	public int getRookBlocks() {
		return rookBlocks;
	}

	public int getnTurns() {
		return nTurns;
	}

	public int getnBranches() {
		return nBranches;
	}

	public int getNoPrize() {
		return noPrize;
	}

	public int getWhiteScore() {
		return whiteScore;
	}

	public int getBlackScore() {
		return blackScore;
	}

	

	public String getCurrentMove() {
		return currentMove;
	}

	public void setCurrentMove(String currentMove) {
		this.currentMove = currentMove;
	}



}
