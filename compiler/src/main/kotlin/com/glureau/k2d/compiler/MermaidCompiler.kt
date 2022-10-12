package com.glureau.k2d.compiler

import com.glureau.k2d.*
import com.glureau.k2d.compiler.dokka.DokkaModuleMarkdownRenderer
import com.glureau.k2d.compiler.dokka.DokkaPackagesMarkdownRenderer
import com.glureau.k2d.compiler.markdown.appendMdMermaid
import com.glureau.k2d.compiler.markdown.table.MarkdownTableRenderer
import com.glureau.k2d.compiler.mermaid.MermaidClassRenderer
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

// Trick to share the Logger everywhere without injecting the dependency everywhere
internal lateinit var sharedLogger: KSPLogger

internal object Logger : KSPLogger by sharedLogger

@KotlinPoetKspPreview
class MermaidCompiler(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    init {
        sharedLogger = environment.logger
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val configuration =
            environment.options["k2d.config"]?.let { Json.decodeFromString<K2DConfiguration>(it) }
                ?: K2DConfiguration()

        Logger.warn("config = $configuration")

        val nodeSequence: Sequence<KSNode> = resolver.getNewFiles()
        Logger.warn("nodeSequence: $nodeSequence")
        val aggregatorClassVisitor = AggregatorClassVisitor()
        nodeSequence.forEach { it.accept(aggregatorClassVisitor, Unit) }
        Logger.warn("aggro=${aggregatorClassVisitor.classes.keys}")


        resolver.onAnnotation(K2DMermaidGraph::class) { annotation ->
            val name = annotation.getArg<String>(K2DMermaidGraph::name)
            val annotationSymbolSelector = annotation.getArg<KSAnnotation>(K2DMermaidGraph::symbolSelector)
            val selector = annotationSymbolSelector.symbolSelector()

            // TODO: No need of the global config here, it's computed by
            val includeRegex = Regex(selector.includesFqnRegex)
            val excludeRegex = Regex(selector.excludesFqnRegex)
            val filtered = aggregatorClassVisitor.classes.filter { (fqn, _) ->
                includeRegex.matches(fqn) && !excludeRegex.matches(fqn)
            }

            // TODO: get MermaidRendererConfiguration from annotation
            val content = buildString { appendMdMermaid(MermaidClassRenderer().renderClassDiagram(filtered)) }
                .toByteArray()


            val files = filtered.values.mapNotNull { it.originFile }
            environment.logger.warn("Rendering markdown $name.md")
            environment.writeMarkdown(content, "", name, files)
        }

        resolver.onAnnotation(K2DClassMembersTable::class) { annotation ->
            val annotationSymbolSelector = annotation.getArg<KSAnnotation>(K2DClassMembersTable::symbolSelector)
            val selector = annotationSymbolSelector.symbolSelector()

            // TODO: No need of the global config here, it's computed by
            val includeRegex = Regex(selector.includesFqnRegex)
            val excludeRegex = Regex(selector.excludesFqnRegex)
            val filtered = aggregatorClassVisitor.classes.filter { (fqn, _) ->
                includeRegex.matches(fqn) && !excludeRegex.matches(fqn)
            }
            filtered.forEach { (_, gClass) ->
                if (gClass.hide) {
                    Logger.info("Ignoring ${gClass.symbolName}")
                    return@forEach
                }
                val content = MarkdownTableRenderer().renderClassMembers(gClass).toByteArray()

                Logger.warn("Rendering table ${gClass.symbolName}.md")
                environment.writeMarkdown(
                    content = content,
                    packageName = gClass.packageName,
                    fileName = "table_" + gClass.symbolName,
                    dependencies = gClass.originFile?.let { listOf(it) } ?: emptyList())

            }
        }

        configuration.dokkaConfig?.let {
            generateDokka(aggregatorClassVisitor, it)
        }

        return emptyList()
    }

    private fun Resolver.onAnnotation(klass: KClass<*>, act: (KSAnnotation) -> Unit) {
        getSymbolsWithAnnotation(
            annotationName = klass.qualifiedName!!,
            inDepth = true
        )
            .forEach { annotated ->
                annotated.annotations
                    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == klass.qualifiedName }
                    .forEach { annotation ->
                        act(annotation)
                    }
            }
    }

    private fun generateDokka(
        aggregatorClassVisitor: AggregatorClassVisitor,
        dokkaConfig: K2DDokkaConfig
    ) {
        val data = aggregatorClassVisitor.classes

        if (data.isNotEmpty()) {
            if (dokkaConfig.generateMermaidOnPackages)
                DokkaPackagesMarkdownRenderer(environment).render(data, aggregatorClassVisitor.moduleClasses)
            if (dokkaConfig.generateMermaidOnModules)
                DokkaModuleMarkdownRenderer(environment).render(data, aggregatorClassVisitor.moduleClasses)
        }
    }
}

private fun KSAnnotation.symbolSelector(): K2DSymbolSelector {
    val includesFqnRegex = (argFrom(K2DSymbolSelectorAnnotation::includesFqnRegex).value as? String).orEmpty()
    val excludesFqnRegex = (argFrom(K2DSymbolSelectorAnnotation::excludesFqnRegex).value as? String).orEmpty()
    return K2DSymbolSelector(includesFqnRegex = includesFqnRegex, excludesFqnRegex = excludesFqnRegex)
}

@KotlinPoetKspPreview
class MermaidCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        MermaidCompiler(environment)
}