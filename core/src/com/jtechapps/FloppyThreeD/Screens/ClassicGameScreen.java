package com.jtechapps.FloppyThreeD.Screens;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
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
import com.badlogic.gdx.utils.Array;
import com.jtechapps.FloppyThreeD.NativeInterface;

public class ClassicGameScreen implements Screen, InputProcessor {
	public PerspectiveCamera camera;
	private float width;
	private float height;
	public ModelBatch modelBatch;
	public Model model;//create 500*5*500 floor
	public Array<ModelInstance> instances;
	public Array<ModelInstance> pipeinstances;
	public Array<ModelInstance> toppipeinstances;
	public Environment environment;
	public CameraInputController camController;
	ModelInstance playerinstance;
	private int blockscale = 5;//pipe height max 65 or 75
	private float pipespeed = 0.5f;
	private Texture groundTexture;
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
    
    public ClassicGameScreen(Game game, NativeInterface nativeInterface){
    	g = game;
    	nface = nativeInterface;
    }

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(camera);
		for (ModelInstance minstance : instances) {
			modelBatch.render(minstance, environment);
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
			playerinstance.transform.translate(0, gravity*delta, 0);
			//jumps
			float playerforcetoapply = playerforce*delta*2;
			playerforce-=playerforcetoapply;
			if(playerforce<=0){
				playerforce = 0.0f;
			}
			playerinstance.transform.translate(0, playerforcetoapply/2, 0);
			ballObject.setWorldTransform(playerinstance.transform);
			collision = checkCollision();
			if(collision){
				playerdie();
			}
			Vector3 playertmppos = new Vector3();
			playerinstance.transform.getTranslation(playertmppos);
			if(playertmppos.y >= 90){
				playerdie();
			}
		}
		else if(touched && dead && collision){
			playerinstance.transform.translate(0, gravity*delta, 0);
		}
		modelBatch.render(playerinstance, environment);
		modelBatch.end();

	}
	
	private void addscore(){
		score++;
		scoreSound.play();
		Gdx.app.log("score ", ""+score);
	}
	
	private void playerdie(){
		//play sound and wait
		dieSound.play();
		dead = true;
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
		camera.position.set(50.0f*blockscale, 25.0f, 0.0f);
		camera.lookAt(50.0f*blockscale, 25.0f, 50.0f*blockscale);
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
		//environment.set(new ColorAttribute(ColorAttribute.Fog, 1f, 0.1f, 0.1f, 1.0f));

		//physics
		ballShape = new btSphereShape(4.0f);
		pipeShape = new btCylinderShape(new Vector3(5.0f, 75.0f/2, 5.0f));
        groundShape = new btBoxShape(new Vector3(50.0f*blockscale, 0.5f*blockscale, 50.0f*blockscale));
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
		
		spawnfloor();
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
        
		Gdx.input.setInputProcessor(this);
	}
	
	public void spawnfloor(){
		groundTexture = new Texture("img/grass.png");
		ModelBuilder modelBuilder = new ModelBuilder();
		Model model;
		
		/*model = modelBuilder.createBox(blockscale*100, blockscale, blockscale*100, new Material(
				TextureAttribute.createDiffuse(groundTexture)),
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);*/
		model = modelBuilder.createBox(blockscale*100, blockscale, blockscale*100, new Material(
				ColorAttribute.createDiffuse(Color.CLEAR)),
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		ModelInstance instance = new ModelInstance(model);
		instance.transform.translate(blockscale*50, -20.0f, blockscale*50);
		instances.add(instance);
	}
	
	public void spawnCubeArray(Array<ModelInstance> cubeinstances){
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
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createSphere(8.0f, 8.0f, 8.0f, 100, 100, new Material(ColorAttribute.createDiffuse(Color.MAROON)), Usage.Position | Usage.Normal);
		playerinstance = new ModelInstance(model);
		playerinstance.transform.translate(53.0f*blockscale, 40.0f, 10.0f*blockscale);
	}
	
	public void spawnpipes(Array<ModelInstance> pinstances, Array<ModelInstance> tinstances){
		Random rn = new Random();
		int random = rn.nextInt(29) + 1;
		ModelBuilder modelBuilder = new ModelBuilder();
		Model model;
		model = modelBuilder.createCylinder(10.0f, 75.0f, 10.0f, 100, new Material(
				ColorAttribute.createDiffuse(Color.MAGENTA)), Usage.Position | Usage.Normal);
		ModelInstance bottompipeinstance = new ModelInstance(model);
		ModelInstance toppipeinstance = new ModelInstance(model);
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
			g.setScreen(new ClassicGameScreen(g, nface));
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
		modelBatch.dispose();
		groundTexture.dispose();
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
