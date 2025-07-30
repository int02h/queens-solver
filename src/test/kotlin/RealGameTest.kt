import com.dpforge.easyraster.Position
import com.dpforge.easyraster.SolutionFinder
import com.dpforge.easyraster.Solver
import com.dpforge.easyraster.decodeField
import kotlin.test.Test
import kotlin.test.assertEquals

class RealGameTest {
    @Test
    fun `2025-Jul-23`() {
        val queens = solve(
            "pppobbb",
            "pgggggb",
            "pgwwwgb",
            "pggwggr",
            "pppyrrr",
            "ppyyyrr",
            "pppyrrr",
        )
        assertEquals(
            setOf(
                Position(row = 0, col = 3),
                Position(row = 1, col = 6),
                Position(row = 5, col = 2),
                Position(row = 2, col = 4),
                Position(row = 3, col = 1),
                Position(row = 6, col = 0),
                Position(row = 4, col = 5)
            ),
            queens
        )
    }

    @Test
    fun `2025-Apr-18`() {
        val queens = solve(
            "ppppppo",
            "pwgyyyo",
            "pwgggyo",
            "pwwbgyo",
            "prwbbbo",
            "prrrrbo",
            "poooooo"
        )
        assertEquals(
            setOf(
                Position(row = 3, col = 1),
                Position(row = 4, col = 4),
                Position(row = 5, col = 2),
                Position(row = 2, col = 3),
                Position(row = 1, col = 5),
                Position(row = 6, col = 6),
                Position(row = 0, col = 0)
            ),
            queens
        )
    }

    @Test
    fun `2025-Apr-7`() {
        val queens = solve(
            "yyyyyyyy",
            "ypppoyod",
            "ypwpoyod",
            "ypwpoood",
            "ybwwgrdd",
            "ybwwgrdd",
            "ybbbgggd",
            "yddddddd",
        )
        assertEquals(
            setOf(
                Position(row = 4, col = 5),
                Position(row = 5, col = 1),
                Position(row = 3, col = 2),
                Position(row = 1, col = 3),
                Position(row = 2, col = 6),
                Position(row = 6, col = 4),
                Position(row = 7, col = 7),
                Position(row = 0, col = 0)
            ),
            queens
        )
    }

    @Test
    fun `2025-Apr-2`() {
        val queens = solve(
            "wwwwoob",
            "wwwoobb",
            "wwoobbb",
            "wgppprb",
            "wggprrb",
            "wwgyrbb",
            "wwyyybb",
        )
        assertEquals(
            setOf(
                Position(row = 3, col = 3),
                Position(row = 6, col = 2),
                Position(row = 4, col = 1),
                Position(row = 5, col = 4),
                Position(row = 0, col = 5),
                Position(row = 2, col = 6),
                Position(row = 1, col = 0)
            ),
            queens
        )
    }

    @Test
    fun `2024-Oct-17`() {
        val queens = solve(
            "byyydwww",
            "bbbydddw",
            "byyydwww",
            "yypyywrr",
            "ggpyowwr",
            "gpppooor",
            "grrrorrr",
            "rrrrrrrr",
        )
        assertEquals(
            setOf(
                Position(row = 1, col = 1),
                Position(row = 0, col = 4),
                Position(row = 2, col = 3),
                Position(row = 3, col = 5),
                Position(row = 4, col = 2),
                Position(row = 6, col = 0),
                Position(row = 5, col = 6),
                Position(row = 7, col = 7)
            ),
            queens
        )
    }

    @Test
    fun `2024-Oct-21`() {
        val queens = solve(
            "ppooooo",
            "ppooobo",
            "oogggoo",
            "oogwwoo",
            "oogwooo",
            "orooooy",
            "ooooyyy",
        )
        assertEquals(
            setOf(
                Position(row = 1, col = 5),
                Position(row = 5, col = 1),
                Position(row = 0, col = 0),
                Position(row = 2, col = 2),
                Position(row = 6, col = 4),
                Position(row = 4, col = 3),
                Position(row = 3, col = 6)
            ),
            queens
        )
    }

    @Test
    fun `2025-Mar-16`() {
        val queens = solve(
            "ppppppoo",
            "pppbgooo",
            "ppbbggoo",
            "pwwwgggo",
            "prrryyyo",
            "pdrryydo",
            "dddryddo",
            "ddddddoo"
        )
        assertEquals(
            setOf(
                Position(row = 1, col = 3),
                Position(row = 2, col = 5),
                Position(row = 3, col = 1),
                Position(row = 5, col = 2),
                Position(row = 0, col = 0),
                Position(row = 6, col = 7),
                Position(row = 7, col = 4),
                Position(row = 4, col = 6)
            ),
            queens
        )
    }

    @Test
    fun `2025-Mar-31`() {
        val queens = solve(
            "ppoooob",
            "pppgppb",
            "pppgppb",
            "pwwgppb",
            "ppppppb",
            "prppppp",
            "pyyyyyy"
        )
        assertEquals(
            setOf(
                Position(row = 5, col = 1),
                Position(row = 3, col = 2),
                Position(row = 1, col = 3),
                Position(row = 0, col = 5),
                Position(row = 6, col = 4),
                Position(row = 2, col = 0),
                Position(row = 4, col = 6)
            ),
            queens
        )
    }

    @Test
    fun `2025-Jul-24`() {
        val queens = solve(
            "pppppppp",
            "pppppopp",
            "bbbppggg",
            "wbrpprgy",
            "wbrrrrgy",
            "bbbrrggg",
            "ddrrdddd",
            "dddddddd",
        )
        assertEquals(
            setOf(
                Position(row = 0, col = 3),
                Position(row = 1, col = 5),
                Position(row = 2, col = 1),
                Position(row = 3, col = 7),
                Position(row = 4, col = 0),
                Position(row = 5, col = 6),
                Position(row = 6, col = 2),
                Position(row = 7, col = 4),
            ),
            queens
        )
    }

    @Test
    fun `2025-Jul-25`() {
        val queens = solve(
            "ppooobbb",
            "pppogbgb",
            "pppogggp",
            "pppppppp",
            "pwwwprrp",
            "pwywyrdd",
            "ppyyyrrd",
            "ppppppdd",
        )
        assertEquals(
            setOf(
                Position(row = 0, col = 2),
                Position(row = 1, col = 7),
                Position(row = 2, col = 4),
                Position(row = 3, col = 0),
                Position(row = 4, col = 5),
                Position(row = 5, col = 1),
                Position(row = 6, col = 3),
                Position(row = 7, col = 6),
            ),
            queens
        )
    }

    @Test
    fun `2025-Jul-27`() {
        val queens = solve(
            "rrrrobbbb",
            "rrrroggbb",
            "rrwrogggb",
            "rvwwogygb",
            "rvvwoyyyb",
            "rdvwopbbb",
            "rdddoppbb",
            "rrrropppb",
            "rrrropppp",
        )
        assertEquals(
            setOf(
                Position(row = 0, col = 0),
                Position(row = 1, col = 5),
                Position(row = 2, col = 2),
                Position(row = 3, col = 6),
                Position(row = 4, col = 1),
                Position(row = 5, col = 8),
                Position(row = 6, col = 3),
                Position(row = 7, col = 7),
                Position(row = 8, col = 4),
            ),
            queens
        )
    }

    @Test
    fun `2025-Jul-28`() {
        val queens = solve(
            "ppppppo",
            "pbgggpo",
            "pbgpppo",
            "pbbwooo",
            "pprryyy",
            "ypprrry",
            "yyyyyyy",
        )
        assertEquals(
            setOf(
                Position(row = 0, col = 6),
                Position(row = 1, col = 4),
                Position(row = 2, col = 1),
                Position(row = 3, col = 3),
                Position(row = 4, col = 0),
                Position(row = 5, col = 5),
                Position(row = 6, col = 2),
            ),
            queens
        )
    }

    @Test
    fun `2025-Jul-29`() {
        val queens = solve(
            "pppppppp",
            "oobbbbbp",
            "gobbbbbp",
            "gobbbbbp",
            "ggwwwwbb",
            "rgwywwww",
            "rggyrrdr",
            "rrrrrrrr"
        )
        assertEquals(
            setOf(
                Position(row = 0, col = 7),
                Position(row = 1, col = 1),
                Position(row = 2, col = 4),
                Position(row = 3, col = 0),
                Position(row = 4, col = 5),
                Position(row = 5, col = 3),
                Position(row = 6, col = 6),
                Position(row = 7, col = 2),
            ),
            queens
        )
    }

    @Test
    fun `2025-Jul-30`() {
        val queens = solve(
            "ppppobbb",
            "ppppobbb",
            "ppppobbb",
            "ppppoggg",
            "wwwppppp",
            "rryppppp",
            "rryppppd",
            "rryppppp",
        )
        assertEquals(
            setOf(
                Position(row = 0, col = 4),
                Position(row = 1, col = 6),
                Position(row = 2, col = 3),
                Position(row = 3, col = 5),
                Position(row = 4, col = 0),
                Position(row = 5, col = 2),
                Position(row = 6, col = 7),
                Position(row = 7, col = 1),
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