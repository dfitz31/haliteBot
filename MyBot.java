import hlt.*;

import java.util.ArrayList;
import java.lang.Math;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Tamagocchi");
        final ArrayList<Move> moveList = new ArrayList<>();
		final ArrayList<Planet> planetList = new ArrayList<>();
		int turn = 0; 
		

        for (;;) {
            moveList.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());
			
			
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }

                for (final Entity entity : gameMap.nearbyEntitiesByDistance(ship).values()) {
                    if(entity instanceof Ship){
						continue;
					}
					Planet planet = (Planet)entity;
					
					if (planet.getOwner() == gameMap.getMyPlayerId() && !planet.isFull()) {
						
						if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        break;
						}
						
						final ThrustMove newThrustMove = new Navigation(ship, planet).navigateToDock(gameMap, Constants.MAX_SPEED);
						if (newThrustMove != null) {
							moveList.add(newThrustMove);
						}
						break;
						
                        
                    }
					
					if (planet.getOwner() != gameMap.getMyPlayerId() && !planet.isFull()) {
						 
						for (Entity nearby : gameMap.nearbyEntitiesByDistance(ship).values()){
							for(int dockedId : planet.getDockedShips()){
								if(nearby.getId() == dockedId){
									Ship enemy = (Ship)nearby;
									break;
									
								}
							}
						}
			
						
						
						
						final ThrustMove newThrustMove = new Navigation(ship, enemy).navigateTowards(gameMap, enemy, Constants.MAX_SPEED, true, Constants.MAX_CORRECTIONS, Math.PI/180);
						if (newThrustMove != null) {
							moveList.add(newThrustMove);
						}
						break;
						
						
					}
					
					if (!planet.isOwned()){
						
						if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        break;
						}
						
						final ThrustMove newThrustMove = new Navigation(ship, planet).navigateToDock(gameMap, Constants.MAX_SPEED);
						if (newThrustMove != null) {
							moveList.add(newThrustMove);
						}
						break;
					}

                    else continue;

                    
                }
            }
            Networking.sendMoves(moveList);
			turn++;
        }
    }
}
