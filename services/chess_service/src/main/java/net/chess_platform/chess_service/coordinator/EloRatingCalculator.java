package net.chess_platform.chess_service.coordinator;

import net.chess_platform.chess_service.coordinator.match.Player;

public class EloRatingCalculator {

    public int[] calculateMmrs(Player p1, Player p2) {
        var mmr1 = p1.getMmr();
        var mmr2 = p2.getMmr();

        var score1 = p1.getScore();
        var score2 = p2.getScore();

        var e1 = 1 / (1 + Math.pow(10, (mmr2 - mmr1) / 400.0));
        var e2 = 1 / (1 + Math.pow(10, (mmr1 - mmr2) / 400.0));

        var k1 = calculateKFactor(mmr1);
        var k2 = calculateKFactor(mmr2);

        var mmrAfter1 = mmr1 + k1 * (score1 - e1);
        var mmrAfter2 = mmr2 + k2 * (score2 - e2);

        return new int[] { (int) mmrAfter1, (int) mmrAfter2 };
    }

    private int calculateKFactor(int mmr) {
        if (mmr < 2100) {
            return 32;
        }

        if (mmr > 2400) {
            return 16;
        }

        return 24;
    }
}
