package net.chigita.spider

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [SpiderGeneratedCodeProvider].
 */
class SpiderGeneratedCodeProviderTest {
    private lateinit var target: SpiderGeneratedCodeProvider

    @Before
    fun setUp() {
        target = SpiderGeneratedCodeProvider()
    }

    @Test
    fun testProvideGeneratedCode_AllUpperCase() {
        val specifierString = "TEST"
        val result = target.provideCustomShaderCode(specifierString)
        assertEquals("RememberTestShader", result.fileName)
        assertTrue { result.generatedCode.contains("RememberTestShader") }
        assertTrue { result.generatedCode.contains("test.agsl") }
    }

    @Test
    fun testProvideGeneratedCode_AllLowerCase() {
        val specifierString = "test"
        val result = target.provideCustomShaderCode(specifierString)
        assertEquals("RememberTestShader", result.fileName)
        assertTrue { result.generatedCode.contains("RememberTestShader") }
        assertTrue { result.generatedCode.contains("test.agsl") }
    }
}
