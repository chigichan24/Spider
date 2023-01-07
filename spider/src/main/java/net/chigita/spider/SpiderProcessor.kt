package net.chigita.spider

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import org.intellij.lang.annotations.Language

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
        @Language("Kotlin")
        val code = """
            package net.chigita.spider
            import android.graphics.RuntimeShader
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.remember
            import androidx.compose.ui.platform.LocalContext
            import java.io.IOException
            
            @Composable
            fun RememberShader(): RuntimeShader {
                val context = LocalContext.current
                val shaderRawString = try {
                    val inputStream = context.assets.open("test.agsl")
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
        private const val SPIDER_TEST_FILE_NAME = "Shaders"
    }
}
