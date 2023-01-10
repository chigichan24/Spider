package net.chigita.spider

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile

/**
 * A processor of root gate that analyzes agsl shader files.
 */
internal class SpiderProcessor(
    private val codeGenerator: CodeGenerator,
    private val fileNameFetcher: SpiderTargetFileNameFetcher,
    private val generatedCodeProvider: SpiderGeneratedCodeProvider
) : SymbolProcessor {

    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        val agslShaderFiles = fileNameFetcher.fetch(resolver)
        generateShaderWrappers(agslShaderFiles)

        invoked = true
        return emptyList()
    }

    private fun generateShaderWrappers(agslShaderFiles: Sequence<String>) {
        agslShaderFiles.forEach {
            val result = generatedCodeProvider.provideCustomShaderCode(it)
            codeGenerator.createNewFile(
                dependencies = Dependencies(true),
                packageName = SPIDER_PACKAGE_NAME,
                fileName = result.fileName,
            ).use { outputStream ->
                outputStream.write(result.generatedCode.toByteArray())
            }
        }
    }

    companion object {
        private const val SPIDER_PACKAGE_NAME = "net.chigita.spider"
    }
}
