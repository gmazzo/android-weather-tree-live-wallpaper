package gs.weather.wallpaper

import javax.microedition.khronos.opengles.GL10.GL_FLOAT
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER
import javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER

internal class ModelG11(
        gl: GL11,
        frames: Array<Model.Frame>,
        numIndices: Int,
        private val bufTCHandle: Int,
        private val bufIndexHandle: Int) : Model<GL11>(gl, frames, numIndices) {

    override fun renderFrame(frame: Int) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frames[frame].bufVertexHandle)
        gl.glVertexPointer(3, GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ARRAY_BUFFER, frames[frame].bufNormalHandle)

        gl.glNormalPointer(GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)

        gl.glDrawElements(4, this.numIndices, GL_UNSIGNED_SHORT, 0)

        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

}
