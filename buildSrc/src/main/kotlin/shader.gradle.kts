import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import java.io.FilterReader
import java.io.Reader
import java.io.StringReader
import java.nio.charset.StandardCharsets

extensions.create("shader", Shader::class)

open class Shader : Action<FileCopyDetails> {
  companion object {
    var relocations: Map<String, String> = mapOf()
  }

  @Suppress("unused")
  fun relocate(entry: Pair<String, String>) {
    relocations += entry
  }

  override fun execute(t: FileCopyDetails) {
    var resourcePath: String = t.path
    if (resourcePath.endsWith(".class")) {
      t.filter(ShadeClass::class)
    }
    relocations.forEach { (oldPath, newPath) ->
      resourcePath = resourcePath.replace(oldPath, newPath)
    }
    t.path = resourcePath
  }
}

class ShadeClass(reader: Reader) : FilterReader(shade(reader)) {
  companion object {
    fun shade(reader: Reader): Reader {
      val remapper = object : Remapper(Opcodes.ASM9) {
        override fun map(internalName: String?): String? {
          var result: String? = internalName
          Shader.relocations.forEach { (oldPath, newPath) ->
            result = result?.replace(oldPath, newPath)
          }
          return result
        }

        override fun mapValue(value: Any?): Any? {
          return if (value is String) {
            map(value)
          } else {
            super.mapValue(value)
          }
        }
      }
      val cr = ClassReader(reader.readText().toByteArray(StandardCharsets.ISO_8859_1))
      val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
      cr.accept(ClassRemapper(ToJava8(cw), remapper), 0)
      return StringReader(String(cw.toByteArray(), StandardCharsets.ISO_8859_1))
    }
  }
}

class ToJava8(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {
  override fun visit(
    version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String?>?
  ) {
    super.visit(version.coerceAtLeast(Opcodes.V1_8), access, name, signature, superName, interfaces)
  }
}
