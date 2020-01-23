package proj.cs2d.map.editor.command;

import proj.cs2d.map.MapObject;

public class MoveCommand extends Command {
	private int x, y;
	private MapObject obj;
	
	public MoveCommand(int x, int y, MapObject obj) {
		this.x = x;
		this.y = y;
		this.obj = obj;
	}
	
	@Override
	public Command execute() {
		int tX = obj.getX();
		int tY = obj.getY();
		this.obj.setPosition(x, y);
		this.x = tX;
		this.y = tY;
		return this;
	}

	@Override
	public Command undo() {
		int tX = obj.getX();
		int tY = obj.getY();
		this.obj.setPosition(x, y);
		this.x = tX;
		this.y = tY;
		return this;
	}
}
