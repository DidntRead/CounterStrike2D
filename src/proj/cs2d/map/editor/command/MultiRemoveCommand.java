package proj.cs2d.map.editor.command;

import java.util.List;

import proj.cs2d.map.Map;
import proj.cs2d.map.MapObject;

public class MultiRemoveCommand extends Command {
	private Map map;
	private List<MapObject> objects;
	
	public MultiRemoveCommand(Map map, List<MapObject> obj) {
		this.map = map;
		this.objects = obj;
	}
	
	@Override
	public Command execute() {
		for(MapObject obj : objects) {
			map.remove(obj);
		}
		return this;
	}

	@Override
	public Command undo() {
		for(MapObject obj : objects) {
			map.add(obj);
		}
		return this;
	}
}
