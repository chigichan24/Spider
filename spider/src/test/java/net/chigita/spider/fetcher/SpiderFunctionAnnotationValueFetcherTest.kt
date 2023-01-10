package net.chigita.spider.fetcher

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
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
 * Tests for [SpiderFunctionAnnotationValueFetcher].
 */
class SpiderFunctionAnnotationValueFetcherTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var resolver: Resolver

    private lateinit var target: SpiderFunctionAnnotationValueFetcher

    @Before
    fun setUp() {
        target = SpiderFunctionAnnotationValueFetcher(ANNOTATION)
    }

    @Test
    fun testFetch() {
        val ksName = Mockito.mock(KSName::class.java).stub {
            on { asString() } doReturn "annotation"
        }
        val ksDeclaration = Mockito.mock(KSDeclaration::class.java).stub {
            on { qualifiedName } doReturn ksName
        }
        val ksType = Mockito.mock(KSType::class.java).stub {
            on { declaration } doReturn ksDeclaration
        }
        val ksReference = Mockito.mock(KSTypeReference::class.java).stub {
            on { resolve() } doReturn ksType
        }
        val ksValueArgument = Mockito.mock(KSValueArgument::class.java).stub {
            on { value } doReturn "test"
        }
        val ksAnnotation = Mockito.mock(KSAnnotation::class.java).stub {
            on { annotationType } doReturn ksReference
            on { arguments } doReturn listOf(ksValueArgument)
        }
        val ksClassDeclaration = Mockito.mock(KSFunctionDeclaration::class.java).stub {
            on { annotations } doReturn sequenceOf(ksAnnotation)
        }

        resolver.stub {
            on { getSymbolsWithAnnotation(ANNOTATION) } doReturn sequenceOf(ksClassDeclaration)
        }
        val result = target.fetch(resolver)
        assertContentEquals(
            sequenceOf("test"),
            result
        )
    }

    @Test
    fun testFetch_multipleValueCase() {
        val ksName = Mockito.mock(KSName::class.java).stub {
            on { asString() } doReturn "annotation"
        }
        val ksDeclaration = Mockito.mock(KSDeclaration::class.java).stub {
            on { qualifiedName } doReturn ksName
        }
        val ksType = Mockito.mock(KSType::class.java).stub {
            on { declaration } doReturn ksDeclaration
        }
        val ksReference = Mockito.mock(KSTypeReference::class.java).stub {
            on { resolve() } doReturn ksType
        }
        val ksValueArgumentFoo = Mockito.mock(KSValueArgument::class.java).stub {
            on { value } doReturn "foo"
        }
        val ksValueArgumentBar = Mockito.mock(KSValueArgument::class.java).stub {
            on { value } doReturn "bar"
        }
        val ksAnnotation = Mockito.mock(KSAnnotation::class.java).stub {
            on { annotationType } doReturn ksReference
            on { arguments } doReturn listOf(ksValueArgumentFoo, ksValueArgumentBar)
        }
        val ksClassDeclaration = Mockito.mock(KSFunctionDeclaration::class.java).stub {
            on { annotations } doReturn sequenceOf(ksAnnotation)
        }

        resolver.stub {
            on { getSymbolsWithAnnotation(ANNOTATION) } doReturn sequenceOf(ksClassDeclaration)
        }
        val result = target.fetch(resolver)
        assertContentEquals(
            sequenceOf("foo", "bar"),
            result
        )
    }

    @Test
    fun testFetch_notFoundValueCase() {
        val ksName = Mockito.mock(KSName::class.java).stub {
            on { asString() } doReturn "annotation"
        }
        val ksDeclaration = Mockito.mock(KSDeclaration::class.java).stub {
            on { qualifiedName } doReturn ksName
        }
        val ksType = Mockito.mock(KSType::class.java).stub {
            on { declaration } doReturn ksDeclaration
        }
        val ksReference = Mockito.mock(KSTypeReference::class.java).stub {
            on { resolve() } doReturn ksType
        }
        val ksAnnotation = Mockito.mock(KSAnnotation::class.java).stub {
            on { annotationType } doReturn ksReference
        }
        val ksClassDeclaration = Mockito.mock(KSFunctionDeclaration::class.java).stub {
            on { annotations } doReturn sequenceOf(ksAnnotation)
        }

        resolver.stub {
            on { getSymbolsWithAnnotation(ANNOTATION) } doReturn sequenceOf(ksClassDeclaration)
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
