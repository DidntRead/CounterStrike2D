package proj.cs2d.map.editor.command;

import proj.cs2d.map.MapObject;

public class ResizeCommand extends Command {
	private int x,y,width,height;
	private MapObject obj;
	
	public ResizeCommand(int x, int y, int width, int height, MapObject obj) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.obj = obj;
	}
	
	@Override
	public Command execute() {
		int tX = obj.getX(), tY = obj.getY(), tWidth = obj.getWidth(), tHeight = obj.getHeight();
		obj.setPosition(x, y);
		obj.setSize(width, height);
		this.x = tX;
		this.y = tY;
		this.width = tWidth;
		this.height = tHeight;
		return this;
	}

	@Override
	public Command undo() {
		int tX = obj.getX(), tY = obj.getY(), tWidth = obj.getWidth(), tHeight = obj.getHeight();
		obj.setPosition(x, y);
		obj.setSize(width, height);
		this.x = tX;
		this.y = tY;
		this.width = tWidth;
		this.height = tHeight;
		return this;
	}
}
