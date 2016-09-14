package com.base.engine;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

public class Shader 
{
	private int program;
	public Shader()
	{
		program=glCreateProgram();
		if(program==0)
		{
			System.err.println("Shader creation failed: Could not find valid memory location in constructor");
			System.exit(1);
		}
	}
	
	
	public void addVertexShader(String text)
	{
		addProgram(text, GL_VERTEX_SHADER);
	}
	
	public void addFragmentShader(String text)
	{
		addProgram(text, GL_FRAGMENT_SHADER);
	}
	
	public void addGeometryShader(String text)
	{
		addProgram(text, GL_GEOMETRY_SHADER);
	}
	
	@SuppressWarnings("deprecation")
	private void addProgram(String text, int type)
	{
		int shader=glCreateShader(type);
		if(shader==0)
		{
			System.err.println("Shader creation failed: Could not find valid memory location when adding shader");
			System.exit(1);
		}
		
		glShaderSource(shader,text);
		glCompileShader(shader);
		
		if(glGetShader(shader, GL_COMPILE_STATUS)==0)
		{
			System.err.println(glGetShaderInfoLog(shader,1024));
			System.exit(1);
		}
		
		glAttachShader(shader,program);
	}
}
