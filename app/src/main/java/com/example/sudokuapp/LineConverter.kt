package com.example.sudokuapp

class LineConverter {
    public fun gridToLine(grid: Array<IntArray>): MutableList<Int>{
        val res = mutableListOf<Int>()
        for(i in grid.indices){
            for(j in grid.indices){
                res.add(grid[i][j])
            }
        }
        return res
    }
    public fun lineToGrid(line: MutableList<Int>): Array<IntArray>{
        val grid = Array(9) { IntArray(9) }
        for(i in grid.indices){
            for(j in grid.indices){
                 grid[i][j] = line[(i * grid[0].size)+j]
            }
        }
        return grid.clone()
    }
}