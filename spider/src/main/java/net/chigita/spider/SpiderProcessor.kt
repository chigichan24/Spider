package net.chigita.spider

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

/**
 * A processor of root gate that analyzes ksp block options.
 */
class SpiderProcessor(
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        val files = resolver.getNewFiles()
        val code = """
            package net.chigita.spider
            class Sample {
                fun hello() {
                    println("Hello World")
                }
            }
        """.trimIndent()

        codeGenerator.createNewFile(
            dependencies = Dependencies(true, files.first()),
            packageName = SPIDER_PACKAGE_NAME,
            fileName = SPIDER_TEST_FILE_NAME,
        ).use { outputStream ->
            outputStream.write(code.toByteArray())
        }

        invoked = true
        return emptyList()
    }

    companion object {
        private const val SPIDER_PACKAGE_NAME = "net.chigita.spider"
        private const val SPIDER_TEST_FILE_NAME = "Test"
    }
}
