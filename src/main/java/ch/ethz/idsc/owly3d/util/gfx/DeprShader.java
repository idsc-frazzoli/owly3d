//// code by jph
// package ch.ethz.idsc.owly3d.util.gfx;
//
// import java.io.File;
// import java.nio.file.Files;
//
// import org.lwjgl.opengl.GL20;
//
// class DeprShader {
// public static DeprShader of(File vfile, File ffile) {
// // System.out.println("SHADER");
// // System.out.println(vfile.exists());
// // System.out.println(ffile.exists());
// try {
// String vString = new String(Files.readAllBytes(vfile.toPath()));
// String fString = new String(Files.readAllBytes(ffile.toPath()));
// // System.out.println(vString);
// // System.out.println(fString);
// return new DeprShader(vString, fString);
// } catch (Exception exception) {
// exception.printStackTrace();
// }
// return null;
// }
//
// // int vsid;
// // int fsid;
// public final int program;
//
// private DeprShader(String vString, String fString) {
// int vsid = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
// int fsid = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
// // System.out.println("shaders " + vsid + " " + fsid);
// compile(vsid, vString);
// compile(fsid, fString);
// // ---
// program = GL20.glCreateProgram();
// GL20.glAttachShader(program, vsid);
// GL20.glAttachShader(program, fsid);
// GL20.glLinkProgram(program);
// // ---
// // int status =
// GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
// // System.out.println("link=" + status);
// String log = GL20.glGetProgramInfoLog(program);
// if (!log.isEmpty()) {
// System.out.println(log);
// throw new RuntimeException(log);
// }
// GL20.glDetachShader(program, vsid);
// GL20.glDetachShader(program, fsid);
// GL20.glDeleteShader(vsid);
// GL20.glDeleteShader(fsid);
// }
//
// private static void compile(int id, String string) {
// GL20.glShaderSource(id, string);
// GL20.glCompileShader(id);
// // int status =
// GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS);
// // System.out.println("status=" + status);
// String log = GL20.glGetShaderInfoLog(id);
// if (!log.isEmpty()) {
// System.out.println(log);
// throw new RuntimeException(log);
// }
// }
//
// public void load() {
// GL20.glUseProgram(program); // TODO use function
// }
// }
