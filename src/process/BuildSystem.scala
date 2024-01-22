package process

import compilers.{Clang, GCC, DefaultCompiler, Compiler}
import extensions.{Cpp, C, FileExtension}

import java.io.File

trait BuildSystem {

  val DEFINE_REGEX = "-D\\s*([^ ]+)".r
  val INCLUDE_REGEX = """-I\s*([^ ]+)""".r
  val INCLUDE_SYSTEM_REGEX = """-isystem\s*([^ ]+)""".r
  val STD_REGEX = """-std\s*([^ ]+)""".r

  def process(inputFile : File) : List[ProcessVO]

  def using[A, B <: { def close(): Unit }](resource: B)(f: B => A): A =
    try {
      f(resource)
    } finally {
      resource.close()
    }

  def getFileExtension(fileName: String): Option[FileExtension] = {
    val extension = fileName.split("\\.").lastOption.getOrElse("")
    extension.toLowerCase match {
      case "cpp" => Some(Cpp())
      case "cc"  => Some(Cpp())
      case "c"   => Some(C())
      case _=> None
    }
  }

  def getCompiler(line: String): Option[Compiler] = line match {
    case _ if line.contains("clang++") => Some(Clang())
    case _ if line.contains("gcc") => Some(GCC())
    case _ if line.contains("/usr/bin/c++") => Some(DefaultCompiler())
    case _ => None
  }

}
