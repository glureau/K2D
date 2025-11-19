package com.glureau.k2d.compiler

import com.glureau.k2d.K2DMarkdownClassTable
import com.glureau.k2d.K2DMermaidGraph
import com.glureau.k2d.compiler.K2DSymbolSelector.Companion.symbolSelector
import com.glureau.k2d.compiler.dokka.DokkaModuleMermaidRenderer
import com.glureau.k2d.compiler.dokka.DokkaPackagesMermaidRenderer
import com.glureau.k2d.compiler.dokka.K2DDokkaConfig
import com.glureau.k2d.compiler.markdown.appendMdMermaid
import com.glureau.k2d.compiler.markdown.table.MarkdownClassTableConfiguration
import com.glureau.k2d.compiler.markdown.table.MarkdownClassTableConfiguration.Companion.toDomain
import com.glureau.k2d.compiler.markdown.table.MarkdownTableRenderer
import com.glureau.k2d.compiler.mermaid.MermaidClassRenderer
import com.glureau.k2d.compiler.mermaid.MermaidRendererConfiguration
import com.glureau.k2d.compiler.mermaid.MermaidRendererConfiguration.Companion.toDomain
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import kotlinx.serialization.json.Json
import java.util.Base64
import kotlin.reflect.KClass

// Trick to share the Logger everywhere without injecting the dependency everywhere
internal lateinit var sharedLogger: KSPLogger

internal object Logger : KSPLogger by sharedLogger

@OptIn(KspExperimental::class)
class K2DCompiler(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    init {
        sharedLogger = environment.logger
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val configParamB64 = environment.options["k2d.config"]

        val configuration = if (!configParamB64.isNullOrBlank() && configParamB64 != "null") {
            val configJson = Base64.getDecoder().decode(configParamB64)
            Json.decodeFromString(configJson.decodeToString())
        } else {
            K2DConfiguration()
        }

        val nodeSequence: Sequence<KSNode> = resolver.getNewFiles()
        val aggregatorClassVisitor = AggregatorClassVisitor()
        nodeSequence.forEach { it.accept(aggregatorClassVisitor, Unit) }


        resolver.onFileAnnotation(K2DMermaidGraph::class) { annotation, rawAnnotation, packageName ->
            val name = annotation.name
            val selectorAnnotation: KSAnnotation = rawAnnotation.getArg(K2DMermaidGraph::symbolSelector)
            val tableConfig = annotation.configuration.toDomain()
            // TODO: check if defaults + merge to global config instead?
            val finalConfig = if (tableConfig == MermaidRendererConfiguration()) { // If default value, global config
                configuration.defaultMermaidConfiguration
            } else {
                tableConfig
            }
            // TODO : This crash when using the SymbolSelector by default, see
            //  https://kotlinlang.slack.com/archives/C013BA8EQSE/p1668459386930549?thread_ts=1643275195.027000&cid=C013BA8EQSE
            val selector: K2DSymbolSelector = annotation.symbolSelector.symbolSelector(selectorAnnotation, packageName)

            // TODO: No need of the global config here, it's computed by
            val filtered = aggregatorClassVisitor.classes
                .filter { (_, gClass) -> selector.matches(gClass) }

            val content = buildString {
                appendMdMermaid(
                    MermaidClassRenderer(finalConfig).renderClassDiagram(filtered)
                )
            }.toByteArray()


            val files = filtered.values.mapNotNull { it.originFile }
            environment.writeMarkdown(content, "", name, files)

        }

        resolver.onFileAnnotation(K2DMarkdownClassTable::class) { annotation, rawAnnotation, packageName ->
            val name = annotation.name
            val selectorAnnotation: KSAnnotation = rawAnnotation.getArg(K2DMarkdownClassTable::symbolSelector)
            val tableConfig = annotation.configuration.toDomain()
            // TODO: check if defaults + merge to global config instead?
            val finalConfig = if (tableConfig == MarkdownClassTableConfiguration()) { // If default value, global config
                configuration.defaultMarkdownTableConfiguration
            } else {
                tableConfig
            }
            val selector = annotation.symbolSelector.symbolSelector(selectorAnnotation, packageName)

            // TODO: No need of the global config here, it's computed by
            val filtered = aggregatorClassVisitor.classes
                .filter { (_, gClass) -> selector.matches(gClass) }

            filtered.forEach { (_, gClass) ->
                if (gClass.hide) {
                    Logger.info("Ignoring ${gClass.symbolName}")
                    return@forEach
                }
                val content = MarkdownTableRenderer(finalConfig).renderClassMembers(gClass).toByteArray()

                if (content.isNotEmpty()) {
                    environment.writeMarkdown(
                        content = content,
                        packageName = if (name.isBlank()) gClass.packageName else "",
                        // Replacing "." by "·" allow a proper default order (main class then inner classes)
                        fileName = name.ifBlank { gClass.symbolName.replace(".", "·") + "_table" },
                        dependencies = gClass.originFile?.let { listOf(it) } ?: emptyList())
                }
            }
        }

        configuration.dokkaConfig?.let {
            generateDokka(aggregatorClassVisitor, it, configuration)
        }

        return emptyList()
    }

    private fun <T : Annotation> Resolver.onFileAnnotation(
        klass: KClass<T>,
        block: (T, rawAnnotation: KSAnnotation, packageName: String?) -> Unit,
    ) {
        getSymbolsWithAnnotation(
            annotationName = klass.qualifiedName!!,
            inDepth = true
        )
            .forEach { annotated ->
                annotated.getAnnotationsByType(klass)
                    .forEachIndexed { index, annotation ->
                        // Because getAnnotationsByType doesn't allow retrieval of KClass, we have to also uses the raw KSAnnotation.
                        // https://kotlinlang.slack.com/archives/C013BA8EQSE/p1643275195027000
                        val rawAnnotation = annotated.annotations
                            .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == klass.qualifiedName }
                            .toList()[index]
                        block(annotation, rawAnnotation, (annotated as? KSFile)?.packageName?.asString())
                    }
            }
    }

    private fun <T : Annotation> Resolver.onAnnotation(
        klass: KClass<T>,
        block: (T) -> Unit,
    ) {
        getSymbolsWithAnnotation(
            annotationName = klass.qualifiedName!!,
            inDepth = true
        )
            .forEach { annotated ->
                annotated.getAnnotationsByType(klass)
                    .forEach { block(it) }
            }
    }

    private fun generateDokka(
        aggregatorClassVisitor: AggregatorClassVisitor,
        dokkaConfig: K2DDokkaConfig,
        configuration: K2DConfiguration,
    ) {
        val data = aggregatorClassVisitor.classes

        if (data.isNotEmpty()) {
            if (dokkaConfig.generateMermaidOnPackages)
                DokkaPackagesMermaidRenderer(environment, configuration.defaultMermaidConfiguration).render(
                    data,
                    aggregatorClassVisitor.moduleClasses
                )
            if (dokkaConfig.generateMermaidOnModules)
                DokkaModuleMermaidRenderer(environment, configuration.defaultMermaidConfiguration).render(
                    data,
                    aggregatorClassVisitor.moduleClasses
                )
        }
    }
}

class K2DCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        K2DCompiler(environment)
}