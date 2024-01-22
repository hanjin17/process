package process

import java.io.File

case class Cmake() extends BuildSystem {
  override def process(inputFile : File): List[ProcessVO] = ???
}
