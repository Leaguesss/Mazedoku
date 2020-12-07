import java.io.*;
import java.util.*;
public class MazeGame {

    static int lives;
    static int steps;
    static int gold;
    static ArrayList<String> array;
    static boolean completed;

    public static void initialiseGame(String configFileName) throws IOException {
    	completed=false;
    	if (configFileName.equals("DEFAULT")) {
			lives = 3;
			steps = 20;
			gold = 0;
			array=new ArrayList<String>();
			array.add("#@ ##  &4#");
			array.add("##  # ## #");
			array.add("###  3#   ");
			array.add("#######  #");
			return;
		}
    	File f=new File(configFileName);
    	Scanner sc=new Scanner(f);
    	String[] temp=sc.nextLine().split(" ");
    	lives=Integer.parseInt(temp[0]);
    	steps=Integer.parseInt(temp[1]);
    	gold=Integer.parseInt(temp[2]);
    	array=new ArrayList<String>();
    	for(int i=0;i<Integer.parseInt(temp[3]);i++) {
			
    		array.add(sc.nextLine());
    	}
    }


    public static void saveGame(String toFileName) throws IOException {
        // TODO: Implement this method.
    	File f=new File(toFileName);
    	PrintWriter p=new PrintWriter(f);
    	p.println(lives+" "+steps+" "+gold+" "+array.size());
    	for(int i=0;i<array.size();i++) {
    		p.println(array.get(i));
    	}
    	p.close();
    }


    public static int getCurrentXPosition() {
    	return array.get(getCurrentYPosition()).indexOf('&');
    }


    public static int getCurrentYPosition() {
    	for(int i=0;i<array.size();i++) {
    		if(array.get(i).contains("&"))
    			return i;
    	}
    	return -1;
    }


    public static int numberOfLives() {
        return lives;
    }


    public static int numberOfStepsRemaining() {
        return steps;
    }

    public static int amountOfGold() {
        return gold;
    }


    public static boolean isMazeCompleted() {
        return completed;
    }


    public static boolean isGameEnd() {
		if(steps<=0) return true;
		if(lives<=0) return true;
		if(isMazeCompleted() == true) return true;
		return false;
    }


    public static boolean isValidCoordinates(int x, int y) {
        return x>=0&&x<array.get(0).length()&&y>=0&&y<array.size();
    }


    public static boolean canMoveTo(int x, int y) {
        return isValidCoordinates(x,y)&&(array.get(y).charAt(x)!='#')&&(!isGameEnd());
    }


    public static void moveTo(int x, int y) {
		steps--;
    	if(isValidCoordinates(x,y)&&(array.get(y).charAt(x)!='#')&&(steps>=0)&&(lives>0)&&!completed){
    		System.out.println("Moved to ("+x+", "+y+").");
    		if(array.get(y).charAt(x)=='@') {
				System.out.println("Congratulations! You completed the maze!");
				System.out.println("Your final status is:");
				printStatus();
				completed=true;
			}
    		if("0123456789".contains(array.get(y).substring(x, x+1))) {
    			gold+=Integer.parseInt(array.get(y).substring(x, x+1));
    			System.out.println("Plus "+array.get(y).substring(x, x+1)+" gold.");
    		}
    		int curx=getCurrentXPosition();
    		int cury=getCurrentYPosition();
    		array.set(cury, array.get(cury).substring(0,curx)+"."+array.get(cury).substring(curx+1,array.get(0).length()));
    		array.set(y,array.get(y).substring(0,x)+"&"+array.get(y).substring(x+1,array.get(0).length()));
    	}else {
    		System.out.println("Invalid move. One life lost.");
    		lives--;
    	}
    }


    public static void printHelp() {
    	System.out.println("Usage: You can type one of the following commands.");
		System.out.println("help         Print this help message.");
		System.out.println("board        Print the current board.");
		System.out.println("status       Print the current status.");
		System.out.println("left         Move the player 1 square to the left.");
		System.out.println("right        Move the player 1 square to the right.");
		System.out.println("up           Move the player 1 square up.");
		System.out.println("down         Move the player 1 square down.");
		System.out.println("save <file>  Save the current game configuration to the given file.");
    }


    public static void printStatus() {
    	System.out.println("Number of live(s): "+numberOfLives());
		System.out.println("Number of step(s) remaining: "+numberOfStepsRemaining());
		System.out.println("Amount of gold: "+amountOfGold());
    }


    public static void printBoard() {
    	for (int i = 0;i < array.size();i++) {
			System.out.println(array.get(i));
		}
    }

	public static void performAction(String action) throws IllegalArgumentException {
		if(action.equalsIgnoreCase("help")) printHelp(); 
		else if (action.equalsIgnoreCase("board")) printBoard();
		else if (action.equalsIgnoreCase("status")) printStatus();
		else if (action.equalsIgnoreCase("left")) moveTo(getCurrentXPosition()-1,getCurrentYPosition());
		else if (action.equalsIgnoreCase("right")) moveTo(getCurrentXPosition()+1,getCurrentYPosition());
		else if (action.equalsIgnoreCase("up")) moveTo(getCurrentXPosition(),getCurrentYPosition()-1);
		else if (action.equalsIgnoreCase("down")) moveTo(getCurrentXPosition(),getCurrentYPosition()+1);
		else if (action.length()>4 && action.substring(0,4).equalsIgnoreCase("save") && action.split(" ").length == 2) {
			String tofile = action.split(" ")[1];
			try {
				saveGame(tofile);
				System.out.println("Successfully saved the current game configuration to '"+ tofile+"'.");
			} catch (IOException e) {
				System.out.println("Error: Could not save the current game configuration to '"+ tofile+"'.");

			}
		} else {
			if (action.isEmpty()) {
				return;
			} 
			System.out.println("Error: Could not find command '"+action+"'.");
			System.out.println("To find the list of valid commands, please type 'help'.");
			throw new IllegalArgumentException();
		}
	}



    public static void main(String[] args){
    	if (args.length == 0) {
			System.out.println("Error: Too few arguments given. Expected 1 argument, found 0.");
			System.out.println("Usage: MazeGame [<game configuration file>|DEFAULT]");
			return;
			
		} else if (args.length > 1) {
			System.out.println("Error: Too many arguments given. Expected 1 argument, found " + args.length+".");
			System.out.println("Usage: MazeGame [<game configuration file>|DEFAULT]");
			return;
		}
    	try{
			initialiseGame(args[0]);
		} catch(IOException e) {
			System.out.println("Error: Could not load the game configuration from '"+args[0]+"'.");
			return;
		}
    	Scanner sc=new Scanner(System.in);
    	while(!isGameEnd()) {
			try {
				performAction(sc.nextLine());
			} catch(NoSuchElementException e) {
				System.out.println("You did not complete the game.");
				return;
			} catch (IllegalArgumentException e) {
				continue;
			}
			if(completed)return;
			if(steps==0&&lives==0) {
				System.out.println("Oh no! You have no lives and no steps left.");
				System.out.println("Better luck next time!");
				return;
			}
			if(steps==0) {
				System.out.println("Oh no! You have no steps left.");
				System.out.println("Better luck next time!");
				return;
			}
			if(lives==0) {
				System.out.println("Oh no! You have no lives left.");
				System.out.println("Better luck next time!");
				return;
			}
		}
    }
}

