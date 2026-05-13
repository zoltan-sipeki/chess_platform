create view leaderboard as (
	select user_id, ranked_mmr, rank() over ( order by ranked_mmr desc ) as rank, cume_dist() over ( order by ranked_mmr ) percentile
	from player_mmr
	order by ranked_mmr desc
);

grant select on leaderboard to match_service;