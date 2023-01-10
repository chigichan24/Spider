package net.chigita.spider.fetcher

import com.google.devtools.ksp.processing.Resolver

/**
 * An interface of target value fetcher.
 */
interface AnnotatedValueFetcher {
    fun fetch(resolver: Resolver): Sequence<String>
}
