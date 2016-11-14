package com.base.engine;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class Level 
{
	private static final float CUBE_WIDTH=1;
	private static final float CUBE_HEIGHT=1;
	private static final float CUBE_LENGTH=1;
	
	private Bitmap level;
	private Mesh meshWall, meshFloor,meshCeiling, meshGrafitti, meshPictureFrame;
	private Shader shaderWall,shaderFloor,shaderCeiling, shaderGrafitti, shaderPictureFrame;
	private Material materialWall, materialFloor, materialCeiling, materialGrafitti, materialPictureFrame;
	
	private ArrayList<Vector2f> collisionStart;
	private ArrayList<Vector2f> collisionEnd;
	
	private static final Clip GUNSHOT_AUDIO=ResourceLoader.loadAudio("ClockTick2.wav");
	
	
	public ArrayList<Node> nodes=new ArrayList<Node>();
	public class Node
	{
		Vector2f pos;
		ArrayList<Node> neighbours=new ArrayList<Node>();
	}
	
	private Transform transform;
	
	public ArrayList<Enemy> enemyList;
	public ArrayList<Enemy> deadEnemyList;
	private Player player;
	
	public Level(String levelName)
	{
		deadEnemyList=new ArrayList<Enemy>();
		enemyList=new ArrayList<Enemy>();
		collisionStart=new ArrayList<Vector2f>();
		collisionEnd=new ArrayList<Vector2f>();

		level=new Bitmap(levelName).flipY();
		meshWall=new Mesh();
		meshFloor=new Mesh();
		meshCeiling=new Mesh();
		meshGrafitti=new Mesh();
		meshPictureFrame=new Mesh();
		
		shaderWall=new BasicShader();
		shaderFloor=new BasicShader();
		shaderCeiling=new BasicShader();
		shaderGrafitti=new BasicShader();
		shaderPictureFrame=new BasicShader();
		
		materialWall=new Material(ResourceLoader.loadTexture("wall.png"));
		materialFloor=new Material(ResourceLoader.loadTexture("floor.png"));
		materialCeiling=new Material(ResourceLoader.loadTexture("ceiling.png"));
		materialGrafitti=new Material(ResourceLoader.loadTexture("grafitti.png"));
		materialPictureFrame=new Material(ResourceLoader.loadTexture("poster.png"));
		
		transform=new Transform();
		
		generateFloor(meshFloor);
		generateWall(meshWall);
		generateCeiling(meshCeiling);
		generateGrafitti(meshGrafitti);
		generatePictureFrame(meshPictureFrame);
		generatePlayer();
		generateEnemies();
	}
	
	private void generateEnemies()
	{
		int id=0;
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==-16777216 || level.getPixel(i, j)==-65536 || level.getPixel(i, j)==-16711936)
					continue;
				
				if(level.getPixel(i, j)==-16776961)
				{
					Transform enemyT=new Transform();
					enemyT.setTranslation(new Vector3f((i+0.5f)*CUBE_HEIGHT,0, (j+0.5f)*CUBE_LENGTH));
					enemyList.add(new Enemy(enemyT,id));
					id++;
				}
				
			}
		}
	}
	
	public void deleteDeadEnemy(int id)
	{
		for(Enemy e:enemyList)
		{
			if(e.getId()==id)
			{
				deadEnemyList.add(e);
			}
		}
		System.out.println(deadEnemyList.size());
	}
	
	private void generatePlayer()
	{
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==-16777216 || level.getPixel(i, j)==-65536 || level.getPixel(i, j)==-16711936)
					continue;
				
				if(level.getPixel(i, j)==-65281)
				{
					player=new Player(new Vector3f((i+0.5f)*CUBE_HEIGHT,0.4375f,(j+0.5f)*CUBE_LENGTH));
					Transform.setCamera(player.getCamera());
					Transform.setProjection(70, 0.01f, 1000f, Window.getWidth(), Window.getHeight());
				}
				
			}
		}
	}
	
	public void input()
	{
		player.input();
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
					if(level.getPixel(i, j)==-16777216 || level.getPixel(i, j)==-65536 || level.getPixel(i, j)==-16711936)
						collisionVector = collisionVector.multiply(AABB(oldPos2, newPos2, objectSize, blockSize.multiply(new Vector2f(i,j)), blockSize));
		}
		
		return new Vector3f(collisionVector.getX(), 0, collisionVector.getY());
	}
	
	private Vector2f AABB(Vector2f oldPos, Vector2f newPos, Vector2f size1, Vector2f pos2, Vector2f size2)
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
	
	public Vector2f checkCollisionOfBullet(Vector2f start, Vector2f end, boolean enemy)
	{
		Vector2f nearest=null;
		for(int i=0;i<collisionStart.size();i++)
		{
			Vector2f collision=findPointOfIntersection(start,end, collisionStart.get(i), collisionEnd.get(i));
			
			if(collision!=null && (nearest==null || nearest.subtract(start).length() > collision.subtract(start).length()))
				nearest=collision;
				
		}
		if(enemy)
		{
			Vector2f nearestEnemyIntersect=null;
			Enemy nearestEnemy=null;
			for(Enemy e:enemyList)
			{
				if(deadEnemyList.contains(e)==true)
				{
					continue;
				}
				else
				{
					Vector2f enemySize=new Vector2f(0.2f,0.2f);
					Vector3f enemyPos3f=e.getTransform().getTranslation();
					Vector2f enemyPos2f=new Vector2f(enemyPos3f.getX(),enemyPos3f.getZ());
					Vector2f collision=lineIntersectRect(start,end,enemyPos2f, enemySize);
				
					if(collision!=null && (nearestEnemyIntersect==null || nearestEnemyIntersect.subtract(start).length() > collision.subtract(start).length()))
					nearestEnemyIntersect=collision;
				
					if(nearestEnemyIntersect==collision)
						nearestEnemy=e;
				}
			}
			if(nearestEnemyIntersect!=null && (nearest==null || nearestEnemyIntersect.subtract(start).length()<nearest.subtract(start).length()))
			{
				if(nearestEnemy!=null)
				{
					nearestEnemy.damage(player.getDamage());
					AudioUtil.playAudio(GUNSHOT_AUDIO, 0);
				}
			}
		}
			
		return nearest;
	}
	
	public void damage(int dmg)
	{
		player.damage(dmg);
	}
	
	public void gainAmmo(int val)
	{
		player.gainAmmo(val);
	}
	
	public Vector2f lineIntersectRect(Vector2f lineStart, Vector2f lineEnd,Vector2f pos, Vector2f size)
	{
		Vector2f res=null;
		
		//1st side
		Vector2f collision=findPointOfIntersection(lineStart,lineEnd,pos, new Vector2f(pos.getX()+size.getX(), pos.getY()));
		
		if(collision!=null && (res==null || res.subtract(lineStart).length() > collision.subtract(lineStart).length()))
			res=collision;
		
		//2nd side
		collision=findPointOfIntersection(lineStart,lineEnd,pos, new Vector2f(pos.getX(), pos.getY()+size.getY()));
		
		if(collision!=null && (res==null || res.subtract(lineStart).length() > collision.subtract(lineStart).length()))
			res=collision;
		
		//3rd side
		collision=findPointOfIntersection(lineStart,lineEnd,new Vector2f(pos.getX(), pos.getY()+size.getY()), pos.add(size));
				
		if(collision!=null && (res==null || res.subtract(lineStart).length() > collision.subtract(lineStart).length()))
				res=collision;
		
		//4th side
		collision=findPointOfIntersection(lineStart,lineEnd,new Vector2f(pos.getX()+size.getX(), pos.getY()), pos.add(size));
						
		if(collision!=null && (res==null || res.subtract(lineStart).length() > collision.subtract(lineStart).length()))
				res=collision;
		
		return res;
	}
	
	private Vector2f findPointOfIntersection(Vector2f lineStart1, Vector2f lineEnd1,Vector2f lineStart2, Vector2f lineEnd2)
	{
		Vector2f line1=lineEnd1.subtract(lineStart1);
		Vector2f line2=lineEnd2.subtract(lineStart2);
		
		float cross=line1.cross(line2);
		if(cross==0)
			return null;
		
		Vector2f distanceStart=lineStart2.subtract(lineStart1);
		
		float num1=distanceStart.cross(line2)/cross;
		float num2=distanceStart.cross(line1)/cross;
			
		if(num1<1.0f && num1>0.0f && num2>0.0f && num2<1.0f)
			return lineStart1.add(line1.multiply(num1));
		
		return null;
			
	}
	
	public void update()
	{
		
		for(Enemy enemy:enemyList)
		{
			enemy.update();
		}
		player.update();
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
		
		shaderGrafitti.bind();
		shaderGrafitti.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), materialGrafitti);
		meshGrafitti.draw();
		
		shaderPictureFrame.bind();
		shaderPictureFrame.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), materialPictureFrame);
		meshPictureFrame.draw();
		for(Enemy enemy:enemyList)
		{
			enemy.render();
		}
		player.render();
		
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
	
	private void generatePictureFrame(Mesh m)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();

		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==-16777216 || level.getPixel(i, j)==-65536 || level.getPixel(i, j)==-16711936)
					continue;
				
				float xHigh=1;
				float xLow=0;
				float yHigh=1;
				float yLow=0;
				
				//Wall
				if(level.getPixel(i, j)==-1  || level.getPixel(i, j)==-16776961 || level.getPixel(i, j)==-65281)
				{
					if(level.getPixel(i, j-1)==-16711936)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
					if(level.getPixel(i, j+1)==-16711936)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}
					
				
					if(level.getPixel(i-1, j)==-16711936)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}	
					
					
					if(level.getPixel(i+1, j)==-16711936)
					{
						collisionStart.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
				}
			}
		}
		
		Vertex[] verticesArray=new Vertex[vertices.size()];
		Integer[] indicesArray=new Integer[indices.size()];
		
		vertices.toArray(verticesArray);
		indices.toArray(indicesArray);
		m.addVertices(verticesArray, Util.toIntArray(indicesArray));
		
	}
	
	private void generateGrafitti(Mesh m)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();

		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				
				if(level.getPixel(i, j)==-16777216 || level.getPixel(i, j)==-65536 || level.getPixel(i, j)==-16711936)
					continue;
				
				float xHigh=1;
				float xLow=0;
				float yHigh=1;
				float yLow=0;
				
				//Wall
				if(level.getPixel(i, j)==-1 || level.getPixel(i, j)==-16776961 || level.getPixel(i, j)==-65281)
				{
					if(level.getPixel(i, j-1)==-65536)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
					if(level.getPixel(i, j+1)==-65536)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}
					
				
					if(level.getPixel(i-1, j)==-65536)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}	
					
					
					if(level.getPixel(i+1, j)==-65536)
					{
						collisionStart.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
				}
			}
		}
		
		Vertex[] verticesArray=new Vertex[vertices.size()];
		Integer[] indicesArray=new Integer[indices.size()];
		
		vertices.toArray(verticesArray);
		indices.toArray(indicesArray);
		m.addVertices(verticesArray, Util.toIntArray(indicesArray));
		
	}
	
	private void generateWall(Mesh mNormal)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();

		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==-16777216 || level.getPixel(i, j)==-65536 || level.getPixel(i, j)==-16711936)
					continue;
				
				float xHigh=1;
				float xLow=0;
				float yHigh=1;
				float yLow=0;
				
				//Wall
				if(level.getPixel(i, j)==-1 || level.getPixel(i, j)==-16776961 || level.getPixel(i, j)==-65281)
				{
					if(level.getPixel(i, j-1)==-16777216)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
					if(level.getPixel(i, j+1)==-16777216)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}
					
				
					if(level.getPixel(i-1, j)==-16777216)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}	
					
					
					if(level.getPixel(i+1, j)==-16777216)
					{
						collisionStart.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
				}
			}
		}
		
		Vertex[] verticesArray=new Vertex[vertices.size()];
		Integer[] indicesArray=new Integer[indices.size()];
		
		vertices.toArray(verticesArray);
		indices.toArray(indicesArray);
		mNormal.addVertices(verticesArray, Util.toIntArray(indicesArray));
		
	}
	
	private void generateFloor(Mesh m)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==-16777216 || level.getPixel(i, j)==-65536 || level.getPixel(i, j)==-16711936)
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
				Node n=new Node();
				n.pos=new Vector2f((i+0.5f)*CUBE_WIDTH, (j+0.5f)*CUBE_LENGTH);
				if(level.getPixel(i-1, j)==-1)
				{
					Node left=new Node();
					left.pos=new Vector2f((i-0.5f)*CUBE_WIDTH,(j+0.5f)*CUBE_LENGTH);
					n.neighbours.add(left);
				}
				if(level.getPixel(i+1, j)==-1)
				{
					Node right=new Node();
					right.pos=new Vector2f((i+1.5f)*CUBE_WIDTH, (j+0.5f)*CUBE_LENGTH);
					n.neighbours.add(right);
				}
				if(level.getPixel(i, j-1)==-1)
				{
					Node up=new Node();
					up.pos=new Vector2f((i+0.5f)*CUBE_WIDTH, (j-0.5f)*CUBE_LENGTH);
					n.neighbours.add(up);
				}
				if(level.getPixel(i, j+1)==-1)
				{
					Node down=new Node();
					down.pos=new Vector2f((i+0.5f)*CUBE_WIDTH, (j+1.5f)*CUBE_LENGTH);
					n.neighbours.add(down);
				}
				nodes.add(n);
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
				if(level.getPixel(i, j)==-16777216 || level.getPixel(i, j)==-65536 || level.getPixel(i, j)==-16711936)
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
				//System.out.println(level.getPixel(i, j));
			}
		}
		
		Vertex[] verticesArray=new Vertex[vertices.size()];
		Integer[] indicesArray=new Integer[indices.size()];
		
		vertices.toArray(verticesArray);
		indices.toArray(indicesArray);
		m.addVertices(verticesArray, Util.toIntArray(indicesArray));
	}
	

}
