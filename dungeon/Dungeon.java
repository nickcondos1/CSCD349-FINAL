package dungeon;

import java.io.Serializable;
import java.util.Random;
import java.util.Scanner;

public class Dungeon implements Serializable
{
	private Room[][] dungeon = new Room[5][5];
	private Hero player;
	private int[] currentRoom;
	
	public Dungeon(Hero player)
	{
		currentRoom = new int[3];
		this.player = player;
		
		for (int row = 0; row < dungeon.length; row++)
		{
			
			for (int col = 0; col < dungeon.length; col++)
			{			
					dungeon[row][col] = new Room(row, col, dungeon.length, ' ');
			}
		}
		
		setPillars();
		setEntranceExit();
		
	}
	
	public Hero getPlayer()
	{
		return player;
	}
	
	public void printRoom()
	{
		dungeon[player.getLocation()[0]][player.getLocation()[1]].printRoom();
	}
	
	public StringBuilder toStringDungeon()
	{
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < dungeon.length; i++) //ROWS
		{
			StringBuilder one = new StringBuilder();
			StringBuilder two = new StringBuilder();
			StringBuilder three = new StringBuilder();
				for (int j = 0; j < 5; j++) //COL
				{			
					one.append(dungeon[i][j].toString().substring(0, 6)); // 0,3
					two.append(dungeon[i][j].toString().substring(6, 12)); //3,6
					three.append(dungeon[i][j].toString().substring(12, 18)); //6,9
										
				}
				str.append(one);
				str.append(two);
				str.append(three);						
		}
		
		
		return str;
	}
	
	public StringBuilder hideContent()
	{
		StringBuilder str = new StringBuilder();
		location();
		for (int i = 0; i < dungeon.length; i++) //ROWS
		{
			StringBuilder one = new StringBuilder();
			StringBuilder two = new StringBuilder();
			StringBuilder three = new StringBuilder();
				for (int j = 0; j < 5; j++) //COL
				{	
					
					if ((i != player.getLocation()[0]) || (j != player.getLocation()[1]))
					{
						if (dungeon[i][j].hasVisted() || dungeon[i][j].hasVision())
						{
							dungeon[i][j].showContent();
						}
						else
						{
							dungeon[i][j].hideContent();
						}
						
					}
										
					one.append(dungeon[i][j].toString().substring(0, 6)); // 0,3
					two.append(dungeon[i][j].toString().substring(6, 12)); //3,6
					three.append(dungeon[i][j].toString().substring(12, 18)); //6,9
										
				}
				str.append(one);
				str.append(two);
				str.append(three);						
		}		
		return str;
	}
	
	public void vision()
	{
		if (currentRoom[1] < 4)
			dungeon[currentRoom[0]][currentRoom[1] + 1].setVision(true);
		if (currentRoom[1] > 0)
			dungeon[currentRoom[0]][currentRoom[1] - 1].setVision(true);
		if (currentRoom[0] < 4)
			dungeon[currentRoom[0] + 1][currentRoom[1]].setVision(true);
		if (currentRoom[0] > 0)
			dungeon[currentRoom[0] - 1][currentRoom[1]].setVision(true);
	}
	
	public void resetVision()
	{
		if (currentRoom[1] < 4)
			dungeon[currentRoom[0]][currentRoom[1] + 1].resetVision();
		if (currentRoom[1] > 0)
			dungeon[currentRoom[0]][currentRoom[1] - 1].resetVision();
		if (currentRoom[0] < 4)
			dungeon[currentRoom[0] + 1][currentRoom[1]].resetVision();
		if (currentRoom[0] > 0)
			dungeon[currentRoom[0] - 1][currentRoom[1]].resetVision();
	}
	
	public void setEntranceExit()
	{
		Random rand = new Random();
		int[] location = new int[2];
		location[0] = rand.nextInt(5);
		location[1] = rand.nextInt(5);
		
		if (dungeon[location[0]][location[1]].getContents() == 'R') {
			dungeon[0][0].setContents("I");		
		}
		else if (location[0] == 4 && location[1] == 4) {
			dungeon[0][0].setContents("I");
			player.setLocation(new int[] {0,0});
		}
		else {
			dungeon[location[0]][location[1]].setContents("I");
			player.setLocation(location);
		}
		
		dungeon[4][4].setContents("O");
		
		
	
	}
	
	public void setPillars()
	{		
		dungeon[0][2].setContents("R"); //Four pillars
		dungeon[2][2].setContents("R");
		dungeon[4][3].setContents("R");
		dungeon[2][0].setContents("R");		
	}
	
		
	public void location()
	{	
		currentRoom[0] = player.getLocation()[0];
		currentRoom[1] = player.getLocation()[1];
		currentRoom[2] = dungeon[player.getLocation()[0]][player.getLocation()[1]].youAreHere(); //RETURNS CHARACTER PREVIOUSLY AT THAT LOCATION
	}
	
	public boolean atExit()
	{
		if (player.getLocation()[0] == 4 && player.getLocation()[1] == 4)
			return true;
		
		return false;
	}
	
	public boolean isValidMove(int move)
	{
		if (player.getLocation()[0] == 0 && move == 1)
			return false;
		if (player.getLocation()[0] == 4 && move == 2)
			return false;
		if (player.getLocation()[1] == 0 && move == 4)
			return false;
		if (player.getLocation()[1] == 4 && move == 3)
			return false;
		
		return true;
	}
	
	public void lootRoom()
	{
		if (dungeon[player.getLocation()[0]][player.getLocation()[1]].getContents() == 'R')
		{
			System.out.println("\nCongrats! You've found a Pillar of OO!");
			player.addPillar();
			dungeon[player.getLocation()[0]][player.getLocation()[1]].resetRoom('R');
		}
		
		if (dungeon[player.getLocation()[0]][player.getLocation()[1]].getContents() == 'P')
		{
			System.out.println("\nOh no! You've fallen in a pit and lost 25 hitpoints");
			player.subtractHP(25);
		}
		
		if (dungeon[player.getLocation()[0]][player.getLocation()[1]].getContents() == 'X')
		{
			System.out.println("\nPrepare for battle!");
			Monster theMonster = generateMonster();
			battle(player, theMonster);
			location();
			resetRoom();
		}
		
		if (dungeon[player.getLocation()[0]][player.getLocation()[1]].getContents() == 'H')
		{
			System.out.println("\nCongrats! You've found a health potion!");
			player.addLoot('H');
		}
		
		if (dungeon[player.getLocation()[0]][player.getLocation()[1]].getContents() == 'V')
		{
			System.out.println("\nCongrats! You've found a vision potion!");
			player.addLoot('V');
		}
		
		if (dungeon[player.getLocation()[0]][player.getLocation()[1]].getContents() == 'M')
		{
			System.out.println("\nCongrats! You've found the multiple item room!");
			Random rand = new Random();
			int num = rand.nextInt(2) + 2; // Between 2 and 3 items
			for (int i = 0; i < num; i++)
			{
				int chance = rand.nextInt(2);
				if (chance == 0){
					System.out.println("Congrats! You've found a health potion!");
					player.addLoot('H');
				}
				else {
					System.out.println("Congrats! You've found a vision potion!");
					player.addLoot('V');
				}				
			}
			
			if (Math.random() <= .30)
			{
				System.out.println("\nOh no! You've fallen in a pit and lost 25 hitpoints");
				System.out.println("But you've also found a plank of wood to cover the pit in the mess of random items");
				player.subtractHP(25);								
			}
		}
	}
	
	
	public void battle(Hero theHero, Monster theMonster)
	{
		Scanner input = new Scanner(System.in);
		char quit = 'p';
		
		FlyweightAttack that = FlyweightAttack.getInstance();
    	Attack atk;
		
		
		System.out.println(theHero.getName() + " battles " +
							theMonster.getName());
		System.out.println("---------------------------------------------");
		
		while (theHero.isAlive() && theMonster.isAlive() && quit != 'q')
		{
			theHero.battleChoices(theMonster);
			
			if (theMonster.isAlive())
			{
				atk = that.getAttack("Attack");
	    		atk.attack(theMonster, theHero);
			}
			
			System.out.print("\n-->q to quit, anything else to continue: ");
			String quitOrNot = input.nextLine();
			
			if (quitOrNot.length() > 0)
				quit = quitOrNot.charAt(0);
			else
				quit = ' ';

		}

		if (!theMonster.isAlive()) {
		    System.out.println(theHero.getName() + " was victorious!");
		    if(theMonster.loot(theMonster))//make them all the same drop rate and delete the param?
		    {
		    	System.out.println("You found a potion!");
		    	theHero.addLoot('H');
		    }
		    
		}
		else if (!theHero.isAlive()) {
			System.out.println(theHero.getName() + " was defeated :-(");
			System.out.println("\nPillars found: " + theHero.pillarsFound());
			System.exit(0);
		}
		else
			System.out.println("Quitters never win ;-)");
		

	}
	
	public Monster generateMonster()
	{
		int choice;
		boolean buffer = false;
		MonsterFactory factory = new MonsterFactory();
		choice = (int)(Math.random() * 5) + 1;
		if(choice == 5 && buffer == false)
		{
			if(Math.random() <=.5) 
			{
				choice = (int)(Math.random() * 5) + 1;
				buffer = true;
			}
		}
		return factory.createMonster(choice);
	}
	
	public void resetRoom()
	{
		char c = (char) currentRoom[2];
		dungeon[currentRoom[0]][currentRoom[1]].resetRoom(c);
	}
}
