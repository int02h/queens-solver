let isMouseDown = false
let queenAutoCrosses = new Map()

const CellState = {
    EMPTY: "empty",
    CROSSED: "crossed",
    QUEEN: "queen"
}

class GameField {
    #rows
    #cols
    #onCellUpdated
    #onSolutionReady
    #queens = new Set()
    #isMouseDown = false
    #queenAutoCrosses = new Map()
    #cells = []
    #colors = []
    #states = []

    constructor(rows, cols, onCellUpdated, onSolutionReady) {
        this.#rows = rows
        this.#cols = cols
        this.#onCellUpdated = onCellUpdated
        this.#onSolutionReady = onSolutionReady

        for (let i = 0; i < rows; i++) {
            this.#cells.push([])
            this.#colors.push([])
            this.#states.push([])
        }
    }

    setCell(row, col, cell, color) {
        this.#cells[row][col] = cell
        this.#colors[row][col] = color
        this.#setCellState(row, col, CellState.EMPTY)
    }

    onMouseDown(row, col) {
        this.#isMouseDown = true;
        switch (this.#states[row][col]) {
            case CellState.EMPTY:
                this.#setCellState(row, col, CellState.CROSSED)
                break
            case CellState.CROSSED:
                this.#setCellState(row, col, CellState.QUEEN)
                this.#putQueenAutoCrosses(row, col)
                this.#queens.add(`${row}:${col}`)
                if (this.#queens.size === this.#rows) {
                    this.#onSolutionReady([...this.#queens].sort().join(","))
                }
                break;
            case CellState.QUEEN:
                this.#setCellState(row, col, CellState.EMPTY)
                this.#removeQueenAutoCrosses(row, col)
                this.#queens.delete(`${row}:${col}`)
                break;
        }
    }

    onMouseEnter(row, col) {
        if (this.#isMouseDown && this.#states[row][col] === CellState.EMPTY) {
            this.#setCellState(row, col, CellState.CROSSED)
        }
    }

    onMouseUp() {
        this.#isMouseDown = false;
    }

    #putQueenAutoCrosses(queenRow, queenCol) {
        const crosses = []

        const putCrossIfEmpty = (row, col) => {
            if (this.#states[row] && this.#states[row][col] === CellState.EMPTY) {
                this.#setCellState(row, col, CellState.CROSSED)
                crosses.push({row: row, col: col})
            }
        }

        const queenColor = this.#colors[queenRow][queenCol]
        for (let row = 0; row < this.#rows; row++) {
            for (let col = 0; col < this.#cols; col++) {
                if (row !== queenRow && col !== queenCol && this.#colors[row][col] === queenColor) {
                    putCrossIfEmpty(row, col)
                }
            }
        }

        for (let col = 0; col < queenCol; col++) putCrossIfEmpty(queenRow, col)
        for (let col = queenCol + 1; col < this.#cols; col++) putCrossIfEmpty(queenRow, col)
        for (let row = 0; row < queenRow; row++) putCrossIfEmpty(row, queenCol)
        for (let row = queenRow + 1; row < this.#rows; row++) putCrossIfEmpty(row, queenCol)
        putCrossIfEmpty(queenRow - 1, queenCol - 1)
        putCrossIfEmpty(queenRow + 1, queenCol - 1)
        putCrossIfEmpty(queenRow - 1, queenCol + 1)
        putCrossIfEmpty(queenRow + 1, queenCol + 1)

        this.#queenAutoCrosses.set(`row:${queenRow};col:${queenCol}`, crosses)
    }

    #removeQueenAutoCrosses(queenRow, queenCol) {
        const crosses = this.#queenAutoCrosses.get(`row:${queenRow};col:${queenCol}`)
        if (!crosses) {
            return
        }
        for (let cross of crosses) {
            if (this.#states[cross.row][cross.col] === CellState.CROSSED) {
                this.#setCellState(cross.row, cross.col, CellState.EMPTY)
            }
        }
        queenAutoCrosses.delete(`row:${queenRow};col:${queenCol}`)
    }

    #setCellState(row, col, state) {
        this.#states[row][col] = state
        const cell = this.#cells[row][col]
        this.#onCellUpdated(cell, state)
    }
}