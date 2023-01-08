package net.chigita.spider

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import java.util.Locale

/**
 * A processor of root gate that analyzes agsl shader files.
 */
internal class SpiderProcessor(
    private val codeGenerator: CodeGenerator,
    private val fileNameFetcher: SpiderTargetFileNameFetcher
) : SymbolProcessor {

    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        val agslShaderFiles = fileNameFetcher.fetch(resolver)
        agslShaderFiles.forEach {
            val lowerCaseFileName = it.lowercase(Locale.ROOT)
            val pascalCaseFileName = lowerCaseFileName
                .replaceFirstChar(Char::uppercaseChar)
            val generatedSpecificName = "Remember${pascalCaseFileName}Shader"
            val code = """
                package net.chigita.spider
                import android.graphics.RuntimeShader
                import androidx.compose.runtime.Composable
                import androidx.compose.runtime.remember
                import androidx.compose.ui.platform.LocalContext
                import java.io.IOException
                
                @Composable
                fun ${generatedSpecificName}(): RuntimeShader {
                    val context = LocalContext.current
                    val shaderRawString = try {
                        val inputStream = context.assets.open("${lowerCaseFileName}.agsl")
                        val size = inputStream.available()
                        val buffer = ByteArray(size)
                        inputStream.read(buffer)
                        String(buffer)
                    } catch (ignore: IOException) {
                        ""
                    }
                    return remember {
                        RuntimeShader(shaderRawString)
                    }
                }
            """.trimIndent()
            codeGenerator.createNewFile(
                dependencies = Dependencies(true, resolver.getNewFiles().first()),
                packageName = SPIDER_PACKAGE_NAME,
                fileName = generatedSpecificName,
            ).use { outputStream ->
                outputStream.write(code.toByteArray())
            }
        }

        invoked = true
        return emptyList()
    }

    companion object {
        private const val SPIDER_PACKAGE_NAME = "net.chigita.spider"
    }
}
