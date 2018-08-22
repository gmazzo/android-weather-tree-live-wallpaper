package gs.weather.wallpaper

import android.content.res.Resources
import android.support.annotation.RawRes
import java.nio.ByteBuffer
import java.nio.channels.Channels
import javax.microedition.khronos.opengles.GL10

class Models(private val resources: Resources,
             private val gl: GL10) {

    fun load(@RawRes rawId: Int, interpolated: Boolean = false) {
        val channel = Channels.newChannel(resources.openRawResource(rawId))

        val header = ByteBuffer.allocate(4)
        channel.read(header)
    }

}