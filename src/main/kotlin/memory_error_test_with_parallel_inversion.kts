// Repeatedly invert arrays utilizing all cores to check for memory errors
// Set `-Xmx` to be close to the total amount of system memory
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread
import kotlin.random.Random

val logger = LoggerFactory.getLogger(this::class.java)

// The best number of threads (that leads to the best speed) is often limited by the memory speed
val numThreads = Runtime.getRuntime().availableProcessors()
val maxMemory = Runtime.getRuntime().maxMemory()
logger.info("Number of threads: $numThreads; max memory: $maxMemory.")

// A good value should cover as much memory as possible, avoid hitting the max heap size too frequently, and also avoid OOM Kills
val constantMemory = maxMemory / 6
val roughNumArrayBytes = constantMemory / numThreads
val numArrayBytes = roughNumArrayBytes and 7L.inv()
val arraySize = Math.toIntExact(roughNumArrayBytes shr 3)
logger.info("Constant memory: $constantMemory; array bytes: $numArrayBytes; array size: $arraySize.")

logger.info("Initializing a random array of $numArrayBytes bytes...")
val initialArray = LongArray(arraySize) { Random.nextLong() }
logger.info("Array initialized.")

fun LongArray.inv(): LongArray =
    Arrays.stream(this).map { it.inv() }.toArray()

fun LongArray.invSelf() {
    for (i in 0 until size)
        this[i] = this[i].inv()
}


val inversionsPerRound = 32 // must be even and should be large enough to eliminate minor factors
logger.info("Started.")
val startingTimeMillis = System.currentTimeMillis()
val i = AtomicLong(0L)
val errorCount = AtomicLong(0L)
repeat(numThreads) {
    thread {
        var array = initialArray

        while (!Thread.interrupted()) {
            val iValue = i.incrementAndGet()
            val threadAndRound = "Round $iValue in ${Thread.currentThread().name}"

            logger.info("$threadAndRound...")
            // Make a copy to make changes or adjust memory location
            array = array.copyOf()
            repeat(inversionsPerRound) {
                array.invSelf()
            }

            val errorCountValue: Long
            if (initialArray contentEquals array) {
                errorCountValue = errorCount.get()
                logger.info("$threadAndRound: No memory errors.")
            } else {
                errorCountValue = errorCount.incrementAndGet()
                logger.error("$threadAndRound: At least one memory error occurred in round $i.\n")
            }

            val elapsedTimeMillis = System.currentTimeMillis() - startingTimeMillis
            val numBytesInverted = numArrayBytes * (iValue + 1) * inversionsPerRound
            logger.info(
                "At least $errorCountValue accumulated errors have occurred in $elapsedTimeMillis ms " +
                        "inverting $numBytesInverted bytes " +
                        "at the average speed of ${numBytesInverted / elapsedTimeMillis} bytes/ms. " +
                        "(the result gets more and more accurate over time)"
            )
        }
    }
}