package com.base.engine;

import java.util.ArrayList;

public class UI 
{
	public static final float UI_SCALE = 0.01f;
	public static final float UI_SIZEY = UI_SCALE;
	public static final float UI_SIZEX = (float)((double)UI_SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float UI_START = 0;
	
	public static final float OFFSET_X = 0.0f;
	public static final float OFFSET_Y = 0.0f;

	public static final float TEX_MIN_X = -OFFSET_X;
	public static final float TEX_MAX_X = -1 - OFFSET_X;
	public static final float TEX_MIN_Y = -OFFSET_Y;
	public static final float TEX_MAX_Y = 1 - OFFSET_Y;
	
	private int value;
	private float xOffset;
	private float yOffset;
	
	private Mesh mesh;
	private Transform transform;
	private Shader shader;
	private Material material;
	

	private static ArrayList<Texture> numbersUI;
	
	public UI(float x, float y, Vector3f position)
	{
		if(numbersUI==null)
		{
			numbersUI=new ArrayList<Texture>();
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/0.png"));
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/1.png"));
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/2.png"));
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/3.png"));
			
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/4.png"));
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/5.png"));
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/6.png"));
			
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/7.png"));
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/8.png"));
			numbersUI.add(ResourceLoader.loadTexture("ui_numbers/9.png"));
		}
		xOffset=x;
		yOffset=y;
		transform=new Transform();
		shader=new BasicShader();
		transform.setTranslation(position);
		material=new Material(numbersUI.get(0));
		mesh=new Mesh();
		
		Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-UI_SIZEX,UI_START,UI_START), new Vector2f(TEX_MAX_X,TEX_MAX_Y)),
				 new Vertex(new Vector3f(-UI_SIZEX,UI_SIZEY,UI_START), new Vector2f(TEX_MAX_X,TEX_MIN_Y)),
				 new Vertex(new Vector3f(UI_SIZEX,UI_SIZEY,UI_START), new Vector2f(TEX_MIN_X,TEX_MIN_Y)),
				 new Vertex(new Vector3f(UI_SIZEX,UI_START,UI_START), new Vector2f(TEX_MIN_X,TEX_MAX_Y))};

		int[] indices = new int[]{0,1,2,
		  0,2,3};

		mesh.addVertices(vertices, indices);
	}
	
	public void setValueUI(int val)
	{
		value=val;
	}
	
	public void update()
	{
		material.setTexture(numbersUI.get(value));
		transform.setTranslation(Game.getLevel().getPlayer().getCamera().getPos().add(Game.getLevel().getPlayer().getCamera().getForward().multiply(0.105f).add(Game.getLevel().getPlayer().getCamera().getLeft().multiply(xOffset))));
		transform.getTranslation().setY(transform.getTranslation().getY()+yOffset);
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

}
