package net.chigita.spider

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import net.chigita.spider.annotation.AGSL

/**
 * A class which represents fetching analyze target file names.
 */
internal class SpiderTargetFileNameFetcher() {
    fun fetch(
        resolver: Resolver,
        annotationName: String? = AGSL::class.qualifiedName
    ): Sequence<String> {
        if (annotationName == null) {
            return emptySequence()
        }
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
