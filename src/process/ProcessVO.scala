package process

import compilers.Compiler
import extensions.FileExtension

case class ProcessVO(name: String,
                     fileExtension: Option[FileExtension],
                     compiler: Option[Compiler],
                     setting: Setting)
