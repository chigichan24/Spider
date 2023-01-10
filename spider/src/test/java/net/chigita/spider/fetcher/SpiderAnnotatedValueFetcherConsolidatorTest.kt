package net.chigita.spider.fetcher

import com.google.devtools.ksp.processing.Resolver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.mockito.quality.Strictness
import kotlin.test.assertContentEquals

/**
 * Tests for [SpiderAnnotatedValueFetcherConsolidator].
 */
class SpiderAnnotatedValueFetcherConsolidatorTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var resolver: Resolver

    @Mock
    private lateinit var enumValueFetcher: SpiderEnumValueFetcher

    @Mock
    private lateinit var functionValueFetcher: SpiderFunctionAnnotationValueFetcher

    private lateinit var target: SpiderAnnotatedValueFetcherConsolidator

    @Before
    fun setUp() {
        target = SpiderAnnotatedValueFetcherConsolidator(enumValueFetcher, functionValueFetcher)
    }

    @Test
    fun testFetch() {
        enumValueFetcher.stub {
            on { fetch(resolver) } doReturn sequenceOf("foo")
        }
        functionValueFetcher.stub {
            on { fetch(resolver) } doReturn sequenceOf("bar")
        }
        val result = target.fetch(resolver)
        assertContentEquals(
            sequenceOf("foo", "bar"),
            result
        )
    }

    @Test
    fun testFetch_conflictCase() {
        enumValueFetcher.stub {
            on { fetch(resolver) } doReturn sequenceOf("foo")
        }
        functionValueFetcher.stub {
            on { fetch(resolver) } doReturn sequenceOf("foo")
        }
        val result = target.fetch(resolver)
        assertContentEquals(
            sequenceOf("foo"),
            result
        )
    }
}
