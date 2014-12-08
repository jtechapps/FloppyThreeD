package com.jtechapps.FloppyThreeD.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.jtechapps.FloppyThreeD.NativeInterface;
import com.jtechapps.FloppyThreeD.ScoreManager;
import com.jtechapps.FloppyThreeD.SettingsManager;

public class SettingsScreen implements Screen {
	private NativeInterface nface;
	private Game g;
	private SpriteBatch batch;
	private Texture background;
	private float width;
	private float height;
	private Texture quickresetTexture;
	private Texture noobTexture;
	private Texture easyTexture;
	private Texture mediumTexture;
	private Texture hardTexture;
	private Texture proTexture;
	private Stage stage;
	private Texture quickresetonTexture;
	private Texture menuTexture;
	private Texture aboutTexture;
	private SettingsManager settingsManager = new SettingsManager();

	public SettingsScreen(Game game, NativeInterface nativeinterface){
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
		quickresetTexture = new Texture("img/quickresetoff.png");
		quickresetonTexture = new Texture("img/quickreseton.png");
		noobTexture = new Texture("img/noob.png");
		easyTexture = new Texture("img/easy.png");
		mediumTexture = new Texture("img/medium.png");
		hardTexture = new Texture("img/hard.png");
		proTexture = new Texture("img/pro.png");
		aboutTexture = new Texture("img/aboutbutton.png");
		menuTexture = new Texture("img/menubutton.png");

		//use images for the stage
		stage = new Stage();
		final Image quickreset = new Image(quickresetTexture);
		if(settingsManager.getquickreset()){
			quickreset.setDrawable(new SpriteDrawable(new Sprite(quickresetonTexture)));
		}
		quickreset.setWidth(width/5);
		quickreset.setHeight(height/7);
		quickreset.setX(width/4);
		quickreset.setY(height-height/4-quickreset.getHeight());
		quickreset.addListener(new ClickListener() {
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		    {
		        settingsManager.togglequickreset();
		        if(settingsManager.getquickreset()){
					quickreset.setDrawable(new SpriteDrawable(new Sprite(quickresetonTexture)));
				}
		        else{
		        	quickreset.setDrawable(new SpriteDrawable(new Sprite(quickresetTexture)));
		        }
		        return true;
		    }
		});
		stage.addActor(quickreset);

		final Image difficulty = new Image(easyTexture);
		if(settingsManager.getdifficulty()==0){
			difficulty.setDrawable(new SpriteDrawable(new Sprite(noobTexture)));
		}
		else if(settingsManager.getdifficulty()==1){
			difficulty.setDrawable(new SpriteDrawable(new Sprite(easyTexture)));
		}
		else if(settingsManager.getdifficulty()==2){
			difficulty.setDrawable(new SpriteDrawable(new Sprite(mediumTexture)));
		}
		else if(settingsManager.getdifficulty()==3){
			difficulty.setDrawable(new SpriteDrawable(new Sprite(hardTexture)));
		}
		else if(settingsManager.getdifficulty()==4){
			difficulty.setDrawable(new SpriteDrawable(new Sprite(proTexture)));
		}
		difficulty.setWidth(width/5);
		difficulty.setHeight(height/7);
		difficulty.setX(width-width/4-difficulty.getWidth());
		difficulty.setY(height-height/4-difficulty.getHeight());
		difficulty.addListener(new ClickListener() {
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		    {
		        settingsManager.toggledifficulty();
		        if(settingsManager.getdifficulty()==0){
					difficulty.setDrawable(new SpriteDrawable(new Sprite(noobTexture)));
				}
				else if(settingsManager.getdifficulty()==1){
					difficulty.setDrawable(new SpriteDrawable(new Sprite(easyTexture)));
				}
				else if(settingsManager.getdifficulty()==2){
					difficulty.setDrawable(new SpriteDrawable(new Sprite(mediumTexture)));
				}
				else if(settingsManager.getdifficulty()==3){
					difficulty.setDrawable(new SpriteDrawable(new Sprite(hardTexture)));
				}
				else if(settingsManager.getdifficulty()==4){
					difficulty.setDrawable(new SpriteDrawable(new Sprite(proTexture)));
				}
		        return true;
		    }
		});
		stage.addActor(difficulty);

		Image menu = new Image(menuTexture);
		menu.setWidth(width/5);
		menu.setHeight(height/7);
		menu.setX(width/4);
		menu.setY(height-height/4-menu.getHeight()-quickreset.getHeight()-quickreset.getHeight()/2);
		menu.addListener(new ClickListener() {
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		    {
		        g.setScreen(new MainMenuScreen(g, nface));
		        return true;
		    }
		});
		stage.addActor(menu);

		Image about = new Image(aboutTexture);
		about.setWidth(width/5);
		about.setHeight(height/7);
		about.setX(width-width/4-difficulty.getWidth());
		about.setY(height-height/4-about.getHeight()-difficulty.getHeight()-difficulty.getHeight()/2);
		about.addListener(new ClickListener() {
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		    {
		        g.setScreen(new AboutScreen(g, nface));
		        return true;
		    }
		});
		stage.addActor(about);

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
		quickresetTexture.dispose();
		quickresetonTexture.dispose();
		noobTexture.dispose();
		easyTexture.dispose();
		mediumTexture.dispose();
		hardTexture.dispose();
		proTexture.dispose();
		menuTexture.dispose();
		aboutTexture.dispose();
		batch.dispose();
		stage.dispose();
		this.dispose();
	}

}
