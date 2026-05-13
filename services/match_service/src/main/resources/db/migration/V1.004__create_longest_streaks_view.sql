create view longest_streak as (
    with is_streaks as (
        select *, case when lag (score) over (partition by user_id order by created_at) = score then 0 else 1 end as is_streak from match_detail
    ),
    streak_groups as (
        select *, sum (is_streak) over (partition by user_id order by created_at) as streak_group 
        from is_streaks 
    ),
    streak_lengths as (
        select user_id, streak_group, score, count(*) as streak_length from streak_groups group by user_id, streak_group, score
    ),
    longest_streaks as (
        select user_id, streak_group, score, max(streak_length) over (partition by score) as longest_streak from streak_lengths treak_lengths group by user_id, streak_group, score, streak_length
    )
    select distinct user_id, score, longest_streak from longest_streaks where score != 'DRAW'
);

grant select on longest_streak to match_service;
