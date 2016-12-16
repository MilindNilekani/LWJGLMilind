package com.base.engine;

public class Banana {
	public static final float PICKUP_DISTANCE = 0.75f;
	public static final int HEAL_AMOUNT = 25;

	public static final float SCALE = 0.25f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float)((double)SIZEY / (0.67857142857142857142857142857143 * 2.5));
	public static final float START = 0;

	public static final float OFFSET_X = 0.0f; //0.05f
	public static final float OFFSET_Y = 0.0f; //0.01f

	public static final float TEX_MIN_X = -OFFSET_X;
	public static final float TEX_MAX_X = -1 - OFFSET_X;
	public static final float TEX_MIN_Y = -OFFSET_Y;
	public static final float TEX_MAX_Y = 1 - OFFSET_Y;
	
	private static Mesh mesh;
	private static Material material;
	private Shader shader;
	
	private Transform transform;
	
	public Banana(Vector3f position)
	{
		mesh=new Mesh();
			Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX,START,START), new Vector2f(TEX_MAX_X,TEX_MAX_Y)),
					new Vertex(new Vector3f(-SIZEX,SIZEY,START), new Vector2f(TEX_MAX_X,TEX_MIN_Y)),
					new Vertex(new Vector3f(SIZEX,SIZEY,START), new Vector2f(TEX_MIN_X,TEX_MIN_Y)),
					new Vertex(new Vector3f(SIZEX,START,START), new Vector2f(TEX_MIN_X,TEX_MAX_Y))};

			int[] indices = new int[]{0,1,2,
					0,2,3};

			mesh.addVertices(vertices, indices);
		
		shader=new BasicShader();

			material = new Material(ResourceLoader.loadTexture("Banana.png"));
		

		transform = new Transform();
		transform.setTranslation(position);
	}
	
	public void update()
	{
		Vector3f directionToCamera = Transform.getCamera().getPos().subtract(transform.getTranslation());

		float angleToFaceTheCamera = (float)Math.toDegrees(Math.atan(directionToCamera.getZ() / directionToCamera.getX()));

		if(directionToCamera.getX() < 0)
			angleToFaceTheCamera += 180;

		transform.getRotation().setY(angleToFaceTheCamera + 90);

		if(directionToCamera.length() < PICKUP_DISTANCE)
		{
			Player player = Game.getLevel().getPlayer();

			if(player.getHealth() < player.getMaxHealth())
			{
				Game.getLevel().removeBananaOnConsumed(this);
				player.damage(-HEAL_AMOUNT);
			}
		}
	}

	public void render()
	{
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}

}
