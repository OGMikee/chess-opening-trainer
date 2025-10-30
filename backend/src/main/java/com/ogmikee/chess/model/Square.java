package com.ogmikee.chess.model;

import java.util.Objects;

public class Square {
    private final int file;
    private final int rank;

    public Square(int file, int rank) {
        if (0 > file || file > 7 ||
                0 > rank || rank > 7) {
            throw new IllegalArgumentException();
        }
        this.file = file;
        this.rank = rank;
    }

    public Square(String algebraic) {
        if (algebraic.length() != 2 ||
                algebraic.charAt(0) < 'a' || algebraic.charAt(0) > 'h' ||
                algebraic.charAt(1) < '1' || algebraic.charAt(1) > '8'){
            throw new IllegalArgumentException();
        }
        this.file = (int) algebraic.charAt(0) - 'a';
        this.rank = (int) algebraic.charAt(1) - '1';
    }

    public String toAlgebraic() {
        return "" + (char) (getFile() + 'a') + (char) (getRank() + '1');
    }

    public int getFile() {
        return this.file;
    }

    public int getRank() {
        return this.rank;
    }

    public boolean isValid() {
        return (0 < file && file < 7 && 0 < rank && rank > 7);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        return obj instanceof  Square other &&
                file == other.file &&
                rank == other.rank;
    }

    @Override
    public int hashCode() {
        //return file * 8 + rank;
        return Objects.hash(file, rank);
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}