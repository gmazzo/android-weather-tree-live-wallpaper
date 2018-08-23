package gs.weather.wallpaper

import javax.microedition.khronos.opengles.GL11

class Texture(
        private val gl: GL11,
        val id: Int,
        val name: String) {

    internal fun unload() {
        gl.glDeleteTextures(1, intArrayOf(id), 0)
    }

}
