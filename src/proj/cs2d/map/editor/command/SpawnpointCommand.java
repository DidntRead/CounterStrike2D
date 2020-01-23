package proj.cs2d.map.editor.command;

import proj.cs2d.map.Map;
import proj.cs2d.map.MapObject;

public class SpawnpointCommand extends Command {
	private int team;
	private Map map;
	private MapObject spawnPoint;
	
	public SpawnpointCommand(Map map, int team, MapObject spawnPoint) {
		this.map = map;
		this.team = team;
		this.spawnPoint = spawnPoint;
	}
	
	@Override
	public Command execute() {
		MapObject temp = this.map.getSpawnPoint(team);
		this.map.setSpawnPoint(team, spawnPoint);
		spawnPoint = temp;
		return this;
	}

	@Override
	public Command undo() {
		MapObject temp = this.map.getSpawnPoint(team);
		this.map.setSpawnPoint(team, spawnPoint);
		spawnPoint = temp;
		return this;
	}

}
