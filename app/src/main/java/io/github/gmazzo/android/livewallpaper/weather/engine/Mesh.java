package io.github.gmazzo.android.livewallpaper.weather.engine;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.engine.Utility.Logger;

import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_MODULATE;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE1;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV_MODE;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLES;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT;
import static javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER;
import static javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER;

public class Mesh {
    private static final String TAG = "GL Engine";
    static final boolean assertionsDisabled;
    private static Tag tagOrigin = null;
    private ShortBuffer bufIndex;
    private ByteBuffer bufIndexDirect;
    public int bufIndexHandle = 0;
    public FloatBuffer bufScratch = null;
    private FloatBuffer bufTC;
    private ByteBuffer bufTCDirect;
    public int bufTCHandle = 0;
    public Frame[] frames;
    public String meshName;
    public int numElements;
    public int numIndices;
    protected int numTriangles;
    public float[] originalVertexArray;

    public static class Frame {
        public FloatBuffer bufNormal;
        public ByteBuffer bufNormalDirect;
        public int bufNormalHandle = 0;
        public FloatBuffer bufVertex;
        public ByteBuffer bufVertexDirect;
        public int bufVertexHandle = 0;

        public Frame() {
        }
    }

    class Tag {
        private float[] normal;
        private float[] position;

        public Tag(int i) {
            this.position = new float[(i * 3)];
            this.normal = new float[(i * 3)];
        }

        public void addNormal(float f, float f1, float f2, int i) {
            this.normal[i * 3] = f;
            this.normal[(i * 3) + 1] = f1;
            this.normal[(i * 3) + 2] = f2;
        }

        public void addPosition(float f, float f1, float f2, int i) {
            this.position[i * 3] = f;
            this.position[(i * 3) + 1] = f1;
            this.position[(i * 3) + 2] = f2;
        }

        public void getNormal(Vector vector, int i) {
            if (i * 3 >= this.position.length) {
                Logger.v(Mesh.TAG, "ERROR: Tried to get tag normal on invalid frame " + i);
                vector.setY(0.0f);
                vector.setX(0.0f);
                vector.setZ(1.0f);
                return;
            }
            vector.setX(this.normal[i * 3]);
            vector.setY(this.normal[(i * 3) + 1]);
            vector.setZ(this.normal[(i * 3) + 2]);
        }

    }

    static {
        if (desiredAssertionStatus()) {
            assertionsDisabled = false;
        } else {
            assertionsDisabled = true;
        }
    }

    private static boolean desiredAssertionStatus() {
        return true;
    }

    public Mesh() {
        if (tagOrigin == null) {
            tagOrigin = new Tag(1);
            tagOrigin.addPosition(0.0f, 0.0f, 0.0f, 0);
            tagOrigin.addNormal(0.0f, 0.0f, 1.0f, 0);
        }
    }

    private void allocateScratchBuffers() {
        this.bufScratch = ByteBuffer.allocateDirect(this.numElements * 3 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    public void createFromArrays(GL10 gl, float[] vertexs, float[] normals, float[] tcs, short[] indices, int num_elements, int num_frames, boolean willBeInterpolated) {
        boolean useVertexBufferObjects = gl instanceof GL11;
        if (useVertexBufferObjects) {
            Logger.v(TAG, " - using GL11 vertex buffer objects");
        }
        int iCapacity = indices.length;
        this.numTriangles = indices.length / 3;
        this.frames = new Frame[num_frames];
        this.numElements = num_elements;
        if (willBeInterpolated) {
            this.originalVertexArray = vertexs;
            Logger.v(TAG, " - preparing for interpolated animation");
        } else {
            this.originalVertexArray = null;
        }
        if (this.meshName == null) {
            this.meshName = "CreatedFromArrays";
        }
        int length = num_elements * 3;
        int vertexBufferBytes = length * 4;
        int normalBufferBytes = length * 4;
        int tcBufferBytes = (num_elements * 2) * 4;
        int indexBufferBytes = iCapacity * 2;
        for (int i = 0; i < num_frames; i++) {
            Frame frame = new Frame();
            this.frames[i] = frame;
            frame.bufVertexDirect = ByteBuffer.allocateDirect(vertexBufferBytes);
            frame.bufVertexDirect.order(ByteOrder.nativeOrder());
            frame.bufVertex = frame.bufVertexDirect.asFloatBuffer();
            frame.bufVertex.clear();
            frame.bufVertex.put(vertexs, i * length, length);
            frame.bufVertex.position(0);
            frame.bufNormalDirect = ByteBuffer.allocateDirect(normalBufferBytes);
            frame.bufNormalDirect.order(ByteOrder.nativeOrder());
            frame.bufNormal = frame.bufNormalDirect.asFloatBuffer();
            frame.bufNormal.clear();
            frame.bufNormal.put(normals, i * length, length);
            frame.bufNormal.position(0);
            if (useVertexBufferObjects) {
                GL11 gl11 = (GL11) gl;
                int[] handleTemp = new int[1];
                gl11.glGenBuffers(1, handleTemp, 0);
                frame.bufVertexHandle = handleTemp[0];
                gl11.glBindBuffer(GL_ARRAY_BUFFER, frame.bufVertexHandle);
                gl11.glBufferData(GL_ARRAY_BUFFER, vertexBufferBytes, frame.bufVertex, 35044);
                gl11.glBindBuffer(GL_ARRAY_BUFFER, 0);
                gl11.glGenBuffers(1, handleTemp, 0);
                frame.bufNormalHandle = handleTemp[0];
                gl11.glBindBuffer(GL_ARRAY_BUFFER, frame.bufNormalHandle);
                gl11.glBufferData(GL_ARRAY_BUFFER, normalBufferBytes, frame.bufNormal, 35044);
                gl11.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
        }
        this.bufTCDirect = ByteBuffer.allocateDirect(tcBufferBytes);
        this.bufTCDirect.order(ByteOrder.nativeOrder());
        this.bufTC = this.bufTCDirect.asFloatBuffer();
        this.bufTC.clear();
        this.bufTC.put(tcs);
        this.bufTC.position(0);
        this.bufIndexDirect = ByteBuffer.allocateDirect(indexBufferBytes);
        this.bufIndexDirect.order(ByteOrder.nativeOrder());
        this.bufIndex = this.bufIndexDirect.asShortBuffer();
        this.bufIndex.clear();
        this.bufIndex.put(indices);
        this.bufIndex.position(0);
        this.numIndices = this.bufIndex.capacity();
        if (useVertexBufferObjects) {
            GL11 gl11 = (GL11) gl;
            int[] handleTemp = new int[1];
            gl11.glGenBuffers(1, handleTemp, 0);
            this.bufIndexHandle = handleTemp[0];
            gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle);
            gl11.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferBytes, this.bufIndex, 35044);
            gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            gl11.glGenBuffers(1, handleTemp, 0);
            this.bufTCHandle = handleTemp[0];
            gl11.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle);
            gl11.glBufferData(GL_ARRAY_BUFFER, tcBufferBytes, this.bufTC, 35044);
            gl11.glBindBuffer(GL_ARRAY_BUFFER, 0);
        }

        // this.numElements = 0;
        this.numTriangles = 0;
        this.bufIndex = null;
        this.bufIndexDirect = null;
        this.bufTC = null;
        this.bufTCDirect = null;
    }

    public void createFromBinaryFile(GL10 gl, InputStream inputstream, String name, boolean willBeInterpolated) {
        HashMap tagList = new HashMap();
        Logger.v(TAG, " - reading as binary");
        this.meshName = name;
        DataInputStream dataInputStream = new DataInputStream(inputstream);
        byte[] segchars = new byte[4];
        try {
            dataInputStream.read(segchars, 0, 4);
            if (segchars[0] == (byte) 66 && segchars[1] == (byte) 77 && segchars[2] == (byte) 68 && segchars[3] == (byte) 76) {
                try {
                    int fileVersion = dataInputStream.readInt();
                    int fileElements = dataInputStream.readInt();
                    int fileFrames = dataInputStream.readInt();
                    Logger.v(TAG, "version: " + fileVersion + " elements: " + fileElements + " frames: " + fileFrames);
                    dataInputStream.skip(12);
                    dataInputStream.skip(4);
                    dataInputStream.read(segchars, 0, 4);
                    if (segchars[0] == (byte) 87 && segchars[1] == (byte) 73 && segchars[2] == (byte) 78 && segchars[3] == (byte) 68) {
                        int i;
                        int numWindings = dataInputStream.readInt();
                        short[] indices = new short[(numWindings * 3)];
                        dataInputStream.skip(8);
                        int curReadIndex = 0;
                        for (i = 0; i < numWindings; i++) {
                            indices[curReadIndex] = dataInputStream.readShort();
                            indices[curReadIndex + 1] = dataInputStream.readShort();
                            indices[curReadIndex + 2] = dataInputStream.readShort();
                            curReadIndex += 3;
                        }
                        try {
                            dataInputStream.skip(4);
                            dataInputStream.read(segchars, 0, 4);
                            if (segchars[0] == (byte) 84 && segchars[1] == (byte) 69 && segchars[2] == (byte) 88 && segchars[3] == (byte) 84) {
                                int numTC = dataInputStream.readInt();
                                float[] tcList = new float[(numTC * 2)];
                                dataInputStream.skip(8);
                                curReadIndex = 0;
                                for (i = 0; i < numTC; i++) {
                                    tcList[curReadIndex] = dataInputStream.readFloat();
                                    tcList[curReadIndex + 1] = dataInputStream.readFloat();
                                    curReadIndex += 2;
                                }
                                try {
                                    dataInputStream.skip(4);
                                    dataInputStream.read(segchars, 0, 4);
                                    if (segchars[0] == (byte) 86 && segchars[1] == (byte) 69 && segchars[2] == (byte) 82 && segchars[3] == (byte) 84) {
                                        int numVertices = dataInputStream.readInt();
                                        float[] vertexList = new float[((numVertices * 3) * fileFrames)];
                                        int vertScale = dataInputStream.readInt();
                                        if (vertScale == 0) {
                                            vertScale = 128;
                                        }
                                        Logger.i(TAG, "vertScale=" + vertScale);
                                        dataInputStream.skip(4);
                                        int n = numVertices * fileFrames;
                                        curReadIndex = 0;
                                        for (i = 0; i < n; i++) {
                                            if (fileVersion >= 4) {
                                                vertexList[curReadIndex] = ((float) dataInputStream.readShort()) / ((float) vertScale);
                                                vertexList[curReadIndex + 1] = ((float) dataInputStream.readShort()) / ((float) vertScale);
                                                vertexList[curReadIndex + 2] = ((float) dataInputStream.readShort()) / ((float) vertScale);
                                                curReadIndex += 3;
                                            } else {
                                                vertexList[curReadIndex] = dataInputStream.readFloat();
                                                vertexList[curReadIndex + 1] = dataInputStream.readFloat();
                                                vertexList[curReadIndex + 2] = dataInputStream.readFloat();
                                                curReadIndex += 3;
                                            }
                                        }
                                        try {
                                            dataInputStream.skip(4);
                                            dataInputStream.read(segchars, 0, 4);
                                            if (segchars[0] == (byte) 78 && segchars[1] == (byte) 79 && segchars[2] == (byte) 82 && segchars[3] == (byte) 77) {
                                                int numNormals = dataInputStream.readInt();
                                                float[] normalList = new float[((numNormals * 3) * fileFrames)];
                                                dataInputStream.skip(8);
                                                curReadIndex = 0;
                                                for (i = 0; i < numNormals * fileFrames; i++) {
                                                    if (fileVersion >= 3) {
                                                        normalList[curReadIndex] = ((float) dataInputStream.readByte()) / 127.0f;
                                                        normalList[curReadIndex + 1] = ((float) dataInputStream.readByte()) / 127.0f;
                                                        normalList[curReadIndex + 2] = ((float) dataInputStream.readByte()) / 127.0f;
                                                        curReadIndex += 3;
                                                    } else {
                                                        normalList[curReadIndex] = dataInputStream.readFloat();
                                                        normalList[curReadIndex + 1] = dataInputStream.readFloat();
                                                        normalList[curReadIndex + 2] = dataInputStream.readFloat();
                                                        curReadIndex += 3;
                                                    }
                                                }
                                                createFromArrays(gl, vertexList, normalList, tcList, indices, fileElements, fileFrames, willBeInterpolated);
                                                if (tagList.size() > 0) {
                                                    return;
                                                }
                                                return;
                                            }
                                            Logger.v(TAG, " - invalid chunk tag: NORM");
                                            return;
                                        } catch (IOException ex2) {
                                            ex2.printStackTrace();
                                            return;
                                        }
                                    }
                                    Logger.v(TAG, " - invalid chunk tag: BVRT");
                                    return;
                                } catch (IOException ex22) {
                                    ex22.printStackTrace();
                                    return;
                                }
                            }
                            Logger.v(TAG, " - invalid chunk tag: TEXT");
                            return;
                        } catch (IOException ex222) {
                            ex222.printStackTrace();
                            return;
                        }
                    }
                    Logger.v(TAG, " - invalid chunk tag: WIND");
                    return;
                } catch (IOException ex2222) {
                    Logger.v(TAG, " - ERROR reading model WIND!");
                    ex2222.printStackTrace();
                    return;
                }
            }
            Logger.v(TAG, " - invalid chunk tag: BMDL");
        } catch (IOException ex22222) {
            Logger.v(TAG, " - ERROR reading model BMDL!");
            ex22222.printStackTrace();
        }
    }

    public void createFromTextFile(GL10 gl, InputStream inputstream, String s, boolean flag) {
    }

    public void render(GL10 gl10) {
        renderFrame(gl10, 0);
    }

    public void renderFrame(GL10 gl10, int frameNum) {
        if (frameNum >= this.frames.length || frameNum < 0) {
            Logger.v(TAG, "ERROR: Mesh.renderFrame (" + this.meshName + ") given a frame outside of frames.length: " + frameNum);
            frameNum = this.frames.length - 1;
        }
        if (gl10 instanceof GL11) {
            renderFrame_gl11((GL11) gl10, frameNum);
            return;
        }
        gl10.glVertexPointer(3, GL_FLOAT, 0, this.frames[frameNum].bufVertex);
        gl10.glNormalPointer(GL_FLOAT, 0, this.frames[frameNum].bufNormal);
        gl10.glTexCoordPointer(2, GL_FLOAT, 0, this.bufTC);
        gl10.glDrawElements(GL_TRIANGLES, this.numIndices, GL_UNSIGNED_SHORT, this.bufIndex);
    }

    public void renderFrameInterpolated(GL10 gl, int frameNum, int frameBlendNum, float blendAmount) {
        if (this.originalVertexArray == null) {
            renderFrame(gl, frameNum);
        } else if (((double) blendAmount) < 0.01d) {
            renderFrame(gl, frameNum);
        } else if (((double) blendAmount) > 0.99d) {
            renderFrame(gl, frameBlendNum);
        } else {
            StringBuilder sb;
            if (frameNum >= this.frames.length || frameNum < 0) {
                sb = new StringBuilder("ERROR: Mesh.renderFrameInterpolated (");
                sb.append(this.meshName);
                sb.append(") given a frame outside of frames.length: ");
                sb.append(frameNum);
                Logger.v(TAG, sb.toString());
                frameNum = this.frames.length - 1;
            }
            if (frameBlendNum >= this.frames.length || frameBlendNum < 0) {
                sb = new StringBuilder("ERROR: Mesh.renderFrameInterpolated (");
                sb.append(this.meshName);
                sb.append(") given a blendframe outside of frames.length: ");
                sb.append(frameBlendNum);
                Logger.v(TAG, sb.toString());
                frameBlendNum = this.frames.length - 1;
            }
            if (this.bufScratch == null) {
                allocateScratchBuffers();
                Logger.v(TAG, this.meshName + " allocated animation buffers");
            }
            int firstFrameOffset = (this.numElements * frameNum) * 3;
            int blendFrameOffset = (this.numElements * frameBlendNum) * 3;
            float oneminusblend = 1.0f - blendAmount;
            for (int i = 0, c = bufScratch.capacity(); i < c; i++) {
                this.bufScratch.put(i,
                        (this.originalVertexArray[firstFrameOffset + i] * oneminusblend) +
                                (this.originalVertexArray[blendFrameOffset + i] * blendAmount));
            }
            this.bufScratch.position(0);

            gl.glVertexPointer(3, GL_FLOAT, 0, this.bufScratch);
            if (gl instanceof GL11) {
                GL11 gl11 = (GL11) gl;
                gl11.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufNormalHandle);
                gl11.glNormalPointer(GL_FLOAT, 0, 0);

                gl11.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle);
                gl11.glTexCoordPointer(2, GL_FLOAT, 0, 0);

                gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle);
                gl11.glDrawElements(GL_TRIANGLES, this.numIndices, GL_UNSIGNED_SHORT, 0);

                gl11.glBindBuffer(GL_ARRAY_BUFFER, 0);
                gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

            } else {
                gl.glNormalPointer(GL_FLOAT, 0, this.frames[frameNum].bufNormal);
                gl.glTexCoordPointer(2, GL_FLOAT, 0, this.bufTC);
                gl.glDrawElements(GL_TRIANGLES, this.numIndices, GL_UNSIGNED_SHORT, this.bufIndex);
            }
        }
    }

    public void renderFrameMultiTexture(GL11 gl11, int frameNum, int tex1, int tex2, int combine, boolean envMap) {
        gl11.glActiveTexture(GL_TEXTURE0);
        gl11.glBindTexture(GL_TEXTURE_2D, tex1);
        if (envMap) {
            gl11.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufNormalHandle);
            gl11.glTexCoordPointer(3, GL_FLOAT, 0, 0);
        } else {
            gl11.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle);
            gl11.glTexCoordPointer(2, GL_FLOAT, 0, 0);
        }
        gl11.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        gl11.glActiveTexture(GL_TEXTURE1);
        gl11.glEnable(GL_TEXTURE_2D);
        gl11.glClientActiveTexture(GL_TEXTURE1);
        gl11.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl11.glBindTexture(GL_TEXTURE_2D, tex2);
        gl11.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle);
        gl11.glTexCoordPointer(2, GL_FLOAT, 0, 0);
        gl11.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, combine);
        gl11.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufVertexHandle);
        gl11.glVertexPointer(3, GL_FLOAT, 0, 0);
        gl11.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufNormalHandle);
        gl11.glNormalPointer(GL_FLOAT, 0, 0);
        gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle);
        gl11.glDrawElements(GL_TRIANGLES, this.numIndices, GL_UNSIGNED_SHORT, 0);
        gl11.glBindBuffer(GL_ARRAY_BUFFER, 0);
        gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        gl11.glDisable(GL_TEXTURE_2D);
        gl11.glActiveTexture(GL_TEXTURE0);
        gl11.glClientActiveTexture(GL_TEXTURE0);
    }

    public void renderFrame_gl11(GL11 gl11, int frameNum) {
        renderFrame_gl11_setup(gl11, frameNum);
        renderFrame_gl11_render(gl11);
        renderFrame_gl11_clear(gl11);
    }

    public void renderFrame_gl11_clear(GL11 gl11) {
        gl11.glBindBuffer(GL_ARRAY_BUFFER, 0);
        gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void renderFrame_gl11_render(GL11 gl11) {
        gl11.glDrawElements(GL_TRIANGLES, this.numIndices, GL_UNSIGNED_SHORT, 0);
    }

    public void renderFrame_gl11_setup(GL11 gl11, int frameNum) {
        if (frameNum >= this.frames.length || frameNum < 0) {
            StringBuilder sb = new StringBuilder("ERROR: Mesh.renderFrame (");
            sb.append(this.meshName).append(") given a frame outside of frames.length: ").append(frameNum);
            Logger.v(TAG, sb.toString());
            frameNum = this.frames.length - 1;
        }
        gl11.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufVertexHandle);
        gl11.glVertexPointer(3, GL_FLOAT, 0, 0);
        gl11.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufNormalHandle);
        gl11.glNormalPointer(GL_FLOAT, 0, 0);
        gl11.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle);
        gl11.glTexCoordPointer(2, GL_FLOAT, 0, 0);
        gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle);
    }

    public void unload(GL10 gl10) {
        /* TODO managed by Models
        boolean isGL11 = gl10 instanceof GL11;
        int[] tmpBuffer = new int[2];
        for (int i = 0; i < this.frames.length; i++) {
            this.frames[i].bufNormal = null;
            this.frames[i].bufNormalDirect = null;
            this.frames[i].bufVertex = null;
            this.frames[i].bufVertexDirect = null;
            if (isGL11) {
                GL11 gl11 = (GL11) gl10;
                tmpBuffer[0] = this.frames[i].bufNormalHandle;
                tmpBuffer[1] = this.frames[i].bufVertexHandle;
                gl11.glDeleteBuffers(2, tmpBuffer, 0);
            }
        }
        this.bufIndex = null;
        this.bufIndexDirect = null;
        this.bufTC = null;
        this.bufTCDirect = null;
        if (isGL11) {
            GL11 gl11 = (GL11) gl10;
            tmpBuffer[0] = this.bufIndexHandle;
            tmpBuffer[1] = this.bufTCHandle;
            gl11.glDeleteBuffers(2, tmpBuffer, 0);
        }
        this.bufScratch = null;
        */
    }
}