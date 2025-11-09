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

// support configuration of relocations via extension
extensions.create("shader", Shader::class)

// automatically apply shading to the main jar task
tasks.named<Jar>("jar").configure {
  // prune away unused directories after relocation
  exclude { it.isDirectory }
  // apply relocations as we add resources to the jar
  eachFile(Shader())
  // this charset supports safe filtering of binary content, as chars map 1:1 to bytes
  filteringCharset = "ISO-8859-1"
}

/** Applies configured relocations as resources are copied into the jar. */
open class Shader : Action<FileCopyDetails> {
  companion object {
    var relocations: Map<String, String> = mapOf()
  }

  // accepts relocations from build.gradle.kts
  fun relocate(entry: Pair<String, String>) {
    relocations += entry
  }

  override fun execute(t: FileCopyDetails) {
    var resourcePath: String = t.path
    // relocate any class-file references
    if (resourcePath.endsWith(".class")) {
      t.filter(ShadeClass::class)
    }
    // relocate file-name for consistency
    relocations.forEach { (oldPath, newPath) ->
      resourcePath = resourcePath.replace(oldPath, newPath)
    }
    t.path = resourcePath
  }
}

/** Filters class-files using remapper from asm-commons. */
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
      // rebuild constant-pool from scratch and make sure every method has a stack-map
      val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
      // apply relocation to class-file and upgrade pre-Java 8 class-files to Java 8
      val cr = ClassReader(reader.readText().toByteArray(StandardCharsets.ISO_8859_1))
      cr.accept(ClassRemapper(ToJava8(cw), remapper), 0)
      return StringReader(String(cw.toByteArray(), StandardCharsets.ISO_8859_1))
    }
  }
}

/** Upgrades pre-Java 8 class-files to Java 8, to allow faster verification at runtime. */
class ToJava8(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {
  override fun visit(
    version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String?>?
  ) {
    super.visit(version.coerceAtLeast(Opcodes.V1_8), access, name, signature, superName, interfaces)
  }
}
