package com.base.engine;

public class Vertex 
{
	
	public static final int SIZE=5;
	private Vector3f pos;
	private Vector2f tex;
	
	public Vertex(Vector3f pos)
	{
		this(pos,new Vector2f(0,0));
	}
	
	public Vertex(Vector3f pos, Vector2f tex)
	{
		this.pos=pos;
		this.tex=tex;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector2f getTex() {
		return tex;
	}

	public void setTex(Vector2f tex) {
		this.tex = tex;
	}
}
