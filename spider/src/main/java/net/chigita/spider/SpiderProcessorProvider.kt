package net.chigita.spider

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import net.chigita.spider.fetcher.SpiderAnnotatedValueFetcherConsolidator

/**
 * A class which provides [SpiderProcessor] with initialization.
 */
class SpiderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        SpiderProcessor(
            environment.codeGenerator,
            SpiderAnnotatedValueFetcherConsolidator(),
            SpiderGeneratedCodeProvider()
        )
}
