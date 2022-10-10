package com.glureau.mermaidksp.compiler

import K2DConfiguration
import K2DDokkaConfig
import K2DSymbolSelectorAnnotation
import MermaidGraph
import com.glureau.mermaidksp.compiler.dokka.DokkaModuleMarkdownRenderer
import com.glureau.mermaidksp.compiler.dokka.DokkaPackagesMarkdownRenderer
import com.glureau.mermaidksp.compiler.markdown.appendMdMermaid
import com.glureau.mermaidksp.compiler.mermaid.MermaidClassRenderer
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty1

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


        resolver.getSymbolsWithAnnotation(
            annotationName = MermaidGraph::class.qualifiedName!!,
            inDepth = true
        )
            .forEach { annotated ->
                annotated.annotations
                    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == MermaidGraph::class.qualifiedName }
                    .forEach { annotation ->
                        val name = annotation.getArg<String>(MermaidGraph::name)
                        Logger.warn("ANNOTATION = $name")
                        // TODO: remove?
                        //  val klasses = annotation.getArg<List<KSType>>(MermaidGraph::klasses)
                        //  val nodeSequence = klasses.map { it.declaration }.asSequence()

                        fun KSAnnotation.argFrom(kProp: KProperty1<*, String>) =
                            arguments.first { it.name?.asString() == kProp.name }

                        val annotationSymbolSelector = annotation.getArg<KSAnnotation>(MermaidGraph::symbolSelector)
                        val includesFqnRegex =
                            annotationSymbolSelector.argFrom(K2DSymbolSelectorAnnotation::includesFqnRegex).value as String
                        val excludesFqnRegex =
                            annotationSymbolSelector.argFrom(K2DSymbolSelectorAnnotation::excludesFqnRegex).value as String

                        Logger.warn("ANNOTATION.symbolSelector = ${annotationSymbolSelector.arguments}")

                        /*
                        val mermaidClassVisitor = AggregatorClassVisitor()
                        nodeSequence.forEach { it.accept(mermaidClassVisitor, Unit) }
                        val data = mermaidClassVisitor.classes*/
                        // TODO: No need of the global config here, it's by
                        val includeRegex = Regex(includesFqnRegex)
                        val excludeRegex = Regex(excludesFqnRegex)
                        val filtered = aggregatorClassVisitor.classes.filter { (fqn, klass) ->
                            includeRegex.matches(fqn) && !excludeRegex.matches(fqn)
                        }

                        val content = buildString {
                            appendMdMermaid(MermaidClassRenderer().renderClassDiagram(filtered))
                        }.toByteArray()

                        Logger.warn("content=${content}")
                        val files = filtered.values.mapNotNull { it.originFile }

                        environment.logger.warn("Rendering markdown $name.md")
                        environment.writeMarkdown(content, "", name, files)
                    }
            }

        configuration.dokkaConfig?.let {
            generateDokka(aggregatorClassVisitor, it)
        }

        return emptyList()
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

@KotlinPoetKspPreview
class MermaidCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        MermaidCompiler(environment)
}