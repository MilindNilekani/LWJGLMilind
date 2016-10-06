package com.base.engine;

public class Game 
{
	private Mesh mesh;
	private Shader shader;
	private Transform transform;
	private Material material;
	private Camera camera;
	
	PointLight pLight1=new PointLight(new Light(new Vector3f(1,0.5f,0),0.8f), new Attenuation(0,0,1), new Vector3f(-2,0,6));
	PointLight pLight2=new PointLight(new Light(new Vector3f(0,0.5f,1),0.8f), new Attenuation(0,0,1), new Vector3f(2,0,7));
	
	public Game()
	{
		//mesh=ResourceLoader.loadMesh("box.obj");
		material=new Material(ResourceLoader.loadTexture("test.png"), new Vector3f(1,1,1),1,8);
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
		
		/*Vertex[] vertices=new Vertex[] {new Vertex(new Vector3f(-1,-1,0), new Vector2f(0,0)), 
				new Vertex(new Vector3f(0,1,0), new Vector2f(0.5f,0)),
				new Vertex(new Vector3f(1,-1,0), new Vector2f(1,0)),
				new Vertex(new Vector3f(0,-1,1), new Vector2f(0.5f,1))};
		*/
		/*Vertex[] vertices=new Vertex[] {new Vertex(new Vector3f(-1,-1,0)), 
				new Vertex(new Vector3f(0,1,0)),
				new Vertex(new Vector3f(1,-1,0)),
				new Vertex(new Vector3f(0,-1,1))};
		*/
		/*int[] indices=new int[]{3,1,0,
								2,1,3,
								0,1,2,
								0,2,3};
		*/
		mesh.addVertices(vertices, indices,true);
		LightingShader.setAmbientLight(new Vector3f(0.2f,0.2f,0.2f));
		//LightingShader.setDirectionalLight(new DirectionalLight(new Light(new Vector3f(1,1,1),0.8f),new Vector3f(1,1,1)));
		
		
		LightingShader.setPointLight(new PointLight[]{pLight1,pLight2});
		/*shader.addVertexShader(ResourceLoader.loadShader("basicVertex.vs"));
		shader.addFragmentShader(ResourceLoader.loadShader("basicFragment.fs"));
		shader.compileShader();
		
		shader.addUniform("transform");
		*/
	}
	
	public void input()
	{
		camera.input();
		/*if(Input.getKeyDown(Keyboard.KEY_UP))
				System.out.println("Pressed Up");
		if(Input.getKeyUp(Keyboard.KEY_UP))
				System.out.println("Released Up");
		
		if(Input.getMouseDown(1))
			System.out.println("Pressed Right Mouse at " + Input.getMousePosition().toString());
		if(Input.getMouseUp(1))
			System.out.println("Released Right Mouse");*/
	}
	
	float temp=0.0f;
	
	public void update()
	{
		temp+=Time.getDelta();
		transform.setTranslation(0,-1,5);
		
		pLight1.setPosition(new Vector3f(3,0,8.0f*(float)(Math.sin(temp)+1.0/2.0)+10));
		pLight2.setPosition(new Vector3f(7,0,8.0f*(float)(Math.cos(temp)+1.0/2.0)+10));
		//transform.setRotation(0,(float)Math.sin(temp)*180,0);
		//transform.setScale(0.7f*(float)Math.sin(temp),0.7f* (float)Math.sin(temp),0.7f* (float)Math.sin(temp));
	}
	
	public void render()
	{
		RenderUtil.setClearColor(Transform.getCamera().getPos().divide(2048f).abs());
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		
		mesh.draw();
	}
}
