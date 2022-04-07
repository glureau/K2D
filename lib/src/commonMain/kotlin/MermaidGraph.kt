import kotlin.reflect.KClass

@Repeatable
annotation class MermaidGraph(
    val name: String,
    val klasses: Array<KClass<*>>
)
