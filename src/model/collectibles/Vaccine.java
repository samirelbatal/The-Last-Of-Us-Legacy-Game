package model.collectibles;

import engine.Game;
import exceptions.NoAvailableResourcesException;
import model.characters.Hero;
import model.world.CharacterCell;

public class Vaccine implements Collectible{

	public Vaccine() {
		
	}
	
	public void pickUp(Hero hero) {
		hero.getVaccineInventory().add(this);
	}
	public void use(Hero hero) throws  NoAvailableResourcesException {
		
		if(hero.getVaccineInventory().isEmpty()) {
			throw new NoAvailableResourcesException("No available resources");
		}
		else {
		hero.getVaccineInventory().remove(this);
		int random = (int) (Math.random()* Game.availableHeroes.size());
		Hero spawnedHero = Game.availableHeroes.remove(random);
		Game.heroes.add(spawnedHero);
		spawnedHero.setLocation(hero.getTarget().getLocation());
		Game.zombies.remove(hero.getTarget());
		int x= (int) spawnedHero.getLocation().getX();
		int y= (int) spawnedHero.getLocation().getY();
		((CharacterCell)Game.map[x][y]).setCharacter(spawnedHero);
		Game.adjustVisibility(spawnedHero);
		
		}
	}
}
