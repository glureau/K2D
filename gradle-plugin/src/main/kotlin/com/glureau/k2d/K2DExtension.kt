package com.glureau.k2d

import com.glureau.k2d.compiler.K2DConfiguration
import com.glureau.k2d.compiler.K2DSymbolSelector
import com.glureau.k2d.compiler.dokka.K2DDokkaConfig
import com.glureau.k2d.compiler.markdown.table.K2DMarkdownTableConfiguration
import com.glureau.k2d.compiler.mermaid.K2DMermaidRendererConfiguration
import org.gradle.api.Action
import org.gradle.api.tasks.Nested

abstract class K2DExtension {
    @Nested
    private var dokkaConfig: K2DDokkaConfig? = null
    fun dokka(configure: Action<K2DDokkaConfig>) {
        if (dokkaConfig == null) dokkaConfig = K2DDokkaConfig()
        dokkaConfig?.apply(configure)
    }

    @Nested
    private var customMermaidConfigs: MutableList<K2DSymbolSelector> = mutableListOf()
    fun customMermaid(configure: Action<K2DSymbolSelector>) {
        customMermaidConfigs.add(
            K2DSymbolSelector().apply(configure)
        )
    }

    @Nested
    private val defaultMarkdownTableConfiguration: K2DMarkdownTableConfiguration = K2DMarkdownTableConfiguration()
    fun defaultMarkdownTableConfiguration(configure: Action<K2DMarkdownTableConfiguration>) {
        defaultMarkdownTableConfiguration.apply(configure)
    }

    @Nested
    private val defaultMermaidConfiguration: K2DMermaidRendererConfiguration = K2DMermaidRendererConfiguration()
    fun defaultMermaidConfiguration(configure: Action<K2DMermaidRendererConfiguration>) {
        defaultMermaidConfiguration.apply(configure)
    }

    internal fun fullConfiguration() = K2DConfiguration(
        dokkaConfig = this.dokkaConfig,
        customMermaidConfigs = this.customMermaidConfigs,
        defaultMarkdownTableConfiguration = this.defaultMarkdownTableConfiguration,
        defaultMermaidConfiguration = this.defaultMermaidConfiguration,
    )
}

fun <T> T.apply(configure: Action<T>) = apply { configure.execute(this) }