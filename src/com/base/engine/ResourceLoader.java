package com.base.engine;

import java.io.BufferedReader;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glTexImage2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.newdawn.slick.opengl.TextureLoader;

public class ResourceLoader 
{
	public static Texture loadTexture(String fileName)
	{
		String[] splitArray=fileName.split("\\.");
		String ext=splitArray[splitArray.length-1];
		
		try
		{
			BufferedImage image = ImageIO.read(new File("./res/textures/" + fileName));

			boolean hasAlpha = image.getColorModel().hasAlpha();

			int[] pixels = image.getRGB(0, 0, image.getWidth(),
										image.getHeight(), null, 0, image.getWidth());

			ByteBuffer buffer = Util.createByteBuffer(image.getWidth() * image.getHeight() * 4);

			for (int y = 0; y < image.getHeight(); y++)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					int pixel = pixels[y * image.getWidth() + x];

					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) ((pixel >> 0) & 0xFF));
					if (hasAlpha)
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					else
						buffer.put((byte) (0xFF));
				}
			}

			buffer.flip();

//			this.width = image.getWidth();
//			this.height = image.getHeight();
//			this.id = Engine.getRenderer().createTexture(width, height, buffer, true, true);
//			this.frameBuffer = 0;
//			this.pixels = null;

			int texture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

			//return texture;
			//int id=TextureLoader.getTexture(ext, new FileInputStream(new File("./res/textures/" + fileName))).getTextureID();
			return new Texture(texture);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	
	public static String loadShader(String fileName)
	{
		StringBuilder shaderSource=new StringBuilder();
		BufferedReader shaderReader=null;
		
		try
		{
			shaderReader=new BufferedReader(new FileReader("./res/shaders/" + fileName));
			String line;
			while((line=shaderReader.readLine())!=null)
			{
				shaderSource.append(line).append("\n");
			}
			shaderReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);;
		}
		
		return shaderSource.toString();
	}
	
	public static Mesh loadMesh(String fileName)
	{
		String[] splitArray=fileName.split("\\.");
		String ext=splitArray[splitArray.length-1];
		
		if(!ext.equals("obj"))
		{
			System.err.println("Error: File format not supported");
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		ArrayList<Vertex> vertices= new ArrayList<Vertex>();
		ArrayList<Integer> indices= new ArrayList<Integer>();
		
		BufferedReader meshReader=null;
		
		try
		{
			meshReader=new BufferedReader(new FileReader("./res/models/" + fileName));
			String line;
			while((line=meshReader.readLine())!=null)
			{
				String[] tokens=line.split(" ");
				tokens=Util.removeEmptyStrings(tokens);
				if(tokens.length==0 || tokens[0].equals("#"))
				{
					continue;
				}
				else if(tokens[0].equals("v"))
				{
					vertices.add(new Vertex(new Vector3f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]), Float.valueOf(tokens[3]))));
				}
				else if(tokens[0].equals("f"))
				{
					indices.add(Integer.parseInt(tokens[1].split("/")[0])-1);
					indices.add(Integer.parseInt(tokens[2].split("/")[0])-1);
					indices.add(Integer.parseInt(tokens[3].split("/")[0])-1);
					
					if(tokens.length>4)
					{
						indices.add(Integer.parseInt(tokens[1].split("/")[0])-1);
						indices.add(Integer.parseInt(tokens[3].split("/")[0])-1);
						indices.add(Integer.parseInt(tokens[4].split("/")[0])-1);
					}
				}
			}
			meshReader.close();
			
			Mesh res=new Mesh();
			Vertex[] vertexData=new Vertex[vertices.size()];
			vertices.toArray(vertexData);
			
			Integer[] indexData=new Integer[indices.size()];
			indices.toArray(indexData);
			
			res.addVertices(vertexData, Util.toIntArray(indexData), true);
			return res;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);;
		}
		return null;
		
		
	}

}
