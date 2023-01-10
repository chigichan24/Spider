package net.chigita.spider

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import net.chigita.spider.annotation.AGSL
import net.chigita.spider.annotation.AGSL_ENUM

/**
 * A class which represents fetching analyze target file names.
 */
internal class SpiderTargetFileNameFetcher(
    private val enumDefinedAnnotationName: String? = AGSL_ENUM::class.qualifiedName,
    private val functionDefinedAnnotationName: String? = AGSL::class.qualifiedName
) {
    fun fetch(resolver: Resolver): Sequence<String> =
        (fetchEnumDefinedFiles(resolver) + fetchFunctionDefinedFiles(resolver)).distinct()

    private fun fetchFunctionDefinedFiles(resolver: Resolver): Sequence<String> {
        val annotationName = functionDefinedAnnotationName ?: return emptySequence()

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

    private fun fetchEnumDefinedFiles(resolver: Resolver): Sequence<String> {
        val annotationName = enumDefinedAnnotationName ?: return emptySequence()

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
