package net.chess_platform.common.domain_events.broker.chess;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent.Payload;

public class MatchEndedEvent extends DomainEvent<Payload> {

	public static record Payload(

			long matchId,

			String matchType,

			Instant startedAt,

			Instant endedAt,

			List<Player> players,

			List<Move> replay) {

		public static record Move(

				Position from,

				Position to,

				String type,

				String checkStatus,

				long timestamp,

				PromotedPiece promotedPiece) {
		}

		public static record PromotedPiece(

				String color,

				String type

		) {
		}

		public static record Position(

				int row,

				int col) {
		}

		public static record Piece(

				String color,

				String type) {
		}

		public static record Player(

				UUID id,

				String color,

				Integer mmrBefore,

				Integer mmrAfter,

				float score) {
		}

	}

	public MatchEndedEvent(Payload data) {
		super(DomainEvent.Category.CHESS, DomainEvent.Type.MATCH_ENDED, data);
	}

}
