package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.characters.Explorer;
import model.characters.Fighter;
import model.characters.Hero;
import model.characters.Medic;
import model.characters.Zombie;

import model.collectibles.Supply;
import model.collectibles.Vaccine;
import model.world.Cell;
import model.world.CharacterCell;
import model.world.CollectibleCell;
import model.world.TrapCell;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughActionsException;

public class Game {
	
	
	public static ArrayList<Hero> availableHeroes = new ArrayList<Hero>();
	public static ArrayList<Hero> heroes = new ArrayList<Hero>();
	public static ArrayList<Zombie> zombies = new ArrayList<Zombie>();
	public static Cell[][] map = new Cell[15][15];

	public static void loadHeroes(String filePath) throws IOException {
		availableHeroes = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] sp = line.split(",");
			Hero h;
			if (sp[1].equals("EXP")) {
				h = new Explorer(sp[0], Integer.parseInt(sp[2]), Integer.parseInt(sp[4]), Integer.parseInt(sp[3]));
			} else if (sp[1].equals("FIGH")) {
				h = new Fighter(sp[0], Integer.parseInt(sp[2]), Integer.parseInt(sp[4]), Integer.parseInt(sp[3]));
			} else {
				h = new Medic(sp[0], Integer.parseInt(sp[2]), Integer.parseInt(sp[4]), Integer.parseInt(sp[3]));
			}
			availableHeroes.add(h);
			line = br.readLine();
		}
		br.close();
	}
	

	public static void endTurn() throws NotEnoughActionsException, InvalidTargetException {
		
		for (Zombie zombie : zombies) {
			zombie.attack();
			zombie.setTarget(null);
		}
		spawnNewZombie();
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[i].length; j++)
				map[i][j].setVisible(false);
		for (Hero hero : heroes) {
			hero.setActionsAvailable(hero.getMaxActions());
			hero.setTarget(null);
			hero.setSpecialAction(false);
			adjustVisibility(hero);
		}
	}

	public static void adjustVisibility(Hero hero) {
		Point p = hero.getLocation();
		for (int i = -1; i <= 1; i++) {
			int cx = p.x + i;
			if (cx >= 0 && cx <= 14) {
				for (int j = -1; j <= 1; j++) {
					int cy = p.y + j;
					if (cy >= 0 && cy <= 14) {
						if (cy >= 0 && cy <= map.length - 1) {
							map[cx][cy].setVisible(true);
						}
					}
				}
			}
		}
	}

	public static void spawnNewZombie() {
		Zombie zombie = new Zombie();
		zombies.add(zombie);
		int x, y;
		do {
			x = ((int) (Math.random() * map.length));
			y = ((int) (Math.random() * map[x].length));
		} while ((map[x][y] instanceof CharacterCell && ((CharacterCell) map[x][y]).getCharacter() != null)
				|| (map[x][y] instanceof CollectibleCell) || (map[x][y] instanceof TrapCell));
		if(Game.map[x][y].isVisible()){
			zombie.setLocation(new Point(x, y));
			map[x][y] = new CharacterCell(zombie);
			Game.map[x][y].setVisible(true);
		}else {
			zombie.setLocation(new Point(x, y));
			map[x][y] = new CharacterCell(zombie);
			
		}
		
	}

	public static boolean checkWin() {
		int remainingVaccines = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] instanceof CollectibleCell
						&& ((CollectibleCell) map[i][j]).getCollectible() instanceof Vaccine)
					remainingVaccines++;
			}
		}
		for (Hero hero : heroes) {
			remainingVaccines += hero.getVaccineInventory().size();
		}
		return heroes.size() >= 5 && remainingVaccines == 0;
	}

	public static boolean checkGameOver() {
		if (heroes.size() > 0) {
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[i].length; j++) {
					if (map[i][j] instanceof CollectibleCell
							&& ((CollectibleCell) map[i][j]).getCollectible() instanceof Vaccine)
						return false;
				}
			}
			for (Hero hero : heroes) {
				if (hero.getVaccineInventory().size() > 0)
					return false;
			}
		}
		return true;
	}

	public static void startGame(Hero hero) {
		
		heroes.add(hero);
		availableHeroes.remove(hero);
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new CharacterCell(null);
			}
		}

		((CharacterCell) map[0][0]).setCharacter(hero);
		hero.setLocation(new Point(0, 0));
		spawnCollectibles();
		for (int i = 0; i < 10; i++) {
			spawnNewZombie();
		}
		spawnTraps();
		adjustVisibility(hero);
	}

	public static void spawnCollectibles() {
		for (int i = 0; i < 5; i++) {
			Vaccine v = new Vaccine();
			int x, y;
			do {
				x = ((int) (Math.random() * map.length));
				y = ((int) (Math.random() * map[x].length));
			} while ((map[x][y] instanceof CharacterCell && ((CharacterCell) map[x][y]).getCharacter() != null)
					|| (map[x][y] instanceof CollectibleCell) || (map[x][y] instanceof TrapCell));
			map[x][y] = new CollectibleCell(v);
		}
		for (int i = 0; i < 5; i++) {
			Supply v = new Supply();
			int x, y;
			do {
				x = ((int) (Math.random() * map.length));
				y = ((int) (Math.random() * map[x].length));
			} while ((map[x][y] instanceof CharacterCell && ((CharacterCell) map[x][y]).getCharacter() != null)
					|| (map[x][y] instanceof CollectibleCell) || (map[x][y] instanceof TrapCell));
			map[x][y] = new CollectibleCell(v);
		}
		
	
		
	}	

	public static void spawnTraps() {
		for (int i = 0; i < 5; i++) {
			int x, y;
			do {
				x = ((int) (Math.random() * map.length));
				y = ((int) (Math.random() * map[x].length));
			} while ((map[x][y] instanceof CharacterCell && ((CharacterCell) map[x][y]).getCharacter() != null)
					|| (map[x][y] instanceof CollectibleCell) || (map[x][y] instanceof TrapCell));
			map[x][y] = new TrapCell();
		}
	}
}
