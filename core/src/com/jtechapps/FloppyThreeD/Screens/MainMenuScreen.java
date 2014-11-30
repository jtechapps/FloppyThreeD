package com.jtechapps.FloppyThreeD.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jtechapps.FloppyThreeD.NativeInterface;

public class MainMenuScreen implements Screen {
	private NativeInterface nface;
	private Game g;
	private SpriteBatch batch;
	private Texture background;
	private float width;
	private float height;
	private Texture classicTexture;
	private Texture firstpersonTexture;
	private Texture settingsTexture;
	private Texture aboutTexture;
	private Stage stage;
	
	public MainMenuScreen(Game game, NativeInterface nativeinterface){
		g = game;
		nface = nativeinterface;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(background, 0, 0, width, height);
		batch.end();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		background = new Texture("img/backgroundone.png");
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		//load up the buttons
		classicTexture = new Texture("img/classicbutton.png");
		firstpersonTexture = new Texture("img/firstpersonbutton.png");
		settingsTexture = new Texture("img/settingsbutton.png");
		aboutTexture = new Texture("img/aboutbutton.png");
		
		//use images for the stage
		stage = new Stage();
		Image classic = new Image(classicTexture);
		classic.setWidth(width/5);
		classic.setHeight(height/7);
		classic.setX(width/4);
		classic.setY(height-height/4-classic.getHeight());
		classic.addListener(new ClickListener() {
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		    {
		        g.setScreen(new ClassicGameScreen(g, nface));
		        return true;
		    }
		});
		stage.addActor(classic);
		
		Image firstperson = new Image(firstpersonTexture);
		firstperson.setWidth(width/5);
		firstperson.setHeight(height/7);
		firstperson.setX(width-width/4-firstperson.getWidth());
		firstperson.setY(height-height/4-firstperson.getHeight());
		stage.addActor(firstperson);
		
		Image settings = new Image(settingsTexture);
		settings.setWidth(width/5);
		settings.setHeight(height/7);
		settings.setX(width/4);
		settings.setY(height-height/4-settings.getHeight()-classic.getHeight()-classic.getHeight()/2);
		stage.addActor(settings);
		
		Image about = new Image(aboutTexture);
		about.setWidth(width/5);
		about.setHeight(height/7);
		about.setX(width-width/4-firstperson.getWidth());
		about.setY(height-height/4-about.getHeight()-firstperson.getHeight()-firstperson.getHeight()/2);
		stage.addActor(about);
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		background.dispose();
		classicTexture.dispose();
		firstpersonTexture.dispose();
		settingsTexture.dispose();
		aboutTexture.dispose();
		batch.dispose();
		stage.dispose();
		this.dispose();
	}

}
