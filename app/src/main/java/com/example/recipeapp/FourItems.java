package com.example.recipeapp;

import java.util.Objects;

// class to store 4 items similar to pair / triple
public class FourItems<A,B,C,D> {
    public final A first;
    public final B second;
    public final C third;
    public final D fourth;

    public FourItems(A first, B second, C third, D fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    // this method is meeded, otherwise references are compared instead
    @Override
    public boolean equals(Object other){
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()){
            return false;
        }

        FourItems<?, ?, ?, ?> otherFourItems = (FourItems<?, ?, ?, ?>) other;

        return Objects.equals(first, otherFourItems.first) &&
                Objects.equals(second, otherFourItems.second) &&
                Objects.equals(third, otherFourItems.third) &&
                Objects.equals(fourth, otherFourItems.fourth);
    }

    // hashcode method to treat 2 items with same values as equal hash
    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }
}
