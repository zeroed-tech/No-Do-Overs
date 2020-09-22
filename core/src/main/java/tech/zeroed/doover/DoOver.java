package tech.zeroed.doover;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DoOver extends Game {
	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}