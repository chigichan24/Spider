package net.chigita.spider

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import net.chigita.spider.annotation.AGSL_ENUM

/**
 * A class which represents fetching analyze target file names.
 */
internal class SpiderTargetFileNameFetcher(
    private val enumAnnotationName: String? = AGSL_ENUM::class.qualifiedName
) {
    fun fetch(resolver: Resolver): Sequence<String> =
        (fetchEnumDefinedFiles(resolver) + fetchFunctionDefinedFiles()).distinct()

    private fun fetchFunctionDefinedFiles(): Sequence<String> {
        return emptySequence()
    }

    private fun fetchEnumDefinedFiles(resolver: Resolver): Sequence<String> {
        val annotationName = enumAnnotationName ?: return emptySequence()

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
