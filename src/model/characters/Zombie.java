package model.characters;

import exceptions.*;
import engine.*;
import model.world.*;
import java.awt.Point;


public class Zombie extends Character {
	static int ZOMBIES_COUNT = 1;
	
	public Zombie() {
		super("Zombie " + ZOMBIES_COUNT, 40, 10);
		ZOMBIES_COUNT++;
	}
	
	public void attack() throws NotEnoughActionsException , InvalidTargetException{
		Point p=this.getLocation();
		for(int i=p.x-1;i<=p.x+1;i++) {
			for(int j=p.y-1;j<=p.y+1;j++) {
				if(i>=0 && i<=14 && j>=0 && j<=14 && Game.map[i][j] instanceof CharacterCell) {
					CharacterCell c=(CharacterCell)Game.map[i][j];
				
	if (((CharacterCell)c).getCharacter() instanceof Hero && !((CharacterCell)c).isSafe()) {
					this.setTarget(((CharacterCell)c).getCharacter());
					super.attack();
					return;
				}
				}
			}
		}
		
		
	}

}


