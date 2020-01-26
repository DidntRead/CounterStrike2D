package proj.cs2d.map;

import java.awt.Color;
import java.awt.Rectangle;

public class Cool extends Map {
	public Cool() {
		super(1200);
		spawnPoint0 = new MapObject(new Rectangle(294, 0, 112, 112), false);
		spawnPoint1 = new MapObject(new Rectangle(324, 880, 112, 112), false);
		tree.insert(new RenderableMapObject(406, 112, 140, 58, Color.blue));
		tree.insert(new RenderableMapObject(190, 112, 106, 58, Color.blue));
		tree.insert(new RenderableMapObject(506, 170, 40, 176, Color.blue));
		tree.insert(new RenderableMapObject(468, 346, 78, 38, Color.blue));
		tree.insert(new RenderableMapObject(506, 384, 40, 140, Color.blue));
		tree.insert(new RenderableMapObject(506, 594, 40, 36, Color.blue));
		tree.insert(new RenderableMapObject(472, 630, 74, 34, Color.blue));
		tree.insert(new RenderableMapObject(506, 664, 40, 176, Color.blue));
		tree.insert(new RenderableMapObject(436, 840, 110, 40, Color.blue));
		tree.insert(new RenderableMapObject(0, 994, 774, 40, Color.blue));
		tree.insert(new RenderableMapObject(0, 194, 98, 78, Color.blue));
		tree.insert(new RenderableMapObject(160, 112, 30, 768, Color.blue));
		tree.insert(new RenderableMapObject(0, 0, 30, 196, Color.blue));
		tree.insert(new RenderableMapObject(0, 272, 28, 152, Color.blue));
		tree.insert(new RenderableMapObject(96, 370, 64, 54, Color.blue));
		tree.insert(new RenderableMapObject(246, 228, 210, 50, Color.blue));
		tree.insert(new RenderableMapObject(768, 368, 210, 50, Color.blue));
		tree.insert(new RenderableMapObject(246, 340, 150, 50, Color.blue));
		tree.insert(new RenderableMapObject(332, 430, 2, 2, Color.blue));
		tree.insert(new RenderableMapObject(326, 428, 50, 210, Color.blue));
		tree.insert(new RenderableMapObject(674, 294, 60, 122, Color.blue));
		tree.insert(new RenderableMapObject(546, 294, 58, 122, Color.blue));
		tree.insert(new RenderableMapObject(586, 142, 100, 100, Color.blue));
		tree.insert(new RenderableMapObject(452, 346, 16, 38, Color.blue));
		tree.insert(new RenderableMapObject(188, 840, 136, 40, Color.blue));
		tree.insert(new RenderableMapObject(90, 744, 70, 136, Color.blue));
		tree.insert(new RenderableMapObject(0, 744, 34, 136, Color.blue));
		tree.insert(new RenderableMapObject(190, 534, 50, 56, Color.blue));
		tree.insert(new RenderableMapObject(190, 430, 50, 56, Color.blue));
		tree.insert(new RenderableMapObject(40, 472, 80, 80, Color.blue));
		tree.insert(new RenderableMapObject(40, 608, 80, 80, Color.blue));
		tree.insert(new RenderableMapObject(546, 840, 48, 40, Color.blue));
		tree.insert(new RenderableMapObject(664, 840, 70, 40, Color.blue));
		tree.insert(new RenderableMapObject(546, 680, 50, 40, Color.blue));
		tree.insert(new RenderableMapObject(596, 680, 40, 80, Color.blue));
		tree.insert(new RenderableMapObject(734, 0, 40, 880, Color.blue));
		tree.insert(new RenderableMapObject(546, 594, 120, 30, Color.blue));
		tree.insert(new RenderableMapObject(608, 474, 58, 120, Color.blue));
		tree.insert(new RenderableMapObject(756, 880, 16, 66, Color.blue));
		tree.insert(new RenderableMapObject(734, 880, 40, 122, Color.blue));
		tree.insert(new RenderableMapObject(768, 416, 240, 54, Color.blue));
		tree.insert(new RenderableMapObject(458, 630, 16, 34, Color.blue));
		tree.insert(new RenderableMapObject(240, 720, 210, 50, Color.blue));
	}
}
