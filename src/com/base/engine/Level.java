package com.base.engine;

import java.util.ArrayList;

public class Level 
{
	private static final float CUBE_WIDTH=1;
	private static final float CUBE_HEIGHT=1;
	private static final float CUBE_LENGTH=1;
	
	
	private Bitmap level;
	private Mesh meshWall, meshFloor,meshCeiling;
	private Shader shaderWall,shaderFloor,shaderCeiling;
	private Material materialWall, materialFloor, materialCeiling;
	private Transform transform;
	public Level(String levelName, String textureWallName, String textureFloorName, String textureCeilingName)
	{
		level=new Bitmap(levelName).flipY();
		meshWall=new Mesh();
		meshFloor=new Mesh();
		meshCeiling=new Mesh();
		
		shaderWall=new BasicShader();
		shaderFloor=new BasicShader();
		shaderCeiling=new BasicShader();
		
		materialWall=new Material(ResourceLoader.loadTexture(textureWallName));
		materialFloor=new Material(ResourceLoader.loadTexture(textureFloorName));
		materialCeiling=new Material(ResourceLoader.loadTexture(textureCeilingName));
		
		transform=new Transform();
		
		generateFloor(meshFloor);
		generateWall(meshWall);
		generateCeiling(meshCeiling);
	}
	
	public void input()
	{
		
	}
	
	public Vector3f checkCollision(Vector3f oldPos, Vector3f newPos, float objectWidth, float objectLength)
	{
		Vector2f collisionVector = new Vector2f(1,1);
		Vector3f movementVector = newPos.subtract(oldPos);
		
		if(movementVector.length() > 0)
		{
			Vector2f blockSize = new Vector2f(CUBE_WIDTH, CUBE_LENGTH);
			Vector2f objectSize = new Vector2f(objectWidth, objectLength);
			
			Vector2f oldPos2 = new Vector2f(oldPos.getX(), oldPos.getZ());
			Vector2f newPos2 = new Vector2f(newPos.getX(), newPos.getZ());
			
			for(int i = 0; i < level.getWidth(); i++)
				for(int j = 0; j < level.getHeight(); j++)
					if((level.getPixel(i,j) & 0xFFFFFF) == 0)
						collisionVector = collisionVector.multiply(rectCollide(oldPos2, newPos2, objectSize, blockSize.multiply(new Vector2f(i,j)), blockSize));
		}
		
		return new Vector3f(collisionVector.getX(), 0, collisionVector.getY());
	}
	
	private Vector2f rectCollide(Vector2f oldPos, Vector2f newPos, Vector2f size1, Vector2f pos2, Vector2f size2)
	{
		Vector2f result = new Vector2f(0,0);
		
		if(newPos.getX() + size1.getX() < pos2.getX() ||
		   newPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() ||
		   oldPos.getY() + size1.getY() < pos2.getY() ||
		   oldPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY())
			result.setX(1);
		
		if(oldPos.getX() + size1.getX() < pos2.getX() ||
		   oldPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() ||
		   newPos.getY() + size1.getY() < pos2.getY() ||
		   newPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY())
			result.setY(1);
		
		return result;
	}
	
	public void update()
	{
		
	}
	
	public void render()
	{
		shaderWall.bind();
		shaderWall.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), materialWall);
		meshWall.draw();
		
		shaderFloor.bind();
		shaderFloor.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), materialFloor);
		meshFloor.draw();
		
		shaderCeiling.bind();
		shaderCeiling.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), materialCeiling);
		meshCeiling.draw();
	}
	
	private void addFace(ArrayList<Integer> indices, int startLocation, boolean direction)
	{
		if(direction)
		{
			indices.add(startLocation+2);
			indices.add(startLocation+1);
			indices.add(startLocation+0);
			indices.add(startLocation+3);
			indices.add(startLocation+2);
			indices.add(startLocation+0);
		}
		else
		{
			indices.add(startLocation+0);
			indices.add(startLocation+1);
			indices.add(startLocation+2);
			indices.add(startLocation+0);
			indices.add(startLocation+2);
			indices.add(startLocation+3);
		}
	}
	
	private void generateWall(Mesh m)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();

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
				
				//Wall
				if((level.getPixel(i, j-1) & 0xFFFFFF)==0)
				{
					addFace(indices, vertices.size(), false);
					
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				}
				if((level.getPixel(i, j+1) & 0xFFFFFF)==0)
				{
					addFace(indices, vertices.size(), true);
					
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				}
				if((level.getPixel(i-1, j) & 0xFFFFFF)==0)
				{
					addFace(indices, vertices.size(), true);
					
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
					vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				}
				if((level.getPixel(i+1, j) & 0xFFFFFF)==0)
				{
					addFace(indices, vertices.size(), false);
					
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
		m.addVertices(verticesArray, Util.toIntArray(indicesArray));
	}
	
	private void generateFloor(Mesh m)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();
		
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
				addFace(indices, vertices.size(), true);
				
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				
			}
		}
		
		Vertex[] verticesArray=new Vertex[vertices.size()];
		Integer[] indicesArray=new Integer[indices.size()];
		
		vertices.toArray(verticesArray);
		indices.toArray(indicesArray);
		m.addVertices(verticesArray, Util.toIntArray(indicesArray));
	}
	
	private void generateCeiling(Mesh m)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();
		
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
				
				addFace(indices, vertices.size(), false);
				
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
				
			}
		}
		
		Vertex[] verticesArray=new Vertex[vertices.size()];
		Integer[] indicesArray=new Integer[indices.size()];
		
		vertices.toArray(verticesArray);
		indices.toArray(indicesArray);
		m.addVertices(verticesArray, Util.toIntArray(indicesArray));
	}
	

}
