package com.base.engine;

import java.util.Random;

import org.lwjgl.input.Keyboard;

public class Player {
	
	public static final float SCALE = 0.0325f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float)((double)SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float START = 0;
	
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
	
	private Camera camera;
	private Random random;
	private Mesh mesh;
	private Transform transform;
	private Shader shader;
	private Material material;
	boolean mouseLocked=true;
	private Vector2f centerPosition=new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private Vector3f movement;
	private int health;
	
	public Player(Vector3f position)
	{
		random=new Random();
		camera=new Camera(position, new Vector3f(0,0,1), new Vector3f(0,1,0));
		Input.setCursor(false);
		health=MAX_HEALTH;
		transform=new Transform();
		shader=new BasicShader();
		transform.setTranslation(position);
		material=new Material(ResourceLoader.loadTexture("PISGB0.png"));
		mesh=new Mesh();

		
		Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX,START,START), new Vector2f(TEX_MAX_X,TEX_MAX_Y)),
										 new Vertex(new Vector3f(-SIZEX,SIZEY,START), new Vector2f(TEX_MAX_X,TEX_MIN_Y)),
										 new Vertex(new Vector3f(SIZEX,SIZEY,START), new Vector2f(TEX_MIN_X,TEX_MIN_Y)),
										 new Vertex(new Vector3f(SIZEX,START,START), new Vector2f(TEX_MIN_X,TEX_MAX_Y))};

		int[] indices = new int[]{0,1,2,
								  0,2,3};

		mesh.addVertices(vertices, indices);
	}
	
	public void damage(int dmg)
	{
		health-=dmg;
		if(health>MAX_HEALTH)
			health=MAX_HEALTH;
		if(health<=0)
		{
			System.out.println("You died");
			Game.setIsRunning(false);
		}
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
				Vector2f lineStart=new Vector2f(camera.getPos().getX(),camera.getPos().getZ());
				Vector2f dir=new Vector2f(camera.getForward().getX(), camera.getForward().getZ()).normalizeIntoUnitVector();
				Vector2f lineEnd=lineStart.add(dir.multiply(SHOOT_DISTANCE));
				
				Game.getLevel().checkCollisionOfBullet(lineStart, lineEnd,true);
			}
		}
		
		movement=new Vector3f(0,0,0);
		
		
		if(Input.getKey(Keyboard.KEY_W))
			movement=movement.add(camera.getForward());//camera.move(camera.getForward(), movAmt);
		if(Input.getKey(Keyboard.KEY_S))
			movement=movement.subtract(camera.getForward());//camera.move(camera.getForward(), -movAmt);
		if(Input.getKey(Keyboard.KEY_A))
			movement=movement.add(camera.getLeft());//camera.move(camera.getLeft(), movAmt);
		if(Input.getKey(Keyboard.KEY_D))
			movement=movement.add(camera.getRight());//camera.move(camera.getRight(), movAmt);
		
		
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
		
		transform.setTranslation(camera.getPos().add(camera.getForward().multiply(0.105f)));
		transform.getTranslation().setY(transform.getTranslation().getY()-0.0740f);
		Vector3f dirToCamera = Transform.getCamera().getPos().subtract(transform.getTranslation());
		float angleCamera=(float)Math.toDegrees(Math.atan(dirToCamera.getZ()/dirToCamera.getX()));
		if(dirToCamera.getX() < 0)
			angleCamera+=180;
		transform.getRotation().setY(angleCamera+90);
	}
	
	public void render()
	{
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
			

}
