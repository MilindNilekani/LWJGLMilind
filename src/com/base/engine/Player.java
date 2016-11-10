package com.base.engine;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

public class Player {
	
	public static final float GUN_SCALE = 0.06f;
	public static final float GUN_SIZEY = GUN_SCALE;
	public static final float GUN_SIZEX = (float)((double)GUN_SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float GUN_START = 0;
	
	public static final float UI_SCALE = 0.01f;
	public static final float UI_SIZEY = UI_SCALE;
	public static final float UI_SIZEX = (float)((double)UI_SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float UI_START = 0;
	
	public static final float SHOOT_DISTANCE=100.0f;

	public static final float OFFSET_X = 0.0f;
	public static final float OFFSET_Y = 0.0f;

	public static final float TEX_MIN_X = -OFFSET_X;
	public static final float TEX_MAX_X = -1 - OFFSET_X;
	public static final float TEX_MIN_Y = -OFFSET_Y;
	public static final float TEX_MAX_Y = 1 - OFFSET_Y;
	
	public static final int MIN_DAMAGE=20;
	public static final int MAX_DAMAGE=40;

	public static final int MAX_HEALTH=100;
	public static final int MAX_AMMO=10000000;
	
	private Camera camera;
	private Random random;
	private Mesh gunMesh;
	private Transform gunTransform;
	private Shader gunShader;
	private Material gunMaterial;
	private static boolean mouseLocked=true;
	private Vector2f centerPosition=new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private Vector3f movement;
	private int health;
	private int ammo;
	
	
	private static ArrayList<Texture> numbersUI;
	
	//Health units digit
	private Mesh healthUnitsMesh;
	private Transform healthUnitsTransform;
	private Shader healthUnitsShader;
	private Material healthUnitsMaterial;
	
	//Health tens digit
		private Mesh healthTensMesh;
		private Transform healthTensTransform;
		private Shader healthTensShader;
		private Material healthTensMaterial;
	
	public Player(Vector3f position)
	{
		ammo=MAX_AMMO;
		if(numbersUI==null)
		{
			numbersUI=new ArrayList<Texture>();
			numbersUI.add(ResourceLoader.loadTexture("0.png"));
			numbersUI.add(ResourceLoader.loadTexture("1.png"));
			numbersUI.add(ResourceLoader.loadTexture("2.png"));
			numbersUI.add(ResourceLoader.loadTexture("3.png"));
			
			numbersUI.add(ResourceLoader.loadTexture("4.png"));
			numbersUI.add(ResourceLoader.loadTexture("5.png"));
			numbersUI.add(ResourceLoader.loadTexture("6.png"));
			
			numbersUI.add(ResourceLoader.loadTexture("7.png"));
			numbersUI.add(ResourceLoader.loadTexture("8.png"));
			numbersUI.add(ResourceLoader.loadTexture("9.png"));
		}
		random=new Random();
		camera=new Camera(position, new Vector3f(0,0,1), new Vector3f(0,1,0));
		Input.setCursor(false);
		health=MAX_HEALTH;
		
		//Gun stuff
		gunTransform=new Transform();
		gunShader=new BasicShader();
		gunTransform.setTranslation(position);
		gunMaterial=new Material(ResourceLoader.loadTexture("PISGB0.png"));
		gunMesh=new Mesh();

		
		Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-GUN_SIZEX,GUN_START,GUN_START), new Vector2f(TEX_MAX_X,TEX_MAX_Y)),
										 new Vertex(new Vector3f(-GUN_SIZEX,GUN_SIZEY,GUN_START), new Vector2f(TEX_MAX_X,TEX_MIN_Y)),
										 new Vertex(new Vector3f(GUN_SIZEX,GUN_SIZEY,GUN_START), new Vector2f(TEX_MIN_X,TEX_MIN_Y)),
										 new Vertex(new Vector3f(GUN_SIZEX,GUN_START,GUN_START), new Vector2f(TEX_MIN_X,TEX_MAX_Y))};

		int[] indices = new int[]{0,1,2,
								  0,2,3};

		gunMesh.addVertices(vertices, indices);
		
		//Health Units
		healthUnitsTransform=new Transform();
		healthUnitsShader=new BasicShader();
		healthUnitsTransform.setTranslation(position);
		healthUnitsMaterial=new Material(numbersUI.get(0));
		healthUnitsMesh=new Mesh();

		
		vertices = new Vertex[]{new Vertex(new Vector3f(-UI_SIZEX,UI_START,UI_START), new Vector2f(TEX_MAX_X,TEX_MAX_Y)),
										 new Vertex(new Vector3f(-UI_SIZEX,UI_SIZEY,UI_START), new Vector2f(TEX_MAX_X,TEX_MIN_Y)),
										 new Vertex(new Vector3f(UI_SIZEX,UI_SIZEY,UI_START), new Vector2f(TEX_MIN_X,TEX_MIN_Y)),
										 new Vertex(new Vector3f(UI_SIZEX,UI_START,UI_START), new Vector2f(TEX_MIN_X,TEX_MAX_Y))};

		indices = new int[]{0,1,2,
								  0,2,3};

		healthUnitsMesh.addVertices(vertices, indices);
		
		//Health Tens
				healthTensTransform=new Transform();
				healthTensShader=new BasicShader();
				healthTensTransform.setTranslation(position);
				healthTensMaterial=new Material(numbersUI.get(0));
				healthTensMesh=new Mesh();

				
				vertices = new Vertex[]{new Vertex(new Vector3f(-UI_SIZEX,UI_START,UI_START), new Vector2f(TEX_MAX_X,TEX_MAX_Y)),
												 new Vertex(new Vector3f(-UI_SIZEX,UI_SIZEY,UI_START), new Vector2f(TEX_MAX_X,TEX_MIN_Y)),
												 new Vertex(new Vector3f(UI_SIZEX,UI_SIZEY,UI_START), new Vector2f(TEX_MIN_X,TEX_MIN_Y)),
												 new Vertex(new Vector3f(UI_SIZEX,UI_START,UI_START), new Vector2f(TEX_MIN_X,TEX_MAX_Y))};

				indices = new int[]{0,1,2,
										  0,2,3};

				healthTensMesh.addVertices(vertices, indices);
	}
	
	public void damage(int dmg)
	{
		health-=dmg;
		if(health>MAX_HEALTH)
			health=MAX_HEALTH;
		if(health<=0)
		{
			health=0;
			System.out.println("You died");
			Game.setIsRunning(false);
		}
	}
	
	public void gainAmmo(int val)
	{
		ammo+=val;
		if(ammo>MAX_AMMO)
			ammo=MAX_AMMO;
	}
	
	public int getDamage()
	{
		return random.nextInt(MAX_DAMAGE-MIN_DAMAGE)+MIN_DAMAGE;
	}
	
	public void input()
	{
		float sen=0.2f;
		
		if(Input.getKey(Keyboard.KEY_ESCAPE))
		{
			Input.setCursor(true);
			mouseLocked=false;
		}
		
		if(Input.getMouseDown(0))
		{
			if(!mouseLocked)
			{
				Input.setMousePosition(centerPosition);
				Input.setCursor(false);
				mouseLocked=true;
			}
			else
			{
				if(ammo>0)
				{
					Vector2f lineStart=new Vector2f(camera.getPos().getX(),camera.getPos().getZ());
					Vector2f dir=new Vector2f(camera.getForward().getX(), camera.getForward().getZ()).normalizeIntoUnitVector();
					Vector2f lineEnd=lineStart.add(dir.multiply(SHOOT_DISTANCE));
				
					Game.getLevel().checkCollisionOfBullet(lineStart, lineEnd,true);
					ammo--;
				}
			}
		}
		
		movement=new Vector3f(0,0,0);
		
		
		if(Input.getKey(Keyboard.KEY_W))
			movement=movement.add(camera.getForward());
		if(Input.getKey(Keyboard.KEY_S))
			movement=movement.subtract(camera.getForward());
		if(Input.getKey(Keyboard.KEY_A))
			movement=movement.add(camera.getLeft());
		if(Input.getKey(Keyboard.KEY_D))
			movement=movement.add(camera.getRight());
		
		
		if(mouseLocked)
		{
			Vector2f deltaPos=Input.getMousePosition().subtract(centerPosition);
			
			boolean rotY=deltaPos.getX()!=0;
			boolean rotX=deltaPos.getY()!=0;
			
			if(rotY)
				camera.rotateY(deltaPos.getX()*sen);
			//if(rotX)
			//	camera.rotateX(-deltaPos.getY()*sen);
			if(rotX || rotY)
				Input.setMousePosition(new Vector2f(Window.getWidth()/2, Window.getHeight()/2));
		}
	}
	
	public void update()
	{
		
		float movAmt=2*(float)(Time.getDelta());
		movement.setY(0);
		if(movement.length()>0)
			movement=movement.normalizeIntoUnitVector();
		
		
		Vector3f oldPos=camera.getPos();
		Vector3f newPos=oldPos.add(movement.multiply(movAmt));
		Vector3f collision=Game.getLevel().checkCollision(oldPos, newPos, 0.2f, 0.2f);
		movement=movement.multiply(collision);
		if(movement.length()>0)
			camera.move(movement, movAmt);
		
		//Gun stuff
		gunTransform.setTranslation(camera.getPos().add(camera.getForward().multiply(0.105f)));
		gunTransform.getTranslation().setY(gunTransform.getTranslation().getY()-0.0740f);
		Vector3f dirToCamera = Transform.getCamera().getPos().subtract(gunTransform.getTranslation());
		float angleCamera=(float)Math.toDegrees(Math.atan(dirToCamera.getZ()/dirToCamera.getX()));
		if(dirToCamera.getX() < 0)
			angleCamera+=180;
		gunTransform.getRotation().setY(angleCamera+90);
		
		//Health units stuff
		int healthUnits=health%10;
		healthUnitsMaterial.setTexture(numbersUI.get(healthUnits));
		healthUnitsTransform.setTranslation(camera.getPos().add(camera.getForward().multiply(0.105f).add(camera.getLeft().multiply(0.105f))));
		healthUnitsTransform.getTranslation().setY(healthUnitsTransform.getTranslation().getY()-0.0640f);
		dirToCamera = Transform.getCamera().getPos().subtract(healthUnitsTransform.getTranslation());
		angleCamera=(float)Math.toDegrees(Math.atan(dirToCamera.getZ()/dirToCamera.getX()));
		if(dirToCamera.getX() < 0)
			angleCamera+=180;
		healthUnitsTransform.getRotation().setY(angleCamera+90);
		
		//Health tens stuff
		int healthTens=(health/10)%10;
		healthTensMaterial.setTexture(numbersUI.get(healthTens));
		healthTensTransform.setTranslation(camera.getPos().add(camera.getForward().multiply(0.105f).add(camera.getLeft().multiply(0.113f))));
		healthTensTransform.getTranslation().setY(healthTensTransform.getTranslation().getY()-0.0645f);
		dirToCamera = Transform.getCamera().getPos().subtract(healthTensTransform.getTranslation());
		angleCamera=(float)Math.toDegrees(Math.atan(dirToCamera.getZ()/dirToCamera.getX()));
		if(dirToCamera.getX() < 0)
			angleCamera+=180;
		healthTensTransform.getRotation().setY(angleCamera+90);
	}
	
	public void render()
	{
		gunShader.bind();
		gunShader.updateUniforms(gunTransform.getTransformation(), gunTransform.getProjectedTransformation(), gunMaterial);
		gunMesh.draw();
		
		healthTensShader.bind();
		healthTensShader.updateUniforms(healthTensTransform.getTransformation(), healthTensTransform.getProjectedTransformation(), healthTensMaterial);
		healthTensMesh.draw();
		
		healthUnitsShader.bind();
		healthUnitsShader.updateUniforms(healthUnitsTransform.getTransformation(), healthUnitsTransform.getProjectedTransformation(), healthUnitsMaterial);
		healthUnitsMesh.draw();
		
		
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
			

}
