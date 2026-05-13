create view longest_streak as (
    with is_streaks as (
        select *, case when lag (score) over (partition by player_id order by created_at) = score then 0 else 1 end as is_streak from match_result
    ),
    streak_groups as (
        select *, sum (is_streak) over (partition by player_id order by created_at) as streak_group 
        from is_streaks 
    ),
    streak_lengths as (
        select player_id, streak_group, score, count(*) as streak_length from streak_groups group by player_id, streak_group, score
    ),
    longest_streaks as (
        select player_id, streak_group, score, max(streak_length) over (partition by score) as longest_streak from streak_lengths treak_lengths group by player_id, streak_group, score, streak_length
    )
    select distinct player_id, score, longest_streak from longest_streaks where score != 'DRAW'
);

grant select on longest_streak to match_service;
