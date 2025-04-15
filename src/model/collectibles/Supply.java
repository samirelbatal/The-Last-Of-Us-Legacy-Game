package model.collectibles;

import exceptions.NoAvailableResourcesException;
import model.characters.Hero;

public class Supply implements Collectible{

	public Supply() {
		
	}
	public void pickUp(Hero hero) {
		hero.getSupplyInventory().add(this);
	}
	public void use(Hero hero) throws NoAvailableResourcesException {
	if(hero.getSupplyInventory().isEmpty()) {
		throw new NoAvailableResourcesException("jj");
	}
	else {
		hero.getSupplyInventory().remove(this);

	}
	}


}
