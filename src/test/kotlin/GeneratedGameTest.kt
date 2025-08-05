import com.dpforge.easyraster.Generator
import com.dpforge.easyraster.Position
import com.dpforge.easyraster.SolutionFinder
import com.dpforge.easyraster.Solver
import com.dpforge.easyraster.decodeField
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratedGameTest {
    @Test
    fun `seed 317000030320750, size 7`() {
        val queens = solve(317000030320750, 7)
        assertEquals(
            setOf(
                Position(row = 0, col = 5),
                Position(row = 1, col = 1),
                Position(row = 2, col = 4),
                Position(row = 3, col = 6),
                Position(row = 4, col = 2),
                Position(row = 5, col = 0),
                Position(row = 6, col = 3),
            ),
            queens
        )
    }

    @Test
    fun `seed 1161055006196476477, size 9`() {
        val queens = solve(1161055006196476477, 9)
        assertEquals(
            setOf(
                Position(row = 0, col = 3),
                Position(row = 1, col = 8),
                Position(row = 2, col = 4),
                Position(row = 3, col = 1),
                Position(row = 4, col = 7),
                Position(row = 5, col = 2),
                Position(row = 6, col = 5),
                Position(row = 7, col = 0),
                Position(row = 8, col = 6),
            ),
            queens
        )
    }

    private fun solve(seed: Long, fieldSize: Int): Set<Position> {
        val field = Generator(Random(seed)).generate(fieldSize)
        val allSolutions = SolutionFinder().findAllSolutions(field)
        assertEquals(1, allSolutions.size)

        val result = Solver().solveField(field)
        assertEquals(allSolutions.first(), result)
        return result
    }
}