package com.glureau.mermaidksp.compiler

import MermaidGraph
import com.glureau.mermaidksp.compiler.dokka.DokkaModuleMarkdownRenderer
import com.glureau.mermaidksp.compiler.dokka.DokkaPackagesMarkdownRenderer
import com.glureau.mermaidksp.compiler.markdown.DemoMarkdownRenderer
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

// Trick to share the Logger everywhere without injecting the dependency everywhere
internal lateinit var sharedLogger: KSPLogger

internal object Logger : KSPLogger by sharedLogger

@KotlinPoetKspPreview
class MermaidCompiler(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    init {
        sharedLogger = environment.logger
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(
            annotationName = MermaidGraph::class.qualifiedName!!,
            inDepth = true
        )
            .forEach { annotated ->
                annotated.annotations
                    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == MermaidGraph::class.qualifiedName }
                    .forEach { annotation ->
                        val name = annotation.getArg<String>(MermaidGraph::name)
                        val klasses = annotation.getArg<List<KSType>>(MermaidGraph::klasses)
                        val nodeSequence = klasses.map { it.declaration }.asSequence()

                        val mermaidClassVisitor = AggregatorClassVisitor()
                        nodeSequence.forEach { it.accept(mermaidClassVisitor, Unit) }
                        val data = mermaidClassVisitor.classes

                        DemoMarkdownRenderer(environment).render(data, name)
                    }
            }

        val nodeSequence: Sequence<KSNode> = resolver.getNewFiles()
        generate(nodeSequence)

        return emptyList()
    }

    private fun generate(
        nodeSequence: Sequence<KSNode>
    ) {
        val aggregatorClassVisitor = AggregatorClassVisitor()
        nodeSequence.forEach { it.accept(aggregatorClassVisitor, Unit) }
        val data = aggregatorClassVisitor.classes

        if (data.isNotEmpty()) {
            DokkaPackagesMarkdownRenderer(environment).render(data, aggregatorClassVisitor.moduleClasses)
            DokkaModuleMarkdownRenderer(environment).render(data, aggregatorClassVisitor.moduleClasses)
            //BasicMarkdownRenderer(environment).render(data, fileName)
        }
    }
}

@KotlinPoetKspPreview
class MermaidCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        MermaidCompiler(environment)
}