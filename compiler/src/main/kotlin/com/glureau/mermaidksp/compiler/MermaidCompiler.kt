package com.glureau.mermaidksp.compiler

import com.glureau.mermaidksp.compiler.renderer.PackageMarkdownRenderer
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

// Trick to share the Logger everywhere without injecting the dependency everywhere
internal lateinit var sharedLogger: KSPLogger

internal object Logger : KSPLogger by sharedLogger

@KotlinPoetKspPreview
class MermaidCompiler(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    init {
        sharedLogger = environment.logger
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        /*
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
                        generate(resolver, klasses.map { it.declaration }.asSequence(), name)
                    }
            }
        */

        val nodeSequence: Sequence<KSNode> = resolver.getNewFiles()
        generate(resolver, nodeSequence, "MermaidUml")

        return emptyList()
    }

    private fun generate(
        resolver: Resolver,
        nodeSequence: Sequence<KSNode>,
        fileName: String
    ) {
        val mermaidClassVisitor = MermaidClassVisitor(resolver)
        nodeSequence.forEach { it.accept(mermaidClassVisitor, Unit) }
        val data = mermaidClassVisitor.classes

        if (data.isNotEmpty()) {
            PackageMarkdownRenderer(environment).render(data)
            //BasicMarkdownRenderer(environment).render(data, fileName)
        }
    }
}

@KotlinPoetKspPreview
class MermaidCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        MermaidCompiler(environment)
}