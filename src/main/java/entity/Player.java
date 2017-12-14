package entity;

import main.Game;
import org.joml.Vector3f;

public class Player {

    private Vector3f position;
    private Vector3f lookAt = new Vector3f(0.0f, 0.0f, -1.0f);
    private Vector3f forwardVector;
    private Vector3f leftVector = new Vector3f();
    private Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
    private PerspectiveCamera camera;

    public Player(Vector3f position) {
        this.position = position;

        float aspect = (float) Game.WIDTH / (float)Game.HEIGHT;
        this.camera = new PerspectiveCamera(position, lookAt, 0.1f, 500f, (float)Math.PI/3, aspect);

        forwardVector = new Vector3f(lookAt.x, 0.0f, lookAt.z).normalize();

        leftVector.set(forwardVector);
        leftVector = leftVector.cross(upVector).normalize();
    }

    public void moveForward(float distance) {
        changePosition(distance, new Vector3f().set(forwardVector));
    }

    public void moveLeft(float distance) {
        changePosition(distance, new Vector3f().set(leftVector));
    }

    public void moveUp(float distance) {
        changePosition(distance, new Vector3f().set(upVector));
    }

    public void rotateView(float xRot, float yRot) {
        lookAt.rotateAxis(yRot, upVector.x, upVector.y, upVector.z).normalize();

        forwardVector.x = lookAt.x;
        forwardVector.z = lookAt.z;
        forwardVector.normalize();

        leftVector.set(forwardVector);
        leftVector = leftVector.cross(upVector).normalize();

        lookAt.rotateAxis(xRot, leftVector.x, leftVector.y, leftVector.z);

        // update the camera
        camera.setLookAt(lookAt);
    }

    private void changePosition(float distance, Vector3f transformation) {
        position.add(transformation.mul(distance));
        camera.setPosition(position);
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }
}
