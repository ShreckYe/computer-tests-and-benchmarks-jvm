// Repeatedly invert an array and check for errors
import java.util.*
import kotlin.random.Random

val DEFAULT_NUM_BYTES = 1 shl 30
val numBytes = args.getOrNull(0)?.toInt() ?: DEFAULT_NUM_BYTES

println("Initializing an array of $numBytes bytes...")
val array = LongArray(numBytes shr 3) { Random.nextLong() }
fun LongArray.parallelInv(): LongArray =
    Arrays.stream(this).parallel().map { it.inv() }.toArray()


println("Started.")
var errorCount = 0
val startingTimeMillis = System.currentTimeMillis()
for (i in 0 until Long.MAX_VALUE) {
    println("Round $i...")
    if (array contentEquals array.parallelInv().parallelInv())
        println("No memory errors.")
    else {
        errorCount++
        System.err.println("At least one memory error occurred in round $i.\n")
    }

    val numBytesWritten = numBytes * (i + 1) * 2
    val numBytesRead = numBytesWritten * 2
    println(
        "$errorCount accumulated errors have occurred in ${System.currentTimeMillis() - startingTimeMillis} ms " +
                "writing $numBytesWritten bytes and reading $numBytesRead bytes."
    )
}
