package compy

import play.twirl.compiler.{ TwirlCompiler => TwirlCompilerObj }
import play.router.{ RoutesCompiler => RoutesCompilerObj }
import java.io.File

class TwirlCompiler(source: File, sourceDirectory: File, generatedDirectory: File, formatterType: String, additionalImports: String = "", codec: String, inclusiveDot: Boolean = false, useOldParser: Boolean = false) {
  def execute() = TwirlCompilerObj.compile(source, sourceDirectory, generatedDirectory, formatterType, additionalImports, io.Codec(codec), inclusiveDot, useOldParser)
}

//(file: File, generatedDir: File, additionalImports: Seq[String], generateReverseRouter: Boolean = true, generateRefReverseRouter: Boolean = true, namespaceReverseRouter: Boolean = false) {

class RoutesCompiler(file: File, generatedDir: File, additionalImports: java.util.List[String], generateReverseRouter: Boolean = true, generateRefReverseRouter: Boolean = true, namespaceReverseRouter: Boolean = false) {
  import collection.JavaConverters._
  def execute() = RoutesCompilerObj.compile(file, generatedDir, additionalImports.asScala, generateReverseRouter, generateRefReverseRouter, namespaceReverseRouter)
}
