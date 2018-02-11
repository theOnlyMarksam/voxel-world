package model.mesh;

public class VertexList {
    private static final int VERTEX_SIZE = 6;

    private int vertexCount;
    private float[] vertices;

    public VertexList() {
        vertexCount = 0;
        vertices = new float[VERTEX_SIZE];
    }

    public void add(float x, float y, float z, float u, float v, float w) {
        if (vertices.length <= vertexCount * VERTEX_SIZE) {
            float[] tmp = new float[vertexCount * VERTEX_SIZE * 2];
            System.arraycopy(vertices, 0, tmp, 0, vertexCount * VERTEX_SIZE);
            vertices = tmp;
            push(x, y, z, u, v, w);
        } else {
            push(x, y, z, u, v, w);
        }
    }

    public float[] getDenseVertexArray() {
        float[] result = new float[vertexCount * VERTEX_SIZE];
        System.arraycopy(vertices, 0, result, 0, vertexCount * VERTEX_SIZE);
        return result;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void clear() {
        vertexCount = 0;
    }

    private void push(float x, float y, float z, float u, float v, float w) {
        vertices[vertexCount * VERTEX_SIZE] = x;
        vertices[vertexCount * VERTEX_SIZE + 1] = y;
        vertices[vertexCount * VERTEX_SIZE + 2] = z;
        vertices[vertexCount * VERTEX_SIZE + 3] = u;
        vertices[vertexCount * VERTEX_SIZE + 4] = v;
        vertices[vertexCount * VERTEX_SIZE + 5] = w;
        vertexCount++;
    }
}
