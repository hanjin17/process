package process


import java.io.File
import scala.io.{Source, BufferedSource}

case class Ninja() extends BuildSystem {
  override def process(inputFile : File): List[ProcessVO] = {
    val process = using(Source.fromFile(inputFile, "UTF-8"): BufferedSource) { source =>
        source.getLines().filter(_.contains("-c")).foldLeft(List[ProcessVO]()){
          case (acc, line) =>
            val list = line.split("\\s+").toList
            val fileIndex = list.indexOf("-c")
            val compiler = getCompiler(line)
            if (fileIndex != -1 && fileIndex + 1 < line.length && compiler.isDefined) {
              val filename = list(fileIndex + 1).trim
              val fileExtension = getFileExtension(filename)

              val setting = list.foldLeft(Setting(Set.empty, Set.empty, Set.empty, Set.empty)) {
                case (setting, line) =>
                  val regexList = List(DEFINE_REGEX, INCLUDE_REGEX, INCLUDE_SYSTEM_REGEX, STD_REGEX)
                  val matchedSets = regexList.flatMap { regex =>
                    regex.findFirstMatchIn(line).map(_.matched).toSet
                  }
                  val defineMatches = DEFINE_REGEX.findAllMatchIn(line).map(_.group(1)).toSet
                  val includeMatches = INCLUDE_REGEX.findAllMatchIn(line).map(_.group(1)).toSet
                  val includeSystemMatches = INCLUDE_SYSTEM_REGEX.findAllMatchIn(line).map(_.group(1)).toSet
                  val compileMatches = STD_REGEX.findAllMatchIn(line).map(_.matched).toSet
                  setting.copy(
                    include = setting.include ++ includeMatches ++ includeSystemMatches,
                    define = setting.define ++ defineMatches,
                    compileOption = setting.compileOption ++ compileMatches,
                    misMatch = if (matchedSets.isEmpty) setting.misMatch + line else setting.misMatch
                  )
              }
              ProcessVO(filename, fileExtension, compiler, setting) :: acc
            } else
              acc
        }
    }
    process
  }
}
