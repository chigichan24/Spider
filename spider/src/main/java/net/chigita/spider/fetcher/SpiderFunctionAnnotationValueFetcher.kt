package net.chigita.spider.fetcher

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import net.chigita.spider.annotation.AGSL

/**
 * A class which represents fetching and analyzing target file names from annotation value.
 */
internal class SpiderFunctionAnnotationValueFetcher(
    private val annotationName: String? = AGSL::class.qualifiedName
) : AnnotatedValueFetcher {
    override fun fetch(resolver: Resolver): Sequence<String> {
        val annotationName = annotationName ?: return emptySequence()

        return resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSFunctionDeclaration>()
            .fetchArguments(annotationName)
    }

    private fun Sequence<KSFunctionDeclaration>.fetchArguments(
        annotationName: String
    ): Sequence<String> = flatMap<KSFunctionDeclaration, String> { ksFunctionDeclaration ->
        val targetAnnotation = ksFunctionDeclaration.annotations.find {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName
        }
        targetAnnotation?.arguments
            ?.mapNotNull { it.value?.toString() }
            ?.asSequence()
            ?: emptySequence()
    }
}
