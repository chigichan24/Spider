package net.chigita.spider.fetcher

import com.google.devtools.ksp.processing.Resolver

/**
 * A class which consolidates each annotated value fetch result.
 */
internal class SpiderAnnotatedValueFetcherConsolidator(
    private val enumValueFetcher: SpiderEnumValueFetcher = SpiderEnumValueFetcher(),
    private val functionAnnotationValueFetcher: SpiderFunctionAnnotationValueFetcher =
        SpiderFunctionAnnotationValueFetcher()
) : AnnotatedValueFetcher {
    override fun fetch(resolver: Resolver): Sequence<String> {
        val fetcherList: List<AnnotatedValueFetcher> =
            listOf(enumValueFetcher, functionAnnotationValueFetcher)
        return fetcherList.flatMap {
            it.fetch(resolver)
        }.asSequence().distinct()
    }
}
