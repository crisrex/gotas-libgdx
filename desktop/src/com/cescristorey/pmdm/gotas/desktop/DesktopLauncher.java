package com.cescristorey.pmdm.gotas.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cescristorey.pmdm.gotas.Gotas;
import java.io.File;

public class DesktopLauncher {
	public static void main (String[] arg) {
            //System.out.println(new File(".").getAbsolutePath());
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Gotas(), config);
	}
}
