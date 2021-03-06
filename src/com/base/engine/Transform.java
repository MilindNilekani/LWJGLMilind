package com.base.engine;

public class Transform 
{
	private static Camera camera;
	
	private static float zN;
	private static float zF;
	private static float width;
	private static float height;
	private static float fov;
	
	private Vector3f translation;
	private Vector3f rotation;
	private Vector3f scale;
	public Transform()
	{
		translation=new Vector3f(0,0,0);
		rotation=new Vector3f(0,0,0);
		scale=new Vector3f(1,1,1);
	}
	public Vector3f getTranslation() {
		return translation;
	}
	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}
	
	public Matrix4f getProjectedTransformation()
	{
		Matrix4f transformationMatrix=getTransformation();
		Matrix4f projectionMatrix= new Matrix4f().initProjection(fov, zN, zF, width, height);
		Matrix4f cameraRotation=new Matrix4f().initCameraRotation(camera.getForward(), camera.getUp());
		Matrix4f cameraTranslation=new Matrix4f().initTranslation(-camera.getPos().getX(),-camera.getPos().getY(),-camera.getPos().getZ());
		
		return projectionMatrix.multiply(cameraRotation.multiply(cameraTranslation.multiply(transformationMatrix)));
		//return projectionMatrix.multiply(transformationMatrix);
	}
	
	public static void setProjection(float fov, float zN, float zF, float width, float height)
	{
		Transform.fov=fov;
		Transform.width=width;
		Transform.height=height;
		Transform.zN=zN;
		Transform.zF=zF;
	}
	
	public void setTranslation(float x, float y, float z) {
		this.translation = new Vector3f(x,y,z);
	}
	
	public Matrix4f getTransformation()
	{
		Matrix4f translationMatrix=new Matrix4f().initTranslation(translation.getX(), translation.getY(), translation.getZ());
		Matrix4f rotationMatrix=new Matrix4f().initRotation(rotation.getX(), rotation.getY(), rotation.getZ());
		Matrix4f scaleMatrix=new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());
		return translationMatrix.multiply(rotationMatrix.multiply(scaleMatrix));
	}
	public Vector3f getRotation() {
		return rotation;
	}
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public void setRotation(float x, float y, float z) {
		this.rotation = new Vector3f(x,y,z);
	}
	public Vector3f getScale() {
		return scale;
	}
	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	public void setScale(float x, float y, float z) {
		this.scale = new Vector3f(x,y,z);
	}
	public static Camera getCamera() {
		return camera;
	}
	public static void setCamera(Camera camera) {
		Transform.camera = camera;
	}
	
	
}
