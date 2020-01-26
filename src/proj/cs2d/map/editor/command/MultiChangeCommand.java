package proj.cs2d.map.editor.command;

import java.awt.Color;
import java.util.List;

import proj.cs2d.map.HealthPickup;
import proj.cs2d.map.MapObject;
import proj.cs2d.map.RenderableMapObject;

public class MultiChangeCommand extends Command {
	private List<MapObject> obj;
	private Change change;
	private Object[] newValue;
	
	public MultiChangeCommand(List<MapObject> obj, Change change, Object newValue) {
		this.obj = obj;
		this.change = change;
		this.newValue = new Object[obj.size()];
		for(int i = 0; i < this.newValue.length; i++) {
			this.newValue[i] = newValue;
		}
	}
	
	@Override
	public Command execute() {
		for(int i = 0; i < newValue.length; i++) {
			switch (change) {
			case Collidable:
				boolean newColl = (boolean) newValue[i];
				newValue[i] = obj.get(i).isCollidable();
				obj.get(i).setCollidable(newColl);
				break;
			case Color:
				Color newColor = (Color) newValue[i];
				newValue[i] = ((RenderableMapObject)obj.get(i)).getColor();
				((RenderableMapObject)obj.get(i)).setColor(newColor);
				break;
			case HealthRestore:
				int healthRestore = (Integer) newValue[i];
				newValue[i] = ((HealthPickup)obj.get(i)).getHealthRestoration();
				((HealthPickup)obj.get(i)).setHealthRestoration(healthRestore);
				break;
			case Cooldown:
				int cooldown = (Integer) newValue[i];
				newValue[i] = ((HealthPickup)obj.get(i)).getCooldown();
				((HealthPickup)obj.get(i)).setCooldown(cooldown);
				break;
			}
		}
		return this;
	}

	@Override
	public Command undo() {
		for(int i = 0; i < newValue.length; i++) {
			switch (change) {
			case Collidable:
				boolean newColl = (boolean) newValue[i];
				newValue[i] = obj.get(i).isCollidable();
				obj.get(i).setCollidable(newColl);
				break;
			case Color:
				Color newColor = (Color) newValue[i];
				newValue[i] = ((RenderableMapObject)obj.get(i)).getColor();
				((RenderableMapObject)obj.get(i)).setColor(newColor);
				break;
			case HealthRestore:
				int healthRestore = (Integer) newValue[i];
				newValue[i] = ((HealthPickup)obj.get(i)).getHealthRestoration();
				((HealthPickup)obj.get(i)).setHealthRestoration(healthRestore);
				break;
			case Cooldown:
				int cooldown = (Integer) newValue[i];
				newValue[i] = ((HealthPickup)obj.get(i)).getCooldown();
				((HealthPickup)obj.get(i)).setCooldown(cooldown);
				break;
			}
		}
		return this;
	}

}
