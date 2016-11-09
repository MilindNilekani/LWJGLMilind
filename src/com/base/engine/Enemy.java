package com.base.engine;

import java.util.ArrayList;
import java.util.Random;

public class Enemy
{
	public static final float SCALE = 0.7f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float)((double)SIZEY / (1.9310344827586206896551724137931 * 2.0));
	public static final float START = 0;

	public static final float OFFSET_X = 0.0f;
	public static final float OFFSET_Y = 0.0f;

	public static final float TEX_MIN_X = -OFFSET_X;
	public static final float TEX_MAX_X = -1 - OFFSET_X;
	public static final float TEX_MIN_Y = -OFFSET_Y;
	public static final float TEX_MAX_Y = 1 - OFFSET_Y;
	
	public static final int STATE_IDLE=0;
	public static final int STATE_CHASE=1;
	public static final int STATE_ATTACK=2;
	public static final int STATE_DYING=3;
	public static final int STATE_DEAD=4;
	
	public static final int MIN_DAMAGE=5;
	public static final int MAX_DAMAGE=30;
	
	public static final float ATTACK_PROB=0.5f;
	public static final int MAX_HEALTH=100;
	
	public static final float MOVE_SPEED=1.0f;
	public static final float STOP_CHASE_DISTANCE=2.0f;
	
	public static final float ENEMY_LENGTH=0.2f;
	public static final float ENEMY_WIDTH=0.2f;
	public static final float SHOOT_DISTANCE=100.0f;
	
	private static Mesh mesh;
	private Material material;
	private Random random;
	private Transform transform;
	private Shader shader;
	private double deathTime;
	
	private int id;
	private int state;
	private boolean look;
	private boolean attack;
	private int health;
	private boolean addedToDeadList=false;
	private static ArrayList<Texture> animations;

	public Enemy(Transform transform, int id)
	{
		if(animations==null)
		{
			animations=new ArrayList<Texture>();
			animations.add(ResourceLoader.loadTexture("SSWVA1.png"));
			animations.add(ResourceLoader.loadTexture("SSWVB1.png"));
			animations.add(ResourceLoader.loadTexture("SSWVC1.png"));
			animations.add(ResourceLoader.loadTexture("SSWVD1.png"));
			
			animations.add(ResourceLoader.loadTexture("SSWVE0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVF0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVG0.png"));
			
			animations.add(ResourceLoader.loadTexture("SSWVH0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVI0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVJ0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVK0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVL0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVM0.png"));
		}
		this.id=id;
		deathTime=0;
		look=false;
		this.state=STATE_IDLE;
		health=MAX_HEALTH;
		attack=false;
		this.random=new Random();
		this.transform = transform;
		shader=new BasicShader();
		material = new Material(animations.get(0));
		mesh=new Mesh();

		
			Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX,START,START), new Vector2f(TEX_MAX_X,TEX_MAX_Y)),
											 new Vertex(new Vector3f(-SIZEX,SIZEY,START), new Vector2f(TEX_MAX_X,TEX_MIN_Y)),
											 new Vertex(new Vector3f(SIZEX,SIZEY,START), new Vector2f(TEX_MIN_X,TEX_MIN_Y)),
											 new Vertex(new Vector3f(SIZEX,START,START), new Vector2f(TEX_MIN_X,TEX_MAX_Y))};

			int[] indices = new int[]{0,1,2,
									  0,2,3};

			mesh.addVertices(vertices, indices);
	}
	
	public Transform getTransform()
	{
		return transform;
	}
	
	public void damage(int dmg)
	{
		if(state==STATE_IDLE)
			state=STATE_CHASE;
		health-=dmg;
		if(health<=0)
		{
			health=0;
			state=STATE_DYING;
		}
		//System.out.println(health);
	}
	
	private void idleUpdate(Vector3f orientation, float distance)
	{
		double time=(double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals=(double)(time-(int)time);
		
		if(timeDecimals<0.5)
		{
			look=true;
			material.setTexture(animations.get(0));
		}
		else
		{
			material.setTexture(animations.get(1));
			if(look)
			{
				Vector2f lineStart=new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
				Vector2f castDirection=new Vector2f(orientation.getX(), orientation.getZ());
				Vector2f lineEnd=lineStart.add(castDirection.multiply(SHOOT_DISTANCE));
				
				Vector2f collision=Game.getLevel().checkCollisionOfBullet(lineStart,lineEnd, false);
				
				Vector2f playerIntersectVector=new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ());
				
				if(collision==null || playerIntersectVector.subtract(lineStart).length()<collision.subtract(lineStart).length())
				{
					state=STATE_CHASE;
				}
				
				look=false;
			}
		}
		
	}
	
	private void chaseUpdate(Vector3f orientation, float distance)
	{
		double time=(double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals=(double)(time-(int)time);
		if(timeDecimals < 0.25)
			material.setTexture(animations.get(0));
		else if(timeDecimals < 0.5)
			material.setTexture(animations.get(1));
		else if(timeDecimals < 0.75)
			material.setTexture(animations.get(2));
		else
			material.setTexture(animations.get(3));
		if(random.nextDouble() < ATTACK_PROB * Time.getDelta())
			state=STATE_ATTACK;
		if(distance>STOP_CHASE_DISTANCE)
		{	
			Vector3f oldPos=transform.getTranslation();
			Vector3f newPos=transform.getTranslation().add(orientation.multiply(MOVE_SPEED*(float)Time.getDelta()));
			
			Vector3f collisionVector=Game.getLevel().checkCollision(oldPos, newPos, ENEMY_WIDTH, ENEMY_LENGTH);
			Vector3f mov=collisionVector.multiply(orientation);
			if(mov.length()>0)
			{
				transform.setTranslation(transform.getTranslation().add(mov.multiply(MOVE_SPEED*(float)Time.getDelta())));
			}
		}
		else
			state=STATE_ATTACK;
	}
	
	private void attackUpdate(Vector3f orientation, float distance)
	{
		double time=(double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals=(double)(time-(int)time);
		
		if(timeDecimals<0.25)
		{
			material.setTexture(animations.get(4));
		}
		else if(timeDecimals<0.5)
		{
			material.setTexture(animations.get(5));
		}
		else if(timeDecimals<0.75)
		{
			material.setTexture(animations.get(6));
			if(attack)
			{
					Vector2f lineStart=new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
					Vector2f castDirection=new Vector2f(orientation.getX(), orientation.getZ()).rotate((random.nextFloat()-0.5f)*10.0f);
					Vector2f lineEnd=lineStart.add(castDirection.multiply(SHOOT_DISTANCE));
		
					Vector2f collision=Game.getLevel().checkCollisionOfBullet(lineStart,lineEnd,false);
		
					Vector2f playerIntersectVector=Game.getLevel().lineIntersectRect(lineStart,lineEnd,new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ()),new Vector2f(0.2f,0.2f));
		
					if(playerIntersectVector!=null && (collision==null || playerIntersectVector.subtract(lineStart).length()<collision.subtract(lineStart).length()))
					{
							Game.getLevel().damage(random.nextInt(MAX_DAMAGE-MIN_DAMAGE)+MIN_DAMAGE);			
					}
					attack=false;
			}
		}
		else
		{
			material.setTexture(animations.get(5));
			if(timeDecimals>0.99f)
			{
				state=STATE_CHASE;
				attack=true;
			}
		}
		
	}
	
	private void dyingUpdate(Vector3f orientation, float distance)
	{
		double time = ((double)Time.getTime())/((double)Time.SECOND);

		if(deathTime == 0)
			deathTime = time;

		final float time1 = 0.1f;
		final float time2 = 0.3f;
		final float time3 = 0.45f;
		final float time4 = 0.6f;

		if(time < deathTime + time1)
		{
			material.setTexture(animations.get(8));
			transform.setScale(1,0.96428571428571428571428571428571f,1);
		}
		else if(time < deathTime + time2)
		{
			material.setTexture(animations.get(9));
			transform.setScale(1.7f,0.9f,1);
		}
		else if(time < deathTime + time3)
		{
			material.setTexture(animations.get(10));
			transform.setScale(1.7f,0.9f,1);
		}
		else if(time < deathTime + time4)
		{
			material.setTexture(animations.get(11));
			transform.setScale(1.7f,0.5f,1);
		}
		else
		{
			state = STATE_DEAD;
		}
	}
	
	private void deadUpdate(Vector3f orientation, float distance)
	{
		material.setTexture(animations.get(12));
		transform.setScale(1.7586206896551724137931034482759f,0.28571428571428571428571428571429f,1);
		if(!addedToDeadList)
		{
			Game.getLevel().deleteDeadMonster(id);
			addedToDeadList=true;
		}
	}
	
	private void enemySetAtGround()
	{
		transform.getTranslation().setY(0.0f);
	}
	
	private void enemyLookAtCamera(Vector3f dirToCamera)
	{
		float angleCamera=(float)Math.toDegrees(Math.atan(dirToCamera.getZ()/dirToCamera.getX()));
		if(dirToCamera.getX() < 0)
			angleCamera+=180;
		transform.getRotation().setY(angleCamera+90);
	}
	
	private Vector2f enemyNearestNode()
	{
		double min=Double.POSITIVE_INFINITY;
		Vector2f nearest=null;
		Vector2f currentPos=new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
		for(int i=0;i<Game.getLevel().nodes.size();i++)
		{
			double distance=currentPos.subtract(Game.getLevel().nodes.get(i).pos).length();
			if(distance<min)
			{
				min=distance;
				nearest=Game.getLevel().nodes.get(i).pos;
			}
		}
		return nearest;
	}
	
	private Vector2f playerNearestNode()
	{
		double min=Double.POSITIVE_INFINITY;
		Vector2f nearest=null;
		Vector2f currentPos=new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ());
		for(int i=0;i<Game.getLevel().nodes.size();i++)
		{
			double distance=currentPos.subtract(Game.getLevel().nodes.get(i).pos).length();
			if(distance<min)
			{
				min=distance;
				nearest=Game.getLevel().nodes.get(i).pos;
			}
		}
		return nearest;
	}
	

	public void update()
	{
			//Vector2f nearestNodeToEnemy=enemyNearestNode();
			//Vector2f nearestNodeToPlayer=playerNearestNode();
		
			//TODO: Node based pathfinding instead of direct movement for enemy
			Vector3f dirToCamera = Transform.getCamera().getPos().subtract(transform.getTranslation());
			float distance=dirToCamera.length();
			Vector3f orientation=dirToCamera.normalizeIntoUnitVector();
			enemySetAtGround();
			enemyLookAtCamera(orientation);
			
			switch(state)
			{
			case STATE_IDLE:
				idleUpdate(orientation, distance);
				break;
			case STATE_CHASE:
				chaseUpdate(orientation, distance);
				break;
			case STATE_ATTACK:
				attackUpdate(orientation, distance);
				break;
			case STATE_DYING:
				dyingUpdate(orientation, distance);
				break;
			case STATE_DEAD:
				deadUpdate(orientation, distance);
				break;
			}
	}

	public void render()
	{
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
