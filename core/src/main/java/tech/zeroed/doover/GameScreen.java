package tech.zeroed.doover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

/** First screen of the application. Displayed after the application is created. */
public class GameScreen implements Screen {

	private GameWorld gameWorld;

	@Override
	public void show() {
		gameWorld = new GameWorld();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,0.75f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameWorld.update(delta);

		gameWorld.draw(delta);
	}

	@Override
	public void resize(int width, int height) {
		gameWorld.resize(width, height);
	}

	@Override
	public void pause() {
		// Invoked when your application is paused.
	}

	@Override
	public void resume() {
		// Invoked when your application is resumed after pause.
	}

	@Override
	public void hide() {
		// This method is called when another screen replaces this one.
	}

	@Override
	public void dispose() {
		// Destroy screen's assets here.
	}
}