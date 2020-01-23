package proj.cs2d.map.editor;

import java.util.Stack;

import proj.cs2d.map.editor.command.Command;

public class CommandManager {
	private Stack<Command> undoStack;
	private Stack<Command> redoStack;
	
	public CommandManager() {
		this.redoStack = new Stack<Command>();
		this.undoStack = new Stack<Command>();
	}
	
	public void undo() {
		if(undoStack.size() == 0) {
			return;
		}
		redoStack.add(undoStack.pop().undo());
	}
	
	public boolean canUndo() {
		return this.undoStack.size() > 0;
	}
	
	public void redo() {
		if(redoStack.size() == 0) return;
		undoStack.add(redoStack.pop().execute());
	}
	
	public boolean canRedo() {
		return this.redoStack.size() > 0;
	}
	
	public void execute(Command cmd) {
		redoStack.clear();
		undoStack.add(cmd.execute());
	}
}
