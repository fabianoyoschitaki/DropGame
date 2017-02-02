package br.com.fsy.drop.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import br.com.fsy.drop.DropGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Drop";
		config.width = DropGame.SCREEN_WIDTH;
		config.height = DropGame.SCREEN_HEIGHT;
		new LwjglApplication(new DropGame(), config);
	}
}
