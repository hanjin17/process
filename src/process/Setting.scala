package process

case class Setting(include: Set[String],
                   define: Set[String],
                   compileOption: Set[String],
                   misMatch: Set[String])
