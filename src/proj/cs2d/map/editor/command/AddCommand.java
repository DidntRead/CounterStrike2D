package proj.cs2d.map.editor.command;

import proj.cs2d.map.Map;
import proj.cs2d.map.MapObject;

public class AddCommand extends Command {
	private Map map;
	private MapObject obj;
	
	public AddCommand(Map map, MapObject obj) {
		this.map= map;
		this.obj = obj;
	}

	@Override
	public Command execute() {
		this.map.add(obj);
		return this;
	}

	@Override
	public Command undo() {
		this.map.remove(obj);
		return this;
	}
}
