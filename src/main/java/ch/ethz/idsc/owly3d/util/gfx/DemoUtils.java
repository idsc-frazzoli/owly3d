package ch.ethz.idsc.owly3d.util.gfx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import org.lwjgl.BufferUtils;

public enum DemoUtils {
  ;
  /** Reads the specified resource and returns the raw data as a ByteBuffer.
   *
   * @param resource the resource to read
   * @param bufferSize the initial buffer size
   *
   * @return the resource data
   *
   * @throws IOException if an IO error occurs */
  public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
    ByteBuffer buffer;
    URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
    File file = new File(url.getFile());
    if (file.isFile()) {
      try (FileInputStream fis = new FileInputStream(file)) {
        try (FileChannel fc = fis.getChannel()) {
          buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        }
      }
    } else {
      buffer = BufferUtils.createByteBuffer(bufferSize);
      try (InputStream inputStream = url.openStream()) {
        if (inputStream == null)
          throw new FileNotFoundException(resource);
        try (ReadableByteChannel rbc = Channels.newChannel(inputStream)) {
          while (true) {
            int bytes = rbc.read(buffer);
            if (bytes == -1)
              break;
            if (buffer.remaining() == 0)
              buffer = resizeBuffer(buffer, buffer.capacity() * 2);
          }
          buffer.flip();
        } finally {
          inputStream.close();
        }
      }
    }
    return buffer;
  }

  private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
  }
}
