package net.chigita.spider.fetcher

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.mockito.quality.Strictness
import kotlin.test.assertContentEquals

/**
 * Tests for [SpiderEnumValueFetcher].
 */
class SpiderEnumValueFetcherTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var resolver: Resolver
    
    private lateinit var target: SpiderEnumValueFetcher
    
    @Before
    fun setUp() {
        target = SpiderEnumValueFetcher(ANNOTATION)
    }

    @Test
    fun testFetch() {
        val ksName = Mockito.mock(KSName::class.java).stub {
            on { getShortName() } doReturn "TEST"
        }
        val ksDeclaration = Mockito.mock(KSDeclaration::class.java).stub {
            on { simpleName } doReturn ksName
        }
        val ksClassDeclaration = Mockito.mock(KSClassDeclaration::class.java).stub {
            on { classKind } doReturn ClassKind.ENUM_CLASS
            on { declarations } doReturn sequenceOf(ksDeclaration)
        }

        resolver.stub {
            on { getSymbolsWithAnnotation(ANNOTATION) } doReturn sequenceOf(
                ksClassDeclaration
            )
        }
        val result = target.fetch(resolver)
        assertContentEquals(
            sequenceOf("TEST"),
            result
        )
    }

    @Test
    fun testFetch_multipleEnumFieldsCase() {
        val ksNameFoo = Mockito.mock(KSName::class.java).stub {
            on { getShortName() } doReturn "FOO"
        }
        val ksNameBar = Mockito.mock(KSName::class.java).stub {
            on { getShortName() } doReturn "BAR"
        }
        val ksDeclarationFoo = Mockito.mock(KSDeclaration::class.java).stub {
            on { simpleName } doReturn ksNameFoo
        }
        val ksDeclarationBar = Mockito.mock(KSDeclaration::class.java).stub {
            on { simpleName } doReturn ksNameBar
        }
        val ksClassDeclaration = Mockito.mock(KSClassDeclaration::class.java).stub {
            on { classKind } doReturn ClassKind.ENUM_CLASS
            on { declarations } doReturn sequenceOf(ksDeclarationFoo, ksDeclarationBar)
        }

        resolver.stub {
            on { getSymbolsWithAnnotation(ANNOTATION) } doReturn sequenceOf(
                ksClassDeclaration
            )
        }
        val result = target.fetch(resolver)
        assertContentEquals(
            sequenceOf("FOO", "BAR"),
            result
        )
    }

    @Test
    fun testFetch_nonEnumClassCase() {
        val ksClassDeclaration = Mockito.mock(KSClassDeclaration::class.java).stub {
            on { classKind } doReturn ClassKind.OBJECT
        }

        resolver.stub {
            on { getSymbolsWithAnnotation(ANNOTATION) } doReturn sequenceOf(
                ksClassDeclaration
            )
        }
        val result = target.fetch(resolver)
        assertContentEquals(
            emptySequence(),
            result
        )
    }
    
    companion object {
        private const val ANNOTATION = "annotation"
    }
}
