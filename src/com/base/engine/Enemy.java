package com.base.engine;

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
	
	
	public static final float MOVE_SPEED=1.0f;
	public static final float STOP_CHASE_DISTANCE=2.0f;
	
	public static final float ENEMY_LENGTH=0.2f;
	public static final float ENEMY_WIDTH=0.2f;
	
	private static Mesh mesh;
	private Material material;
	private Transform transform;
	private Shader shader;
	private int state;

	public Enemy(Transform transform)
	{
		this.state=STATE_CHASE;
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
	
	private void idleUpdate(Vector3f orientation, float distance)
	{
		
	}
	
	private void chaseUpdate(Vector3f orientation, float distance)
	{
		if(distance>STOP_CHASE_DISTANCE)
		{	
			Vector3f oldPos=transform.getTranslation();
			Vector3f newPos=transform.getTranslation().add(orientation.multiply(-MOVE_SPEED*(float)Time.getDelta()));
			
			Vector3f collisionVector=Game.getLevel().checkCollision(oldPos, newPos, ENEMY_WIDTH, ENEMY_LENGTH);
			Vector3f mov=collisionVector.multiply(orientation);
			if(mov.length()>0)
			{
				transform.setTranslation(transform.getTranslation().add(mov.multiply(-MOVE_SPEED*(float)Time.getDelta())));
			}
		}
	}
	
	private void attackUpdate(Vector3f orientation, float distance)
	{
		
	}
	
	private void dyingUpdate(Vector3f orientation, float distance)
	{
		
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
		if(dirToCamera.getX() > 0)
			angleCamera+=180;
		transform.getRotation().setY(angleCamera+90);
	}
	

	public void update()
	{
			Vector3f dirToCamera = transform.getTranslation().subtract(Transform.getCamera().getPos());
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
