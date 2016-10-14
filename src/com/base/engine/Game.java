package com.base.engine;

import java.util.ArrayList;

public class Game 
{
	/*
	private Mesh mesh;
	private Shader shader;
	private Transform transform;
	private Material material;
	private Camera camera;
	
	PointLight pLight1=new PointLight(new Light(new Vector3f(1,0.5f,0),0.8f), new Attenuation(0,0,1), new Vector3f(-2,0,6), 10);
	PointLight pLight2=new PointLight(new Light(new Vector3f(0,0.5f,1),0.8f), new Attenuation(0,0,1), new Vector3f(2,0,7), 10);
	
	SpotLight sLight1=new SpotLight(new PointLight(new Light(new Vector3f(0,1f,1),0.8f), new Attenuation(0,0,0.1f), new Vector3f(-2,0,6), 30), new Vector3f(1,1,1), 0.7f);
	*/
	
	private static final float CUBE_WIDTH=1;
	private static final float CUBE_HEIGHT=1;
	private static final float CUBE_LENGTH=1;
	
	Bitmap level;
	Shader shader;
	Material material;
	Mesh mesh;
	Transform transform;
	
	public Game()
	{
		level=new Bitmap("level1.png").flipY();
		mesh=new Mesh();
		
		shader=BasicShader.getInstance();
		material=new Material(ResourceLoader.loadTexture("test.png"));
		
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();
		transform=new Transform();
		
		
		
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if((level.getPixel(i, j) & 0xFFFFFF)==0)
					continue;
				
				float xHigh=1;
				float xLow=0;
				float yHigh=1;
				float yLow=0;
				
				//Floor
				indices.add(vertices.size()+2);
				indices.add(vertices.size()+1);
				indices.add(vertices.size()+0);
				indices.add(vertices.size()+3);
				indices.add(vertices.size()+2);
				indices.add(vertices.size()+0);
				
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				
				//Ceiling
				indices.add(vertices.size()+0);
				indices.add(vertices.size()+1);
				indices.add(vertices.size()+2);
				indices.add(vertices.size()+0);
				indices.add(vertices.size()+2);
				indices.add(vertices.size()+3);
				
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				
				//Wall
				if((level.getPixel(i, j-1) & 0xFFFFFF)==0)
				{
					indices.add(vertices.size()+0);
					indices.add(vertices.size()+1);
					indices.add(vertices.size()+2);
					indices.add(vertices.size()+0);
					indices.add(vertices.size()+2);
					indices.add(vertices.size()+3);
					
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				}
				if((level.getPixel(i, j+1) & 0xFFFFFF)==0)
				{
					indices.add(vertices.size()+2);
					indices.add(vertices.size()+1);
					indices.add(vertices.size()+0);
					indices.add(vertices.size()+3);
					indices.add(vertices.size()+2);
					indices.add(vertices.size()+0);
					
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				}
				if((level.getPixel(i-1, j) & 0xFFFFFF)==0)
				{
					indices.add(vertices.size()+2);
					indices.add(vertices.size()+1);
					indices.add(vertices.size()+0);
					indices.add(vertices.size()+3);
					indices.add(vertices.size()+2);
					indices.add(vertices.size()+0);
					
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				}
				if((level.getPixel(i+1, j) & 0xFFFFFF)==0)
				{
					indices.add(vertices.size()+0);
					indices.add(vertices.size()+1);
					indices.add(vertices.size()+2);
					indices.add(vertices.size()+0);
					indices.add(vertices.size()+2);
					indices.add(vertices.size()+3);
					
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				}
			}
		}
		
		Vertex[] verticesArray=new Vertex[vertices.size()];
		Integer[] indicesArray=new Integer[indices.size()];
		
		vertices.toArray(verticesArray);
		indices.toArray(indicesArray);
		mesh.addVertices(verticesArray, Util.toIntArray(indicesArray));
		
		Transform.setCamera(new Camera());
		Transform.setProjection(70, 0.01f, 1000f, Window.getWidth(), Window.getHeight());
		
		/*material=new Material(ResourceLoader.loadTexture("test.png"), new Vector3f(1,1,1),1,8);
		mesh=new Mesh();
		shader=LightingShader.getInstance();
		camera=new Camera();
		
		Transform.setProjection(70f, 0.1f, 1000f, Window.getWidth(), Window.getHeight());
		Transform.setCamera(camera);
		
		transform=new Transform();
		
		float xTemp=10.0f;
		float yTemp=10.0f;
		Vertex[] vertices = new Vertex[] { new Vertex( new Vector3f(-xTemp, 0, -yTemp),	new Vector2f(0.0f, 0.0f)),
				        new Vertex( new Vector3f(-xTemp, 0f, yTemp*3),		new Vector2f(0f, 1.0f)),
				        new Vertex( new Vector3f(xTemp*3, 0f, -yTemp),	new Vector2f(1.0f, 0.0f)),
				        new Vertex( new Vector3f(xTemp*3, 0f, yTemp*3),      new Vector2f(1.0f, 1.0f)) };
				
		int indices[] = { 0, 1, 2,
				 		2,1,3 };
		
		mesh.addVertices(vertices, indices,true);
		LightingShader.setAmbientLight(new Vector3f(0.2f,0.2f,0.2f));
		LightingShader.setDirectionalLight(new DirectionalLight(new Light(new Vector3f(1,1,1),0.1f),new Vector3f(1,1,1)));
		
		
		LightingShader.setPointLight(new PointLight[]{pLight1,pLight2});
		
		LightingShader.setSpotLight(new SpotLight[]{sLight1}); 
		*/
	}
	
	public void input()
	{
		//camera.input();
		Transform.getCamera().input();
	}
	
	//float temp=0.0f;
	
	public void update()
	{
		/*temp+=Time.getDelta();
		transform.setTranslation(0,-1,5);
		
		pLight1.setPosition(new Vector3f(3,0,8.0f*(float)(Math.sin(temp)+1.0/2.0)+10));
		pLight2.setPosition(new Vector3f(7,0,8.0f*(float)(Math.cos(temp)+1.0/2.0)+10));
		
		sLight1.getPointLight().setPosition(camera.getPos());
		sLight1.setDirection(camera.getForward());
		*/
		
	}
	
	public void render()
	{
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
		/*RenderUtil.setClearColor(Transform.getCamera().getPos().divide(2048f).abs());
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		
		mesh.draw();*/
	}
}
