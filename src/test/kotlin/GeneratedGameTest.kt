import com.dpforge.easyraster.Position
import com.dpforge.easyraster.SolutionFinder
import com.dpforge.easyraster.Solver
import com.dpforge.easyraster.decodeField
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratedGameTest {
    @Test
    fun test1() {
        val queens = solve(
            "rwwwyyg",
            "rrwwyyg",
            "vvwbppg",
            "vvwbbpg",
            "vvwbbbb",
            "vvwbbbb",
            "vvvbbbb"
        )
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
    fun test2() {
        val queens = solve(
            "bbboooogg",
            "bbbyppoog",
            "bbdypvvrr",
            "wbdyyvvrr",
            "wbdyyvvrr",
            "wwddyvvrr",
            "wwwdyvvvv",
            "wwwdyyyvv",
            "wwwdddyyv"
        )
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

    private fun solve(vararg encodedField: String): Set<Position> {
        val field = decodeField(encodedField.toList())
        val allSolutions = SolutionFinder().findAllSolutions(field)
        assertEquals(1, allSolutions.size)

        val result = Solver().solveField(field)
        assertEquals(allSolutions.first(), result)
        return result
    }
}