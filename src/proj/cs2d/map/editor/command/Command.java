package proj.cs2d.map.editor.command;

public abstract class Command {
	public abstract Command execute();
	public abstract Command undo();
}
