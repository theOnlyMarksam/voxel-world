package entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PerspectiveCamera {

    private Vector3f position = new Vector3f();
    private Vector3f lookAt = new Vector3f();

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    public PerspectiveCamera(
            Vector3f position
            , Vector3f lookAt
            , float near
            , float far
            , float fov
            , float aspectRatio
    ) {
        this.position.set(position);
        this.lookAt.set(lookAt);

        projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, near, far);
        viewMatrix = new Matrix4f().lookAt(position, lookAt, up);
    }

    public void update() {
        Vector3f look = new Vector3f().set(position);
        viewMatrix = new Matrix4f().lookAt(position, look.add(lookAt), up);
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public void setLookAt(Vector3f lookAt) {
        this.lookAt.set(lookAt);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
