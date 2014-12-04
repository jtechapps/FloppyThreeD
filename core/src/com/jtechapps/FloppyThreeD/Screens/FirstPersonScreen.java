package com.jtechapps.FloppyThreeD.Screens;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithmConstructionInfo;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.badlogic.gdx.physics.bullet.collision.btSphereBoxCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.jtechapps.FloppyThreeD.NativeInterface;

public class FirstPersonScreen implements Screen, InputProcessor {
	public PerspectiveCamera camera;
	private float width;
	private float height;
	private ModelBatch modelBatch;
	private Model model;//create 500*5*500 floor
	private Model pipemodel;
	private Array<ModelInstance> instances;
	private Array<ModelInstance> pipeinstances;
	private Array<ModelInstance> toppipeinstances;
	private Environment environment;
	ModelInstance playerinstance;
	private int blockscale = 5;//pipe height max 65 or 75
	private float pipespeed = 0.5f;
	private float gravity = -65.0f;
	private float playerforce = 0.0f;
	boolean collision;
	private boolean touched;
	private boolean dead;
	//physics
	btCollisionShape groundShape;
    btCollisionShape ballShape;
    btCollisionShape pipeShape;
    btCollisionObject groundObject;
    btCollisionObject ballObject;
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    private Array<btCollisionObject> pipecollisions;
    private Array<btCollisionObject> toppipecollisions;
    Game g;
    private Sound dieSound;
    private Sound flopSound;
    private Sound scoreSound;
    private int score = 0;
    private NativeInterface nface;
    private SpriteBatch batch;
    private Texture backgroundone;
    private float bgonex = 0;
    private Texture backgroundtwo;
    private float bgtwox;//look in show()
    //label and gui
    private Stage stage;
    private LabelStyle labelstyle;
    private Label counter;
    private float playerangle = 0.0f;
    private float playeranglemove = 1.0f;
    
    public FirstPersonScreen(Game game, NativeInterface nativeInterface){
    	g = game;
    	nface = nativeInterface;
    }

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		//render 2d background
		batch.begin();
		batch.draw(backgroundone, bgonex, 0, width, height);
		batch.draw(backgroundtwo, bgtwox, 0, width, height);
		if(touched && !dead){
			bgonex-=1;
			bgtwox-=1;
		}
		if(bgonex<=-width){
			bgonex = width;
		}
		if(bgtwox<=-width){
			bgtwox = width;
		}
		batch.end();
		modelBatch.begin(camera);
		for (ModelInstance minstance : instances) {
			//modelBatch.render(minstance, environment);//ground is now invisible
		}
		for (int arrayint = 0; arrayint < pipeinstances.size; arrayint++){
			if(touched && !dead){
				pipeinstances.get(arrayint).transform.translate(pipespeed, 0, 0);
				pipecollisions.get(arrayint).setWorldTransform(pipeinstances.get(arrayint).transform);
				collision = checkCollision(pipecollisions.get(arrayint));
				if(collision){
					playerdie();
				}
			}
			modelBatch.render(pipeinstances.get(arrayint), environment);
		}
		
		for (int arrayint = 0; arrayint < toppipeinstances.size; arrayint++) {
			if(touched && !dead){
				toppipeinstances.get(arrayint).transform.translate(pipespeed, 0, 0);
				toppipecollisions.get(arrayint).setWorldTransform(toppipeinstances.get(arrayint).transform);
				collision = checkCollision(toppipecollisions.get(arrayint));
				if(collision){
					playerdie();
				}
			}
			modelBatch.render(toppipeinstances.get(arrayint), environment);
		}
		//check if pipes moved off screen.
		
		Iterator<ModelInstance> iter = pipeinstances.iterator();
		while(iter.hasNext()) {
			ModelInstance pipe = iter.next();
			Vector3 tmp = new Vector3();
			pipe.transform.getTranslation(tmp);
			float x = tmp.x;
			
			Vector3 playertmp = new Vector3();
			playerinstance.transform.getTranslation(playertmp);
			float playerx = playertmp.x;
			
			if(x == 50*blockscale){//middle
				spawnpipes(pipeinstances, toppipeinstances);
			}
			
			if(x == playerx){//add score
				if(!dead)
					addscore();
			}
				
	        if(x >= 60*blockscale){//left side
	        	iter.remove();
	        }
		}
		Iterator<ModelInstance> iter2 = toppipeinstances.iterator();
		while(iter2.hasNext()) {
			ModelInstance pipe = iter2.next();
			Vector3 tmp = new Vector3();
			pipe.transform.getTranslation(tmp);
			float x = tmp.x;
				
	        if(x >= 60*blockscale){//left side
	        	iter2.remove();
	        }
		}
		Iterator<btCollisionObject> iter3 = pipecollisions.iterator();//delete pipe collisions if they go off screen.
		while(iter3.hasNext()) {
			btCollisionObject pipe = iter3.next();
			Vector3 tmp = new Vector3();
			pipe.getWorldTransform().getTranslation(tmp);
			float x = tmp.x;
				
	        if(x >= 60*blockscale){//left side
	        	iter3.remove();
	        }
		}
		Iterator<btCollisionObject> iter4 = toppipecollisions.iterator();//delete pipe collisions if they go off screen.
		while(iter4.hasNext()) {
			btCollisionObject pipe = iter4.next();
			Vector3 tmp = new Vector3();
			pipe.getWorldTransform().getTranslation(tmp);
			float x = tmp.x;
				
	        if(x >= 60*blockscale){//left side
	        	iter4.remove();
	        }
		}
		//player physics
		if(!collision && touched && !dead){
			//gravity
			playerinstance.transform.rotate(0, 0, 1, -playerangle);
			playerinstance.transform.translate(0, gravity*delta, 0);
			playerinstance.transform.rotate(0, 0, 1, playerangle);
			//jumps
			float playerforcetoapply = playerforce*delta*2;
			playerforce-=playerforcetoapply;
			if(playerforce<=0){
				playerforce = 0.0f;
			}
			playerinstance.transform.rotate(0, 0, 1, -playerangle);
			playerinstance.transform.translate(0, playerforcetoapply/2, 0);
			playerinstance.transform.rotate(0, 0, 1, playerangle);
			ballObject.setWorldTransform(playerinstance.transform);
			if(playerforcetoapply/2>Math.abs(gravity*delta)){
				if(playerangle>=50.0f)
				{
					playerinstance.transform.rotate(0, 0, 1, -playeranglemove);
					playerangle-=playeranglemove;
				}
				else if(playerangle<=-50.0f)
				{
					playerinstance.transform.rotate(0, 0, 1, playeranglemove*4);
					playerangle+=playeranglemove*4;
				}
				else {
					playerinstance.transform.rotate(0, 0, 1, -playeranglemove*3);
					playerangle+=-playeranglemove*3;
				}
				playerinstance.calculateTransforms();
			}
			else {
				if(playerangle>=50.0f)
				{
					playerinstance.transform.rotate(0, 0, 1, -playeranglemove);
					playerangle-=playeranglemove;
				}
				else if(playerangle<=-50.0f)
				{
					playerinstance.transform.rotate(0, 0, 1, playeranglemove*2);
					playerangle+=playeranglemove*2;
				}
				else {
					playerinstance.transform.rotate(0, 0, 1, 2*playeranglemove);
					playerangle+=2*playeranglemove;
				}
				playerinstance.calculateTransforms();
			}
			
			collision = checkCollision();
			if(collision){
				playerdie();
			}
			Vector3 playertmppos = new Vector3();
			playerinstance.transform.rotate(0, 0, 1, -playerangle);
			playerinstance.transform.getTranslation(playertmppos);
			camera.position.set(playertmppos);
			camera.lookAt(40.0f*blockscale, playertmppos.y, 10.0f*blockscale);
			camera.update();
			playerinstance.transform.rotate(0, 0, 1, playerangle);
			if(playertmppos.y >= 90){
				playerdie();
			}
		}
		else if(touched && dead && collision){
			playerinstance.transform.rotate(0, 0, 1, -playerangle);
			playerinstance.transform.translate(0, gravity*delta, 0);
			playerinstance.transform.rotate(0, 0, 1, playerangle);
		}
		modelBatch.render(playerinstance, environment);
		modelBatch.end();
		stage.draw();
	}
	
	private void addscore(){
		score++;
		counter.setText(""+score);
		counter.setPosition(width/2-counter.getWidth()/2, height-height/5);
		scoreSound.play();
		Gdx.app.log("score ", ""+score);
	}
	
	private void playerdie(){
		//play sound and wait
		dieSound.play();
		dead = true;
		Timer.schedule(new Task(){
		    @Override
		    public void run() {
		        g.setScreen(new MainMenuScreen(g, nface, score));
		    }
		}, 1);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		Bullet.init();
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		camera = new PerspectiveCamera(67, 640*2, 480*2);
		camera.position.set(53.0f*blockscale, 40.0f, 10.0f*blockscale);
		camera.lookAt(40.0f*blockscale, 40.0f, 10.0f*blockscale);
		camera.near = 1f;
		camera.far = 300f;
		camera.update();
		modelBatch = new ModelBatch();
		instances = new Array<ModelInstance>();
		pipeinstances = new Array<ModelInstance>();
		toppipeinstances = new Array<ModelInstance>();
		pipecollisions = new Array<btCollisionObject>();
		toppipecollisions = new Array<btCollisionObject>();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
				0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f,
				-0.8f, -0.2f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f,
				-0.8f, -0.2f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f,
				0.8f, -0.2f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f,
				-0.8f, 2f));
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
				40.0f*blockscale, 50.0f, 10.0f*blockscale));
		environment.set(new ColorAttribute(ColorAttribute.createSpecular(0.5f, 0.5f, 0.5f, 1f)));

		//physics
		ballShape = new btSphereShape(4.0f);
		pipeShape = new btCylinderShape(new Vector3(5.0f, 75.0f/2, 5.0f));
        groundShape = new btBoxShape(new Vector3(50.0f*blockscale, 0.5f*blockscale, 50.0f*blockscale));
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        
        //for loading models
        if(nface.getAssetManger()==null){
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
		
		spawnfloor();
		pipemodel = nface.getAssetManger().get("models/pipe.g3db",Model.class);
		spawnpipes(pipeinstances, toppipeinstances);
		spawnplayer();
        groundObject = new btCollisionObject();
        groundObject.setCollisionShape(groundShape);
        groundObject.setWorldTransform(instances.peek().transform); 
         
        ballObject = new btCollisionObject();
        ballObject.setCollisionShape(ballShape);
        ballObject.setWorldTransform(playerinstance.transform);    
        
        //sounds
        flopSound = Gdx.audio.newSound(Gdx.files.internal("sounds/switch.wav"));
        dieSound = Gdx.audio.newSound(Gdx.files.internal("sounds/die.wav"));
        scoreSound = Gdx.audio.newSound(Gdx.files.internal("sounds/point.wav"));
        
        //2d textures
        batch = new SpriteBatch();
        backgroundone = new Texture("img/backgroundonev2.png");
        backgroundtwo = new Texture("img/backgroundtwov2.png");
        bgtwox = width;
        
        //stage with labels
        stage = new Stage();
        labelstyle = new LabelStyle();
		labelstyle.font=nface.getFont1();
		labelstyle.fontColor = Color.BLACK;
		counter = new Label(""+score, labelstyle);
		counter.setPosition(width/2-counter.getWidth()/2, height-height/5);
		stage.addActor(counter);
        
		Gdx.input.setInputProcessor(this);
	}
	
	public void spawnfloor(){
		ModelBuilder modelBuilder = new ModelBuilder();
		Model model;
		model = modelBuilder.createBox(blockscale*100, blockscale, blockscale*100, new Material(
				ColorAttribute.createDiffuse(Color.CLEAR)),
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		ModelInstance instance = new ModelInstance(model);
		instance.transform.translate(blockscale*50, -20.0f, blockscale*50);
		instances.add(instance);
	}
	
	public void spawnCubeArray(Array<ModelInstance> cubeinstances){//not used now but has cool effect
		for (int z = 0; z < 100; z++) {
			for (int x = 0; x < 100; x++) {
				ModelBuilder modelBuilder = new ModelBuilder();
				Model model;
				if (x % 2 == 0) {
					if(z % 2 == 0){
					model = modelBuilder.createBox(blockscale, blockscale, blockscale, new Material(
							ColorAttribute.createDiffuse(Color.RED)),
							Usage.Position | Usage.Normal);
					}
					else {
						model = modelBuilder.createBox(blockscale, blockscale, blockscale, new Material(
								ColorAttribute.createDiffuse(Color.GREEN)),
								Usage.Position | Usage.Normal);
					}
				} else {
					if(z % 2 == 0){
					model = modelBuilder.createBox(blockscale, blockscale, blockscale, new Material(
							ColorAttribute.createDiffuse(Color.GREEN)),
							Usage.Position | Usage.Normal);
					}
					else{
						model = modelBuilder.createBox(blockscale, blockscale, blockscale, new Material(
								ColorAttribute.createDiffuse(Color.RED)),
								Usage.Position | Usage.Normal);
					}
				}
				ModelInstance instance = new ModelInstance(model);
				instance.transform.translate(x * blockscale, 0, z*blockscale);
				cubeinstances.add(instance);
			}
		}
	}
	
	public void spawnplayer(){
		model = nface.getAssetManger().get("models/bird.g3db",Model.class);
		playerinstance = new ModelInstance(model);
		playerinstance.transform.translate(53.0f*blockscale, 40.0f, 10.0f*blockscale);
	}
	
	public void spawnpipes(Array<ModelInstance> pinstances, Array<ModelInstance> tinstances){
		Random rn = new Random();
		int random = rn.nextInt(29) + 1;
		ModelInstance bottompipeinstance = new ModelInstance(pipemodel);
		ModelInstance toppipeinstance = new ModelInstance(pipemodel);
		bottompipeinstance.transform.translate(40.0f*blockscale, -5.0f-random, 10.0f*blockscale);
		toppipeinstance.transform.translate(40.0f*blockscale, 95.0f-random, 10.0f*blockscale);
		pinstances.add(bottompipeinstance);
		tinstances.add(toppipeinstance);
		btCollisionObject bottompipeObject = new btCollisionObject();
	    bottompipeObject.setCollisionShape(pipeShape);
	    bottompipeObject.setWorldTransform(bottompipeinstance.transform);
	    btCollisionObject toppipeObject = new btCollisionObject();
	    toppipeObject.setCollisionShape(pipeShape);
	    toppipeObject.setWorldTransform(toppipeinstance.transform);
	    pipecollisions.add(bottompipeObject);
	    toppipecollisions.add(toppipeObject);
	}
	
	private void jump(){
		if(!touched)
			touched=true;
		if(!dead){
			playerforce+=100.0f;
			flopSound.play();
		}
		else {
			//g.setScreen(new ClassicGameScreen(g, nface));//for reset settings later
		}
	}

	boolean checkCollision() {// thanks blogs.xoppa.com for bullet physics	
		CollisionObjectWrapper co0 = new CollisionObjectWrapper(ballObject);
		CollisionObjectWrapper co1 = new CollisionObjectWrapper(groundObject);
	     
		btCollisionAlgorithmConstructionInfo ci = new btCollisionAlgorithmConstructionInfo();
		ci.setDispatcher1(dispatcher);
		btCollisionAlgorithm algorithm = new btSphereBoxCollisionAlgorithm(null, ci, co0.wrapper, co1.wrapper, false); 
 
		btDispatcherInfo info = new btDispatcherInfo();
		btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);
	     
		algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);
	     
		boolean r = result.getPersistentManifold().getNumContacts() > 0;
	    
		result.dispose();
		info.dispose();
		algorithm.dispose();
		ci.dispose();
		co1.dispose();
		co0.dispose();
	 
	    return r;
	}
	
	boolean checkCollision(btCollisionObject colObject) {// thanks blogs.xoppa.com for bullet physics	
		CollisionObjectWrapper co0 = new CollisionObjectWrapper(ballObject);
		CollisionObjectWrapper co1 = new CollisionObjectWrapper(colObject);
	     
		btCollisionAlgorithmConstructionInfo ci = new btCollisionAlgorithmConstructionInfo();
		ci.setDispatcher1(dispatcher);
		btCollisionAlgorithm algorithm = new btSphereBoxCollisionAlgorithm(null, ci, co0.wrapper, co1.wrapper, false); 
 
		btDispatcherInfo info = new btDispatcherInfo();
		btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);
	     
		algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);
	     
		boolean r = result.getPersistentManifold().getNumContacts() > 0;
	    
		result.dispose();
		info.dispose();
		algorithm.dispose();
		ci.dispose();
		co1.dispose();
		co0.dispose();
	 
	    return r;
	}
	
	@Override
	public void hide() {

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		model.dispose();
		pipemodel.dispose();
		modelBatch.dispose();
		groundObject.dispose();
	    groundShape.dispose();	     
	    ballObject.dispose();
	    ballShape.dispose();
	    pipeShape.dispose();
	    dispatcher.dispose();
	    collisionConfig.dispose();
	    dieSound.dispose();
	    flopSound.dispose();
	    scoreSound.dispose();
	    nface.garbagecollect();
	    backgroundone.dispose();
	    backgroundtwo.dispose();
	    batch.dispose();
	    stage.dispose();
	    labelstyle.font.dispose();
		this.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.SPACE){
			jump();
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		jump();
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
