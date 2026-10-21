package com.l4d1.android;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WorldRenderer implements GLSurfaceView.Renderer {

    private final float[] projection = new float[16];
    private final float[] view = new float[16];
    private final float[] model = new float[16];
    private final float[] mv = new float[16];
    private final float[] mvp = new float[16];

    private int program;
    private int positionHandle;
    private int colorHandle;
    private int matrixHandle;

    private FloatBuffer vertices;

    public volatile float yaw = 0;
    public volatile float pitch = 0;
    public volatile float moveX = 0;
    public volatile float moveY = 0;

    private float playerX = 0;
    private float playerZ = 4;

    private long lastFrame;

    private static final float[] CUBE = {
        -0.5f,-0.5f, 0.5f,  0.5f,-0.5f, 0.5f,  0.5f, 0.5f, 0.5f,
        -0.5f,-0.5f, 0.5f,  0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f,

        0.5f,-0.5f,-0.5f, -0.5f,-0.5f,-0.5f, -0.5f, 0.5f,-0.5f,
        0.5f,-0.5f,-0.5f, -0.5f, 0.5f,-0.5f,  0.5f, 0.5f,-0.5f,

        -0.5f,-0.5f,-0.5f, -0.5f,-0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
        -0.5f,-0.5f,-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,-0.5f,

        0.5f,-0.5f, 0.5f, 0.5f,-0.5f,-0.5f, 0.5f, 0.5f,-0.5f,
        0.5f,-0.5f, 0.5f, 0.5f, 0.5f,-0.5f, 0.5f, 0.5f, 0.5f,

        -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,-0.5f,
        -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,-0.5f, -0.5f, 0.5f,-0.5f,

        -0.5f,-0.5f,-0.5f, 0.5f,-0.5f,-0.5f, 0.5f,-0.5f, 0.5f,
        -0.5f,-0.5f,-0.5f, 0.5f,-0.5f, 0.5f, -0.5f,-0.5f, 0.5f
    };

    private int shader(int type, String source) {
        int id = GLES20.glCreateShader(type);
        GLES20.glShaderSource(id, source);
        GLES20.glCompileShader(id);
        return id;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glClearColor(0.035f, 0.055f, 0.075f, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        String vertexShader =
            "uniform mat4 uMVP;" +
            "attribute vec3 aPosition;" +
            "void main() {" +
            "gl_Position = uMVP * vec4(aPosition,1.0);" +
            "}";

        String fragmentShader =
            "precision mediump float;" +
            "uniform vec4 uColor;" +
            "void main() {" +
            "gl_FragColor = uColor;" +
            "}";

        int vs = shader(GLES20.GL_VERTEX_SHADER, vertexShader);
        int fs = shader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);
        GLES20.glLinkProgram(program);

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        matrixHandle = GLES20.glGetUniformLocation(program, "uMVP");

        ByteBuffer bb = ByteBuffer.allocateDirect(CUBE.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertices = bb.asFloatBuffer();
        vertices.put(CUBE);
        vertices.position(0);

        lastFrame = System.nanoTime();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        GLES20.glViewport(0, 0, width, height);

        Matrix.perspectiveM(
            projection, 0, 70,
            (float) width / Math.max(1, height),
            0.1f, 100f
        );
    }

    private void drawCube(float x, float y, float z,
                          float sx, float sy, float sz,
                          float r, float g, float b) {

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, x, y, z);
        Matrix.scaleM(model, 0, sx, sy, sz);

        Matrix.multiplyMM(mv, 0, view, 0, model, 0);
        Matrix.multiplyMM(mvp, 0, projection, 0, mv, 0);

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvp, 0);
        GLES20.glUniform4f(colorHandle, r, g, b, 1);

        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT,
            false, 0, vertices
        );

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        long now = System.nanoTime();
        float dt = Math.min((now - lastFrame) / 1_000_000_000f, 0.05f);
        lastFrame = now;

        float angle = (float)Math.toRadians(yaw);

        float forwardX = (float)Math.sin(angle);
        float forwardZ = -(float)Math.cos(angle);

        playerX += (forwardX * moveY +
                    (float)Math.cos(angle) * moveX) * dt * 3;

        playerZ += (forwardZ * moveY +
                    (float)Math.sin(angle) * moveX) * dt * 3;

        float lookX = (float)(Math.sin(angle) *
                      Math.cos(Math.toRadians(pitch)));

        float lookY = (float)Math.sin(Math.toRadians(pitch));

        float lookZ = (float)(-Math.cos(angle) *
                      Math.cos(Math.toRadians(pitch)));

        Matrix.setLookAtM(
            view, 0,
            playerX, 1.7f, playerZ,
            playerX + lookX,
            1.7f + lookY,
            playerZ + lookZ,
            0, 1, 0
        );

        GLES20.glClear(
            GLES20.GL_COLOR_BUFFER_BIT |
            GLES20.GL_DEPTH_BUFFER_BIT
        );

        GLES20.glUseProgram(program);

        drawCube(0, -0.15f, -5, 20, 0.2f, 20,
                 0.20f, 0.23f, 0.25f);

        for (int i = 0; i < 8; i++) {
            drawCube(
                (i % 4) * 3 - 4.5f,
                1,
                -3 - (i / 4) * 5,
                1.5f, 2, 1.5f,
                0.25f, 0.35f, 0.40f
            );
        }
    }
}
