package net.chigita.spider.fetcher

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import net.chigita.spider.annotation.AGSL_ENUM

/**
 * A class which represents fetching and analyzing target file names from enum class.
 */
internal class SpiderEnumValueFetcher(
    private val annotationName: String? = AGSL_ENUM::class.qualifiedName
) : AnnotatedValueFetcher {
    override fun fetch(resolver: Resolver): Sequence<String> {
        val annotationName = annotationName ?: return emptySequence()

        return resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.ENUM_CLASS }
            .flatMap { it.declarations.map { property -> property.simpleName.getShortName() } }
            .filterNot { it == IGNORE_FUNCTION_DECLARATION }
    }

    companion object {
        private const val IGNORE_FUNCTION_DECLARATION = "<init>"
    }
}
