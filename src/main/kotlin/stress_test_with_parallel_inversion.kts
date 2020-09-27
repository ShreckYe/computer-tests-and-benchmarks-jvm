// TODO: The results are highly inaccurate: the speed grows linearly to the number of unrolled operations inside the loop!
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread
import kotlin.random.Random

val logger = LoggerFactory.getLogger(this::class.java)

val numThreads = Runtime.getRuntime().availableProcessors()
val initialLong = Random.nextLong()

// Copied and adapted
inline fun repeat(times: Long, action: (Long) -> Unit) {
    for (index in 0 until times) {
        action(index)
    }
}

val inversionsPerRoundBy2pow32 = 64
logger.info("Started.")
val startingTimeMillis = System.currentTimeMillis()
val i = AtomicLong(0L)
val errorCount = AtomicLong(0L)
repeat(numThreads) {
    thread {
        var long = initialLong
        while (!Thread.interrupted()) {
            val iValue = i.incrementAndGet()
            val threadAndRound = "Round $iValue in ${Thread.currentThread().name}"
            logger.info("$threadAndRound...")

            repeat(inversionsPerRoundBy2pow32) {
                repeat(1 shl 26) {
                    // 64 invs
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()

                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()

                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()

                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                    long = long.inv()
                }
            }

            val elapsedTimeMillis = System.currentTimeMillis() - startingTimeMillis
            val numLongsInverted = (iValue + 1) * inversionsPerRoundBy2pow32 * (1L shl 32)
            val numBytesInverted = numLongsInverted shl 3
            logger.info(
                "At least inverted $numBytesInverted bytes in $elapsedTimeMillis ms " +
                        "at the average speed of ${numBytesInverted / elapsedTimeMillis} bytes/ms. " +
                        "(the result gets more and more accurate over time)"
            )
        }
    }
}