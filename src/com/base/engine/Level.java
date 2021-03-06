package com.base.engine;

import java.util.ArrayList;

import javax.sound.sampled.Clip;


//Color codes:
/*

Player=-65281
Enemies=-16776961
Ammo=-16711681
Bananas=-256
Black=-16777216
White=-1

*/
public class Level 
{
	private static final float CUBE_WIDTH=1;
	private static final float CUBE_HEIGHT=1;
	private static final float CUBE_LENGTH=1;
	
	private static final int BLACK=-16777216;
	private static final int WHITE=-1;
	private static final int PLAYER=-65281;
	private static final int AMMO=-16711681;
	private static final int BANANAS=-256;
	private static final int ENEMIES=-16776961;
	private static final int GRAFITTI=-65536;
	private static final int PICTURE_FRAME=-16711936;
	private static final int EXIT_POINT=-16733696;
	private static final int EXPLOSIVE_BARRELS=-14505234;
	private static final int MOSS=-1118550;
	
	private Bitmap level;
	private Mesh meshWall, meshFloor,meshCeiling, meshGrafitti, meshPictureFrame, meshExitPoint, meshMoss;
	private Shader shaderWall,shaderFloor,shaderCeiling, shaderGrafitti, shaderPictureFrame, shaderExitPoint, shaderMoss;
	private Material materialWall, materialFloor, materialCeiling, materialGrafitti, materialPictureFrame, materialExitPoint, materialMoss;
	
	private ArrayList<Vector2f> collisionStart;
	private ArrayList<Vector2f> collisionEnd;
	

	
	private ArrayList<Banana> bananas;
	private ArrayList<Banana> bananasEaten;
	
	private ArrayList<Ammo> ammo;
	private ArrayList<Ammo> ammoCollected;
	
	private ArrayList<ExplosiveBarrel> barrelsInLevel;
	private ArrayList<ExplosiveBarrel> barrelsExploded;
	
	private ArrayList<Vector3f> exitPoints;
	
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
		exitPoints=new ArrayList<Vector3f>();
		ammo=new ArrayList<Ammo>();
		ammoCollected=new ArrayList<Ammo>();
		
		barrelsInLevel=new ArrayList<ExplosiveBarrel>();
		barrelsExploded=new ArrayList<ExplosiveBarrel>();
		
		bananas=new ArrayList<Banana>();
		bananasEaten=new ArrayList<Banana>();
		
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
		meshExitPoint=new Mesh();
		meshMoss=new Mesh();
		
		shaderWall=new BasicShader();
		shaderFloor=new BasicShader();
		shaderCeiling=new BasicShader();
		shaderGrafitti=new BasicShader();
		shaderPictureFrame=new BasicShader();
		shaderExitPoint=new BasicShader();
		shaderMoss=new BasicShader();
		
		materialWall=new Material(ResourceLoader.loadTexture("Wall.png"));
		materialFloor=new Material(ResourceLoader.loadTexture("floor.png"));
		materialCeiling=new Material(ResourceLoader.loadTexture("Ceiling.png"));
		materialGrafitti=new Material(ResourceLoader.loadTexture("Wall_graffiti.png"));
		materialPictureFrame=new Material(ResourceLoader.loadTexture("Wall_Painting.png"));
		materialExitPoint=new Material(ResourceLoader.loadTexture("door.png"));
		materialMoss=new Material(ResourceLoader.loadTexture("Wall_Moss.png"));
		
		transform=new Transform();
		
		generateFloor(meshFloor);
		generateWall(meshWall);
		generateCeiling(meshCeiling);
		generateGrafitti(meshGrafitti);
		generatePictureFrame(meshPictureFrame);
		generatePlayer();
		generateEnemies();
		generateBananas();
		generateAmmo();
		generateExitPoints(meshExitPoint);
		generateBarrels();
		generateMoss(meshMoss);
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	private void generateEnemies()
	{
		int id=0;
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==ENEMIES)
				{
					Transform enemyT=new Transform();
					enemyT.setTranslation(new Vector3f((i+0.5f)*CUBE_HEIGHT,0, (j+0.5f)*CUBE_LENGTH));
					enemyList.add(new Enemy(enemyT,id));
					id++;
				}
				
			}
		}
	}
	
	private void generateBananas()
	{
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==BANANAS)
				{
					bananas.add(new Banana(new Vector3f((i+0.5f)*CUBE_HEIGHT,0, (j+0.5f)*CUBE_LENGTH)));
				}
				
			}
		}
	}
	
	private void generateBarrels()
	{
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==EXPLOSIVE_BARRELS)
				{
					barrelsInLevel.add(new ExplosiveBarrel(new Vector3f((i+0.5f)*CUBE_HEIGHT,0, (j+0.5f)*CUBE_LENGTH)));
				}
			}
		}
	}
	
	public void openDoors(Vector3f position)
	{
		for(Vector3f e:exitPoints)
		{
			if(e.subtract(position).length()<1.0f)
			{
				Game.setIsRunning(false);
				AudioUtil.stopAudio(Game.getCurrentClip());
				Game.loadNextLevel();
			}
		}
	}
	
	private void generateAmmo()
	{
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==AMMO)
				{
					ammo.add(new Ammo(new Vector3f((i+0.5f)*CUBE_HEIGHT,-0.125f, (j+0.5f)*CUBE_LENGTH)));
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
				if(level.getPixel(i, j)==PLAYER)
				{
					player=new Player(new Vector3f((i+0.5f)*CUBE_HEIGHT,0.4375f,(j+0.5f)*CUBE_LENGTH));
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
					if(level.getPixel(i, j)==GRAFITTI || level.getPixel(i, j)==PICTURE_FRAME || level.getPixel(i, j)==BLACK || level.getPixel(i, j)==EXIT_POINT || level.getPixel(i, j)==MOSS)
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
			Vector2f nearestBarrelIntersect=null;
			ExplosiveBarrel nearestBarrel=null;
			for(ExplosiveBarrel eb:barrelsInLevel)
			{
				if(barrelsExploded.contains(eb)==true)
				{
					continue;
				}
				else
				{
					Vector2f enemySize=new Vector2f(eb.BARREL_WIDTH,eb.BARREL_LENGTH);
					Vector3f enemyPos3f=eb.getTransform().getTranslation();
					Vector2f enemyPos2f=new Vector2f(enemyPos3f.getX(),enemyPos3f.getZ());
					Vector2f collision=lineIntersectRect(start,end,enemyPos2f, enemySize);
				
					if(collision!=null && (nearestBarrelIntersect==null || nearestBarrelIntersect.subtract(start).length() > collision.subtract(start).length()))
						nearestBarrelIntersect=collision;
				
					if(nearestBarrelIntersect==collision)
						nearestBarrel=eb;
				}
			}
			
			if(nearestBarrelIntersect!=null && (nearest==null || nearestBarrelIntersect.subtract(start).length()<nearest.subtract(start).length()))
			{
				if(nearestBarrel!=null)
				{
					nearestBarrel.damage(player.getDamage());
				}
			}
			
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
					Vector2f enemySize=new Vector2f(e.ENEMY_WIDTH,e.ENEMY_LENGTH);
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
		Vector2f collision=findPointOfIntersection(lineStart,lineEnd,pos, new Vector2f(pos.getX()-size.getX(), pos.getY()));
		
		if(collision!=null && (res==null || res.subtract(lineStart).length() > collision.subtract(lineStart).length()))
			res=collision;
		
		//2nd side
		collision=findPointOfIntersection(lineStart,lineEnd,pos, new Vector2f(pos.getX()+size.getX(), pos.getY()+size.getY()));
		
		if(collision!=null && (res==null || res.subtract(lineStart).length() > collision.subtract(lineStart).length()))
			res=collision;
		
		//3rd side
		collision=findPointOfIntersection(lineStart,lineEnd,new Vector2f(pos.getX()-size.getX(), pos.getY()+size.getY()), pos.add(size));
				
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
		for(ExplosiveBarrel eb:barrelsInLevel)
			eb.update();
		for(ExplosiveBarrel eb:barrelsExploded)
			barrelsInLevel.remove(eb);
		for(Enemy enemy:enemyList)
			enemy.update();
		for(Banana banana:bananas)
			banana.update();
		for(Banana b:bananasEaten)
			bananas.remove(b);
		for(Ammo a:ammo)
			a.update();
		for(Ammo aC:ammoCollected)
			ammo.remove(aC);
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
		
		shaderExitPoint.bind();
		shaderExitPoint.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), materialExitPoint);
		meshExitPoint.draw();
		
		shaderMoss.bind();
		shaderMoss.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), materialMoss);
		meshMoss.draw();
		
		for(ExplosiveBarrel eb:barrelsInLevel)
		{
			eb.render();
		}
		for(Banana banana:bananas)
			banana.render();
		for(Enemy enemy:enemyList)
		{
			if(!deadEnemyList.contains(enemy))
				enemy.render();
		}
		for(Enemy enemy:deadEnemyList)
			enemy.render();
		for(Ammo a:ammo)
			a.render();
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
				if(level.getPixel(i, j)==BLACK || level.getPixel(i, j)==PICTURE_FRAME || level.getPixel(i, j)==GRAFITTI || level.getPixel(i, j)==EXIT_POINT || level.getPixel(i, j)==MOSS)
					continue;
				
				float xHigh=1;
				float xLow=0;
				float yHigh=1;
				float yLow=0;
				
				//Wall
				if(level.getPixel(i, j)==WHITE  || level.getPixel(i, j)==ENEMIES || level.getPixel(i, j)==PLAYER || level.getPixel(i, j)==AMMO || level.getPixel(i, j)==BANANAS || level.getPixel(i, j)==EXPLOSIVE_BARRELS)
				{
					if(level.getPixel(i, j-1)==PICTURE_FRAME)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
					if(level.getPixel(i, j+1)==PICTURE_FRAME)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}
					
				
					if(level.getPixel(i-1, j)==PICTURE_FRAME)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}	
					
					
					if(level.getPixel(i+1, j)==PICTURE_FRAME)
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
				
				if(level.getPixel(i, j)==BLACK || level.getPixel(i, j)==GRAFITTI || level.getPixel(i, j)==PICTURE_FRAME || level.getPixel(i, j)==EXIT_POINT || level.getPixel(i, j)==MOSS)
					continue;
				
				float xHigh=1;
				float xLow=0;
				float yHigh=1;
				float yLow=0;
				
				//Wall
				if(level.getPixel(i, j)==WHITE || level.getPixel(i, j)==PLAYER || level.getPixel(i, j)==ENEMIES || level.getPixel(i, j)==AMMO || level.getPixel(i, j)==BANANAS || level.getPixel(i, j)==EXPLOSIVE_BARRELS)
				{
					if(level.getPixel(i, j-1)==GRAFITTI)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
					if(level.getPixel(i, j+1)==GRAFITTI)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}
					
				
					if(level.getPixel(i-1, j)==GRAFITTI)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}	
					
					
					if(level.getPixel(i+1, j)==GRAFITTI)
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
	
	private void generateExitPoints(Mesh m)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();

		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==BLACK || level.getPixel(i, j)==GRAFITTI || level.getPixel(i, j)==PICTURE_FRAME || level.getPixel(i, j)==EXIT_POINT || level.getPixel(i, j)==MOSS)
					continue;
				
				float xHigh=1;
				float xLow=0;
				float yHigh=1;
				float yLow=0;
				
				//Wall
				if(level.getPixel(i, j)==WHITE || level.getPixel(i, j)==PLAYER || level.getPixel(i, j)==ENEMIES || level.getPixel(i, j)==AMMO || level.getPixel(i, j)==BANANAS || level.getPixel(i, j)==EXPLOSIVE_BARRELS)
				{
					if(level.getPixel(i, j-1)==EXIT_POINT)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						exitPoints.add(new Vector3f((i + 0.5f) * CUBE_WIDTH, 0, (j + 0.5f) * CUBE_LENGTH));
					}
					
					if(level.getPixel(i, j+1)==EXIT_POINT)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						exitPoints.add(new Vector3f((i + 0.5f) * CUBE_WIDTH, 0, (j + 0.5f) * CUBE_LENGTH));
					}
					
				
					if(level.getPixel(i-1, j)==EXIT_POINT)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						exitPoints.add(new Vector3f((i + 0.5f) * CUBE_WIDTH, 0, (j + 0.5f) * CUBE_LENGTH));
					}	
					
					
					if(level.getPixel(i+1, j)==EXIT_POINT)
					{
						collisionStart.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						exitPoints.add(new Vector3f((i + 0.5f) * CUBE_WIDTH, 0, (j + 0.5f) * CUBE_LENGTH));
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
	
	private void generateMoss(Mesh m)
	{
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();
		for(int i=0;i<level.getWidth();i++)
		{
			for(int j=0;j<level.getHeight();j++)
			{
				if(level.getPixel(i, j)==BLACK || level.getPixel(i, j)==GRAFITTI || level.getPixel(i, j)==PICTURE_FRAME || level.getPixel(i, j)==EXIT_POINT || level.getPixel(i, j)==MOSS)
					continue;
				
				float xHigh=1;
				float xLow=0;
				float yHigh=1;
				float yLow=0;
				
				//Wall
				if(level.getPixel(i, j)==WHITE || level.getPixel(i, j)==PLAYER || level.getPixel(i, j)==ENEMIES || level.getPixel(i, j)==AMMO || level.getPixel(i, j)==BANANAS || level.getPixel(i, j)==EXPLOSIVE_BARRELS)
				{
					if(level.getPixel(i, j-1)==MOSS)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
					}
					
					if(level.getPixel(i, j+1)==MOSS)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}
					
				
					if(level.getPixel(i-1, j)==MOSS)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);

						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(xLow,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yHigh)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(xHigh,yLow)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(xLow,yLow)));
					}	
					
					
					if(level.getPixel(i+1, j)==MOSS)
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
				if(level.getPixel(i, j)==BLACK || level.getPixel(i, j)==GRAFITTI || level.getPixel(i, j)==PICTURE_FRAME || level.getPixel(i, j)==EXIT_POINT || level.getPixel(i, j)==MOSS)
					continue;
				//Wall
				if(level.getPixel(i, j)==WHITE || level.getPixel(i, j)==ENEMIES || level.getPixel(i, j)==PLAYER  || level.getPixel(i, j)==AMMO || level.getPixel(i, j)==BANANAS || level.getPixel(i, j)==EXPLOSIVE_BARRELS)
				{
					if(level.getPixel(i, j-1)==BLACK)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(1,1)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(0,1)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(0,0)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(1,0)));
					}
					
					if(level.getPixel(i, j+1)==BLACK)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(0,1)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(1,1)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(1,0)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(0,0)));
					}
					
				
					if(level.getPixel(i-1, j)==BLACK)
					{
						collisionStart.add(new Vector2f(i*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f(i*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), true);
						
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(0,1)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(1,1)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(1,0)));
						vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(0,0)));
					}	
					
					
					if(level.getPixel(i+1, j)==BLACK)
					{
						collisionStart.add(new Vector2f((i+1)*CUBE_WIDTH,j*CUBE_LENGTH));
						collisionEnd.add(new Vector2f((i+1)*CUBE_WIDTH,(j+1)*CUBE_LENGTH));
						addFace(indices, vertices.size(), false);
						
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(1,1)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(0,1)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(0,0)));
						vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(1,0)));
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
				if(level.getPixel(i, j)==BLACK)
					continue;
				
				//Floor
				addFace(indices, vertices.size(), true);
				
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(0,0)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,j*CUBE_LENGTH), new Vector2f(1,0)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(1,1)));
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,0,(j+1)*CUBE_LENGTH), new Vector2f(0,1)));
				//TODO: Pathfinding
				/*Node n=new Node();
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
				nodes.add(n);*/
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
				//System.out.println(level.getPixel(i, j));
				if(level.getPixel(i, j)==BLACK)
					continue;
				
				addFace(indices, vertices.size(), false);
				
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(0,0)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,j*CUBE_LENGTH), new Vector2f(1,0)));
				vertices.add(new Vertex(new Vector3f((i+1)*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(1,1)));
				vertices.add(new Vertex(new Vector3f(i*CUBE_WIDTH,CUBE_HEIGHT,(j+1)*CUBE_LENGTH), new Vector2f(0,1)));
			}
		}
		
		Vertex[] verticesArray=new Vertex[vertices.size()];
		Integer[] indicesArray=new Integer[indices.size()];
		
		vertices.toArray(verticesArray);
		indices.toArray(indicesArray);
		m.addVertices(verticesArray, Util.toIntArray(indicesArray));
	}
	
	public void removeBananaOnConsumed(Banana b)
	{
		bananasEaten.add(b);
	}
	
	public void removeAmmoOnCollected(Ammo a)
	{
		ammoCollected.add(a);
	}
	
	public void removeBarrelsOnExplosion(ExplosiveBarrel eb)
	{
		barrelsExploded.add(eb);
	}

}
