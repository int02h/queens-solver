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
    #isMouseDown = false
    #queenAutoCrosses = new Map()
    #cells = []
    #states = []

    constructor(rows, cols, onCellUpdated) {
        this.#rows = rows
        this.#cols = cols
        this.#onCellUpdated = onCellUpdated

        for (let i = 0; i < rows; i++) {
            this.#cells.push([])
            this.#states.push([])
        }
    }

    setCell(row, col, cell) {
        this.#cells[row][col] = cell
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
                break;
            case CellState.QUEEN:
                this.#setCellState(row, col, CellState.EMPTY)
                this.#removeQueenAutoCrosses(row, col)
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
            if (this.#states[row][col] === CellState.EMPTY) {
                this.#setCellState(row, col, CellState.CROSSED)
                crosses.push({row: row, col: col})
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
            this.#setCellState(cross.row, cross.col, CellState.EMPTY)
        }
        queenAutoCrosses.delete(`row:${queenRow};col:${queenCol}`)
    }

    #setCellState(row, col, state) {
        this.#states[row][col] = state
        const cell = this.#cells[row][col]
        this.#onCellUpdated(cell, state)
    }
}

function createGrid(rows = 7, cols = 7, preferredCellSize = 48) {
    const grid = document.getElementById('grid');
    grid.innerHTML = ''; // Clear any existing cells

    // Determine the max size available
    const maxWidth = window.innerWidth * 0.95;
    const maxHeight = window.innerHeight * 0.95;
    const maxGridSize = Math.min(maxWidth, maxHeight);
    const cellSize = Math.min(preferredCellSize, Math.floor(maxGridSize / Math.max(rows, cols)));

    // Set CSS grid styles
    grid.style.gridTemplateColumns = `repeat(${cols}, ${cellSize}px)`;
    grid.style.gridTemplateRows = `repeat(${rows}, ${cellSize}px)`;

    const field = new GameField(rows, cols, function (cell, state) {
        switch (state) {
            case CellState.EMPTY:
                cell.textContent = ""
                break
            case CellState.CROSSED:
                cell.textContent = "x"
                break;
            case CellState.QUEEN:
                cell.textContent = "Q"
                break;
        }
    })

    // Populate cells
    for (let row = 0; row < rows; row++) {
        for (let col = 0; col < cols; col++) {
            const cell = document.createElement('div');
            cell.id = `row:${row};col:${col}`;
            cell.position = {row: row, col: col}
            cell.classList.add('cell');
            cell.style.width = `${cellSize}px`;
            cell.style.height = `${cellSize}px`;
            field.setCell(row, col, cell)

            // === Event Handlers ===
            cell.addEventListener('mousedown', (e) => {
                field.onMouseDown(cell.position.row, cell.position.col)
            });

            cell.addEventListener('mouseenter', () => {
                field.onMouseEnter(cell.position.row, cell.position.col)
            });

            grid.appendChild(cell);
        }
    }

    // Global mouseup listener
    document.addEventListener('mouseup', () => {
        field.onMouseUp()
    });
}