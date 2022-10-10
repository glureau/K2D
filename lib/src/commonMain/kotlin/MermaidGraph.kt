import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Repeatable
annotation class MermaidGraph(
    val name: String,
    val klasses: Array<KClass<*>> = emptyArray(), //TODO : use K2DSymbolSelector instead
    val symbolSelector: K2DSymbolSelectorAnnotation = K2DSymbolSelectorAnnotation(),
)

@Repeatable
annotation class ClassMembersTable(
    val symbolSelector: K2DSymbolSelectorAnnotation = K2DSymbolSelectorAnnotation(),
)

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
    val excludesFqnRegex: String = ".*", // field is used as a Regex
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