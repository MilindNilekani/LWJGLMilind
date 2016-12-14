package com.base.engine;

import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.Clip;

public class ExplosiveBarrel
{
	public static final float SCALE = 0.5f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float)((double)0.7 / (1.9310344827586206896551724137931 * 2.0));
	public static final float START = 0;

	public static final float OFFSET_X = 0.0f;
	public static final float OFFSET_Y = 0.0f;

	public static final float TEX_MIN_X = -OFFSET_X;
	public static final float TEX_MAX_X = -1 - OFFSET_X;
	public static final float TEX_MIN_Y = -OFFSET_Y;
	public static final float TEX_MAX_Y = 1 - OFFSET_Y;
	
	public static final int STATE_IDLE=0;
	public static final int STATE_HIT=1;
	public static final int STATE_EXPLODING=2;
	public static final int STATE_EXPLODED=3;
	
	public static final int MIN_DAMAGE=15;
	public static final int MAX_DAMAGE=45;
	
	private static final Clip EXPLODING_SOUND=ResourceLoader.loadAudio("BarrelExplosion.wav");
	
	public static final float ATTACK_PROB=0.5f;
	public static final int MAX_HEALTH=50;
	
	public static final float BARREL_LENGTH=0.2f;
	public static final float BARREL_WIDTH=0.2f;
	public static final float BLAST_RADIUS=5.0f;
	
	private static Mesh mesh;
	private Material material;
	private Random random;
	private Transform transform;
	private Shader shader;
	private double explodingTime;
	
	private boolean hurtPlayer=false;
	
	private int id;
	private int state;
	private int health;
	private static ArrayList<Texture> animations;

	public ExplosiveBarrel(Vector3f position)
	{
		if(animations==null)
		{
			animations=new ArrayList<Texture>();
			animations.add(ResourceLoader.loadTexture("B1.png"));
			animations.add(ResourceLoader.loadTexture("B2.png"));
			animations.add(ResourceLoader.loadTexture("B3.png"));
			animations.add(ResourceLoader.loadTexture("B4.png"));
		}
		explodingTime=0;
		this.state=STATE_IDLE;
		transform = new Transform();
		transform.setTranslation(position);
		health=MAX_HEALTH;
		this.random=new Random();
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
		//if(state==STATE_IDLE)
		//	state=STATE_HIT;
		health-=dmg;
		if(health<=0)
		{
			health=0;
			hurtPlayer=true;
			AudioUtil.playAudio(EXPLODING_SOUND, 10);
			state=STATE_EXPLODING;
		}
	}
	
	private void idleUpdate(Vector3f orientation, float distance)
	{
			material.setTexture(animations.get(0));
	}
	
	private void hitUpdate(Vector3f orientation, float distance)
	{
		double time=(double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals=(double)(time-(int)time);
		if(timeDecimals < 0.1)
			material.setTexture(animations.get(1));
		else
			state=STATE_IDLE;
	}
	
	private void explodingUpdate(Vector3f orientation, float distance)
	{
		double time = ((double)Time.getTime())/((double)Time.SECOND);

		if(explodingTime == 0)
			explodingTime = time;

		final float time1 = 0.1f;
		final float time2 = 0.3f;
		final float time4 = 0.45f;

		if(time < explodingTime + time1)
		{
			material.setTexture(animations.get(1));
		}
		else if(time < explodingTime + time2)
		{
			material.setTexture(animations.get(2));
		}
		else if(time < explodingTime + time4)
		{
			material.setTexture(animations.get(3));
			for(Enemy e:Game.getLevel().enemyList)
			{
				if(!Game.getLevel().deadEnemyList.contains(e))
				{
					float distanceFromEnemy=e.getTransform().getTranslation().subtract(transform.getTranslation()).length();
					if(distanceFromEnemy<BLAST_RADIUS)
					{
						e.damage(100);
					}
				}
			}
			if(distance<BLAST_RADIUS && hurtPlayer)
			{
				Game.getLevel().getPlayer().damage(random.nextInt(MAX_DAMAGE-MIN_DAMAGE)+MIN_DAMAGE);
				hurtPlayer=false;
			}
		}
		else
		{
			state = STATE_EXPLODED;
		}
	}
	
	private void explodedUpdate(Vector3f orientation, float distance)
	{
		Game.getLevel().removeBarrelsOnExplosion(this);
	}
	
	private void barrelSetAtGround()
	{
		transform.getTranslation().setY(0.0f);
	}
	
	private void barrelLookAtCamera(Vector3f dirToCamera)
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
			barrelSetAtGround();
			barrelLookAtCamera(orientation);
			
			switch(state)
			{
			case STATE_IDLE:
				idleUpdate(orientation, distance);
				break;
			case STATE_HIT:
				hitUpdate(orientation, distance);
				break;
			case STATE_EXPLODING:
				explodingUpdate(orientation, distance);
				break;
			case STATE_EXPLODED:
				explodedUpdate(orientation, distance);
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
