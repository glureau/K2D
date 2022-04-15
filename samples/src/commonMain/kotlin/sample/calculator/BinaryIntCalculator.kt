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
    fun add(t: T)
}