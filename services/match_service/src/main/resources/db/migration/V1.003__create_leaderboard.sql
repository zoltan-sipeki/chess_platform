create view leaderboard as (
	select id as player_id, ranked_mmr as mmr, rank() over ( order by ranked_mmr desc ) as rank, cume_dist() over ( order by ranked_mmr ) percentile
	from player
	order by ranked_mmr desc
);

grant select on leaderboard to match_service;