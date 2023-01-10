package net.chigita.spider

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals

/**
 * Tests for [SpiderProcessor].
 */
class SpiderProcessorTest {

    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun testAGSLAnnotation() {
        val kspCompileResult = compile(
            SourceFile.kotlin(
                "Compose.kt",
                """
                    package test
                    import net.chigita.spider.annotation.AGSL
                    
                    @AGSL("test")
                    fun TestComposeFunction() {
                        // code snip...
                    }
                """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, kspCompileResult.result.exitCode)
        assertEquals(1, kspCompileResult.generatedFiles.size)
        val file = kspCompileResult.generatedFiles[0]
        file.inputStream().use {
            val generatedFileText = String(it.readBytes()).trimIndent()
            assertEquals(EXPECTED_GENERATED_CODE, generatedFileText)
        }
    }

    private fun prepareCompilation(vararg sourceFiles: SourceFile): KotlinCompilation =
        KotlinCompilation()
            .apply {
                workingDir = temporaryFolder.root
                inheritClassPath = true
                symbolProcessorProviders = listOf(SpiderProcessorProvider())
                sources = sourceFiles.asList()
                verbose = false
                kspIncremental = true
            }

    private fun compile(vararg sourceFiles: SourceFile): KspCompileResult {
        val compilation = prepareCompilation(*sourceFiles)
        val result = compilation.compile()
        return KspCompileResult(
            result,
            findGeneratedFiles(compilation)
        )
    }

    private fun findGeneratedFiles(compilation: KotlinCompilation): List<File> {
        return compilation.kspSourcesDir
            .walkTopDown()
            .filter { it.isFile }
            .toList()
    }

    /**
     * A data class that contains ksp processed result.
     */
    private data class KspCompileResult(
        val result: KotlinCompilation.Result,
        val generatedFiles: List<File>
    )

    companion object {
        private val EXPECTED_GENERATED_CODE: String = """
            // Generated by https://github.com/chigichan24/Spider
            package net.chigita.spider
            import android.graphics.RuntimeShader
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.remember
            import androidx.compose.ui.platform.LocalContext
            import java.io.IOException
            
            @Composable
            fun RememberTestShader(): RuntimeShader {
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
    }
}
