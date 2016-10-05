package com.base.engine;

public class Vertex 
{
	
	public static final int SIZE=8;
	private Vector3f pos;
	private Vector2f tex;
	private Vector3f normal;
	
	public Vertex(Vector3f pos)
	{
		this(pos,new Vector2f(0,0));
	}
	
	public Vertex(Vector3f pos, Vector2f tex)
	{
		this(pos,tex, new Vector3f(0,0,0));
	}
	
	public Vertex(Vector3f pos, Vector2f tex,Vector3f normal)
	{
		this.pos=pos;
		this.tex=tex;
		this.normal=normal;
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

	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}
}
