package com.base.engine;

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
	private int state;
	private boolean look;
	private boolean attack;
	private int health;

	public Enemy(Transform transform)
	{
		look=false;
		this.state=STATE_IDLE;
		health=MAX_HEALTH;
		attack=false;
		this.random=new Random();
		this.transform = transform;
		shader=new BasicShader();
		material = new Material(ResourceLoader.loadTexture("SSWVA1.png"));
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
		if(state==STATE_IDLE)
			state=STATE_CHASE;
		health-=dmg;
		if(health<=0)
			state=STATE_DYING;
	}
	
	private void idleUpdate(Vector3f orientation, float distance)
	{
		double time=(double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals=(double)(time-(int)time);
		
		if(timeDecimals<0.5)
		{
			look=true;
		}
		else
		{
			if(look)
			{
				Vector2f lineStart=new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
				Vector2f castDirection=new Vector2f(orientation.getX(), orientation.getZ());
				Vector2f lineEnd=lineStart.add(castDirection.multiply(SHOOT_DISTANCE));
				
				Vector2f collision=Game.getLevel().checkCollisionOfBullet(lineStart,lineEnd);
				
				Vector2f playerIntersectVector=new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ());
				
				if(collision==null || playerIntersectVector.subtract(lineStart).length()<collision.subtract(lineStart).length())
				{
					System.out.println("Seen player");
					state=STATE_CHASE;
				}
				
				look=false;
			}
		}
		
	}
	
	private void chaseUpdate(Vector3f orientation, float distance)
	{
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
			attack=true;
		}
		else if(timeDecimals<0.75)
		{
			if(attack)
			{
					Vector2f lineStart=new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
					Vector2f castDirection=new Vector2f(orientation.getX(), orientation.getZ()).rotate((random.nextFloat()-0.5f)*10.0f);
					Vector2f lineEnd=lineStart.add(castDirection.multiply(SHOOT_DISTANCE));
		
					Vector2f collision=Game.getLevel().checkCollisionOfBullet(lineStart,lineEnd);
		
					Vector2f playerIntersectVector=Game.getLevel().lineIntersectRect(lineStart,lineEnd,new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ()),new Vector2f(0.2f,0.2f));
		
					if(playerIntersectVector!=null && (collision==null || playerIntersectVector.subtract(lineStart).length()<collision.subtract(lineStart).length()))
					{
							System.out.println("Hit player");
			
					}
					attack=false;
			}
		}
		else
		{
			state=STATE_CHASE;
			attack=true;
		}
	}
	
	private void dyingUpdate(Vector3f orientation, float distance)
	{
		state=STATE_DEAD;
	}
	
	private void deadUpdate(Vector3f orientation, float distance)
	{
		
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
	

	public void update()
	{
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
}
