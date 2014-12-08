package com.jtechapps.FloppyThreeD.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jtechapps.FloppyThreeD.NativeInterface;
import com.jtechapps.FloppyThreeD.ScoreManager;

public class AboutScreen implements Screen {

	private NativeInterface nface;
	private Game g;
	private SpriteBatch batch;
	private Texture background;
	private float width;
	private float height;
	private Texture menuTexture;
	private Stage stage;
	private LabelStyle labelstyle;
	private Label aboutlabel;

	public AboutScreen(Game game, NativeInterface nativeinterface){
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
		background = new Texture("img/backgroundonev2.png");
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		//load up the buttons
		menuTexture = new Texture("img/menubutton.png");

		//use images for the stage
		stage = new Stage();
		Image menu = new Image(menuTexture);
		menu.setWidth(width/5);
		menu.setHeight(height/7);
		menu.setX(width/2-menu.getWidth()/2);
		menu.setY(height/2);
		menu.addListener(new ClickListener() {
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		    {
		        g.setScreen(new MainMenuScreen(g, nface));
		        return true;
		    }
		});
		stage.addActor(menu);

		if(nface.getAssetManger()==null){// load assets
        	nface.setAssetManger(new  AssetManager());
        	nface.getAssetManger().load("models/pipe.g3db",Model.class);
        	nface.getAssetManger().load("models/bird.g3db",Model.class);
        	FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.size = (int) (height/18);
			nface.setFont1(generator.generateFont(parameter));
			generator.dispose();
        	nface.getAssetManger().finishLoading();
        }

		labelstyle = new LabelStyle();
		labelstyle.font=nface.getFont1();
		labelstyle.fontColor = Color.BLACK;
		aboutlabel = new Label("Made by Jtechapps", labelstyle);
		aboutlabel.setPosition(width/2-aboutlabel.getWidth()/2, height-height/4);
		stage.addActor(aboutlabel);

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
		menuTexture.dispose();
		batch.dispose();
		labelstyle.font.dispose();
		stage.dispose();
		this.dispose();
	}

}
