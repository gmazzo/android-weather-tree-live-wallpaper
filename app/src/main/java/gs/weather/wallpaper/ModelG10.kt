package gs.weather.wallpaper

import java.nio.Buffer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_FLOAT
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT

internal class ModelG10(
        gl: GL10,
        frames: Array<Model.Frame>,
        numIndices: Int,
        private val bufTC: Buffer,
        private val bufIndex: Buffer) : Model<GL10>(gl, frames, numIndices) {

    override fun renderFrame(frame: Int) {
        gl.glVertexPointer(3, GL_FLOAT, 0, frames[frame].bufVertex)
        gl.glNormalPointer(GL_FLOAT, 0, frames[frame].bufNormal)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, this.bufTC)
        gl.glDrawElements(4, numIndices, GL_UNSIGNED_SHORT, this.bufIndex)
    }

}
