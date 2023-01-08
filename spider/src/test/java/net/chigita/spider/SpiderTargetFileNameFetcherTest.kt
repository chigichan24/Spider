package net.chigita.spider

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.mockito.quality.Strictness
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Tests for [SpiderTargetFileNameFetcher].
 */
class SpiderTargetFileNameFetcherTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var resolver: Resolver

    private lateinit var target: SpiderTargetFileNameFetcher

    @Before
    fun setUp() {
        target = SpiderTargetFileNameFetcher()
    }

    @Test
    fun testFetchFiles() {
        val ksName = mock(KSName::class.java).stub {
            on { getShortName() } doReturn "TEST"
        }
        val ksDeclaration = mock(KSDeclaration::class.java).stub {
            on { simpleName } doReturn ksName
        }
        val ksClassDeclaration = mock(KSClassDeclaration::class.java).stub {
            on { classKind } doReturn ClassKind.ENUM_CLASS
            on { declarations } doReturn sequenceOf(ksDeclaration)
        }
        val annotation = "annotation"
        resolver.stub {
            on { getSymbolsWithAnnotation(annotation) } doReturn sequenceOf(
                ksClassDeclaration
            )
        }
        val result = target.fetch(resolver, annotation)
        assertContentEquals(
            sequenceOf("TEST"),
            result
        )
    }

    @Test
    fun testFetchFiles_multipleEnumFieldsCase() {
        val ksNameFoo = mock(KSName::class.java).stub {
            on { getShortName() } doReturn "FOO"
        }
        val ksNameBar = mock(KSName::class.java).stub {
            on { getShortName() } doReturn "BAR"
        }
        val ksDeclarationFoo = mock(KSDeclaration::class.java).stub {
            on { simpleName } doReturn ksNameFoo
        }
        val ksDeclarationBar = mock(KSDeclaration::class.java).stub {
            on { simpleName } doReturn ksNameBar
        }
        val ksClassDeclaration = mock(KSClassDeclaration::class.java).stub {
            on { classKind } doReturn ClassKind.ENUM_CLASS
            on { declarations } doReturn sequenceOf(ksDeclarationFoo, ksDeclarationBar)
        }
        val annotation = "annotation"
        resolver.stub {
            on { getSymbolsWithAnnotation(annotation) } doReturn sequenceOf(
                ksClassDeclaration
            )
        }
        val result = target.fetch(resolver, annotation)
        assertContentEquals(
            sequenceOf("FOO", "BAR"),
            result
        )
    }

    @Test
    fun testFetchFiles_nonEnumClassCase() {
        val ksClassDeclaration = mock(KSClassDeclaration::class.java).stub {
            on { classKind } doReturn ClassKind.OBJECT
        }
        val annotation = "annotation"
        resolver.stub {
            on { getSymbolsWithAnnotation(annotation) } doReturn sequenceOf(
                ksClassDeclaration
            )
        }
        val result = target.fetch(resolver, annotation)
        assertContentEquals(
            emptySequence(),
            result
        )
    }

    @Test
    fun testFetchFiles_annotationNotDefinedCase() {
        val result = target.fetch(resolver, null)
        assertEquals(
            emptySequence(),
            result
        )
    }
}
