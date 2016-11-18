package com.base.engine;

import java.util.ArrayList;
import javax.sound.sampled.Clip;

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
	
	private static final float GUN_FIRE_ANIMATIONTIME = 0.3f;
	
	public static final int MIN_DAMAGE=20;
	public static final int MAX_DAMAGE=40;

	public static final int MAX_HEALTH=100;
	public static final int MAX_AMMO=50;
	
	private Camera camera;
	private Random random;
	private Mesh gunMesh;
	private Transform gunTransform;
	private Shader gunShader;
	private Material gunMaterial, gunFireMaterial;
	private static boolean mouseLocked=false;
	private Vector2f centerPosition=new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private Vector3f movement;
	private int health;
	private int ammo;
	private double gunFireTime;
	
	private UI healthTens, healthUnits, healthHundreds;
	private UI ammoTens, ammoUnits;
	
	public Player(Vector3f position)
	{
		//Initiliaze player values
		ammo=MAX_AMMO;
		health=MAX_HEALTH;
		
		//Initialize objects for classes
		random=new Random();
		camera=new Camera(position, new Vector3f(0,0,1), new Vector3f(0,1,0));
		Input.setCursor(false);

		gunFireTime=0;
		gunFireMaterial = new Material(ResourceLoader.loadTexture("PISFA0.png"));
		
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
		
		healthUnits=new UI(0.105f, -0.0640f,position);
		healthTens=new UI(0.113f,-0.0645f,position);
		healthHundreds=new UI(0.121f,-0.0650f,position);
		
		ammoTens=new UI(-0.105f,-0.0645f,position);
		ammoUnits=new UI(-0.113f,-0.0650f,position);
		movement=new Vector3f(0,0,0);
		
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
	
	public int getHealth()
	{
		return health;
	}
	
	public int getMaxHealth()
	{
		return MAX_HEALTH;
	}
	
	public int getAmmo()
	{
		return ammo;
	}
	
	public int getMaxAmmo()
	{
		return MAX_AMMO;
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
		float sen=0.1f;
		
		if(Input.getKeyDown(Keyboard.KEY_E))
		{
			//Open exit door
			Game.getLevel().openDoors(camera.getPos());
		}
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
					if((double)Time.getTime()/Time.SECOND - gunFireTime>GUN_FIRE_ANIMATIONTIME)
					{
						Vector2f lineStart=new Vector2f(camera.getPos().getX(),camera.getPos().getZ());
						Vector2f dir=new Vector2f(camera.getForward().getX(), camera.getForward().getZ()).normalizeIntoUnitVector();
						Vector2f lineEnd=lineStart.add(dir.multiply(SHOOT_DISTANCE));
				
						Game.getLevel().checkCollisionOfBullet(lineStart, lineEnd,true);
						gunFireTime=(double)Time.getTime()/Time.SECOND;
						ammo--;
					}
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
			if(rotX || rotY)
				Input.setMousePosition(new Vector2f(Window.getWidth()/2, Window.getHeight()/2));
		}
	}
	
	public void update()
	{
		//-----------------Collision--------------//
		float movAmt=3*(float)(Time.getDelta());
		movement.setY(0);
		if(movement.length()>0)
			movement=movement.normalizeIntoUnitVector();
		
		Vector3f oldPos=camera.getPos();
		Vector3f newPos=oldPos.add(movement.multiply(movAmt));
		Vector3f collision=Game.getLevel().checkCollision(oldPos, newPos, 0.2f, 0.2f);
		movement=movement.multiply(collision);
		if(movement.length()>0)
			camera.move(movement, movAmt);
		
		//--------------------Gun------------------//
		gunTransform.setTranslation(camera.getPos().add(camera.getForward().multiply(0.105f).add(camera.getLeft().multiply(0.010f))));
		gunTransform.getTranslation().setY(gunTransform.getTranslation().getY()-0.0740f);
		Vector3f dirToCamera = Transform.getCamera().getPos().subtract(gunTransform.getTranslation());
		float angleCamera=(float)Math.toDegrees(Math.atan(dirToCamera.getZ()/dirToCamera.getX()));
		if(dirToCamera.getX() < 0)
			angleCamera+=180;
		gunTransform.getRotation().setY(angleCamera+90);
		
		
		//-----------------Health----------------------//
		//Health units stuff
		int healthU=health%10;
		healthUnits.setValueUI(healthU);
		healthUnits.update();
		
		//Health tens stuff
		int healthT=(health/10)%10;
		healthTens.setValueUI(healthT);
		healthTens.update();
		
		//Health Hundreds
		int healthH=health/100;
		healthHundreds.setValueUI(healthH);
		healthHundreds.update();
		
		//----------------Ammo--------------------//
		//Ammo tens stuff
		int ammoT=ammo/10;
		ammoTens.setValueUI(ammoT);
		ammoTens.update();
				
		//Ammo Units
		int ammoU=ammo%10;
		ammoUnits.setValueUI(ammoU);
		ammoUnits.update();
	}
	
	public void render()
	{
		//Gun Sprite stuff
		if((double)Time.getTime()/Time.SECOND < gunFireTime + GUN_FIRE_ANIMATIONTIME)
		{
			gunShader.bind();
			gunShader.updateUniforms(gunTransform.getTransformation(), gunTransform.getProjectedTransformation(), gunFireMaterial);
			gunMesh.draw();
		}
		else
		{
			gunShader.bind();
			gunShader.updateUniforms(gunTransform.getTransformation(), gunTransform.getProjectedTransformation(), gunMaterial);
			gunMesh.draw();
		}
		
		//Health stuff
		if(health>99)
			healthHundreds.render();
		healthTens.render();
		healthUnits.render();
		
		//Ammo stuff
		ammoUnits.render();
		ammoTens.render();
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
			

}
