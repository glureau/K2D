package sample.calculator

@Suppress("Unused")
class BinaryIntCalculator(
    var reg1: Int = 1,
    var reg2: Int = 2
) {
    fun calculate(binaryOp: (a: Int, b: () -> Int) -> Int): Int {
        return binaryOp(reg1) { reg2 }
    }
}

@Suppress("Unused")
interface GenericFoo<T : Number> {
    val list: List<T>
    val latestResult: Result<Int>
    val lambda: (String) -> Double

    fun add(t: T)
    fun addAll(others: List<T>): List<T>
    fun computeResult(): Result<Int>
}