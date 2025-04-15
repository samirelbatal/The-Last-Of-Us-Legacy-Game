package model.collectibles;
import exceptions.InvalidTargetException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import model.characters.*;

public interface Collectible {
	
	public void pickUp(Hero h);
	public void use(Hero h) throws InvalidTargetException, NotEnoughActionsException ,NoAvailableResourcesException;
	
}
