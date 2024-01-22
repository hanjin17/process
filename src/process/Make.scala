package process

import java.io.File

case class Make() extends BuildSystem {
  override def process(inputFile : File): List[ProcessVO] = ???
}
