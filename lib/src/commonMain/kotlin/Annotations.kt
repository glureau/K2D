import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Repeatable
annotation class MermaidGraph(
    val name: String,
    val symbolSelector: K2DSymbolSelectorAnnotation = K2DSymbolSelectorAnnotation(),
)

@Repeatable
annotation class ClassMembersTable(
    val symbolSelector: K2DSymbolSelectorAnnotation = K2DSymbolSelectorAnnotation(),
)

@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
)
annotation class K2DHide

@Serializable
data class K2DConfiguration(
    val dokkaConfig: K2DDokkaConfig? = null,
    val customMermaidConfigs: List<K2DSymbolSelector> = emptyList()
)

@Serializable
data class K2DDokkaConfig(
    val generateMermaidOnModules: Boolean = true,
    val generateMermaidOnPackages: Boolean = true,
)

annotation class K2DSymbolSelectorAnnotation(
    val includesFqnRegex: String = ".*", // field is used as a Regex
    val includesClasses: Array<KClass<*>> = emptyArray(),
    val includesClassesInheritingFrom: Array<KClass<*>> = emptyArray(),

    val excludesFqnRegex: String = ".*", // field is used as a Regex
    val excludesClasses: Array<KClass<*>> = emptyArray(),
    val excludesClassesInheritingFrom: Array<KClass<*>> = emptyArray(),
)

fun K2DSymbolSelectorAnnotation.toSelector() = K2DSymbolSelector(
    includesFqnRegex = includesFqnRegex,
    excludesFqnRegex = excludesFqnRegex,
)

@Serializable
data class K2DSymbolSelector(
    val includesFqnRegex: String = ".*", // field is used as a Regex
    val excludesFqnRegex: String = ".*", // field is used as a Regex
)