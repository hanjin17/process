import process.{Ninja, Cmake, Make, ProcessVO, BuildSystem}

import java.io.File
import java.nio.file.{Paths, Files}
import scala.io.{Source, BufferedSource}

object Process {

  def main(args: Array[String]): Unit = {

    if (args.length < 2) {
      println("Usage: ProcessCommandFile <inputFile> <buildSystem>")
      sys.exit(1)
    }

    val inputFile = new File(args(0))
    val buildSystem = args(1) match {
      case "cmake" => Cmake()
      case "ninja" => Ninja()
      case "make" => Make()
      case _=> throw new IllegalArgumentException(s"Not Exist Build System : ${args(1)}")
    }

    val parent = inputFile.getParent
    val outputSelectFile = Paths.get(parent,"select.list")
    val outputIncludeFile = Paths.get(parent,"include.list")
    val outputDefineFile = Paths.get(parent,"define.list")
    val outputCompileFile = Paths.get(parent,"compileOption.list")


    val processVO = getProcessVO(buildSystem, inputFile)
    val selectList = processVO.map(_.name)
    val includeSet = processVO.flatMap(_.setting.include).toSet
    val defineSet = processVO.flatMap(_.setting.define).toSet
    val compileOptionSet = processVO.flatMap(_.setting.compileOption).toSet

    Files.write(outputSelectFile, selectList.mkString("\n").getBytes)
    Files.write(outputIncludeFile, includeSet.mkString("\n").getBytes)
    Files.write(outputDefineFile, defineSet.mkString("\n").getBytes)
    Files.write(outputCompileFile, compileOptionSet.mkString("\n").getBytes)
  }

  def getProcessVO(buildSystem: BuildSystem, inputFile: File): List[ProcessVO] =
    if (buildSystem.isInstanceOf[BuildSystem]) buildSystem.process(inputFile)
    else List.empty

}
