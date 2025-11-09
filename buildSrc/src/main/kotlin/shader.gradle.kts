import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import java.io.FilterReader
import java.io.Reader
import java.io.StringReader
import java.nio.charset.StandardCharsets

class Shade(reader: Reader) : FilterReader(shade(reader)) {
  companion object {
    fun shade(reader: Reader): Reader {
      val remapper = object : Remapper(Opcodes.ASM9) {
        override fun map(internalName: String?): String? {
          return internalName?.replace("org/objectweb/", "datadog/instrument/")
        }
        override fun mapValue(value: Any?): Any? {
          if (value is String) {
            return value.replace("org/objectweb/", "datadog/instrument/")
          } else {
            return super.mapValue(value)
          }
        }
      }
      val cr = ClassReader(reader.readText().toByteArray(StandardCharsets.ISO_8859_1))
      val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
      cr.accept(ClassRemapper(cw, remapper), 0)
      return StringReader(String(cw.toByteArray(), StandardCharsets.ISO_8859_1))
    }
  }
}

open class Shader : Action<FileCopyDetails> {
  override fun execute(t: FileCopyDetails) {
    if (t.path.endsWith(".class")) {
      t.filter(Shade::class)
    }
    t.path = t.path.replace("org/objectweb/", "datadog/instrument/")
  }
}

project.extensions.create("shader", Shader::class)
