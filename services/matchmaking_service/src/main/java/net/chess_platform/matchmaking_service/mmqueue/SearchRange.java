package net.chess_platform.matchmaking_service.mmqueue;

public class SearchRange implements Comparable<SearchRange> {

    private static final int EXPANSION_FACTOR = 25;

    private int minMmr;

    private int maxMmr;

    public SearchRange(int mmr) {
        this.minMmr = Math.max(mmr - EXPANSION_FACTOR, 0);
        this.maxMmr = mmr + EXPANSION_FACTOR;
    }

    public int getMinMmr() {
        return minMmr;
    }

    public int getMaxMmr() {
        return maxMmr;
    }

    public void expand() {
        this.minMmr = Math.max(minMmr - EXPANSION_FACTOR, 0);
        this.maxMmr += EXPANSION_FACTOR;
    }

    @Override
    public int compareTo(SearchRange o) {
        if (o.minMmr > this.maxMmr) {
            return -1;
        }

        if (o.maxMmr < this.minMmr) {
            return 1;
        }

        return 0;
    }
}
