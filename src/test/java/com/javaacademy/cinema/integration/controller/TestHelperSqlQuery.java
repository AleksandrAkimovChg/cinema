package com.javaacademy.cinema.integration.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestHelperSqlQuery {

    LAST_MOVIE_ID("""
                    select id
                    from movie
                    order by id desc limit 1;
                    """),
    CURRENT_SEQ_MOVIE_ID("""
                    select currval('movie_id_seq');
                    """),
    LAST_SESSION_ID("""
                    select id
                    from session
                    order by id desc
                    limit 1;
                    """),
    CURRENT_SEQ_SESSION_ID("""
                    select currval('session_id_seq');
                    """),
    COUNT_ALL_TICKET_ON_SESSION("""
                    select count(*)
                    from ticket
                    where session_id = ?;
                    """),
    COUNT_ALL_PLACES("""
                    select count(*)
                    from place;
                    """),
    COUNT_ALL_MOVIES("""
                    select count(*)
                    from movie;
                    """),
    COUNT_ALL_SESSIONS("""
                    select count(*)
                    from session;
                    """),
    SESSION_IN_LAST_SOLD_TICKET("""
                    select distinct session_id
                    from ticket
                    where is_purchased = true
                    order by session_id desc
                    limit 1;
                    """),
    COUNT_SOLD_TICKETS_ON_SESSION("""
                    select count(*)
                    from ticket
                    where session_id = ? and is_purchased = true;
                    """),
    COUNT_FREE_PLACES_ON_SESSION("""
                    select count(*)
                    from ticket
                    where session_id = ? and is_purchased = false;
                    """),
    NOT_SOLD_LAST_TICKET_BY_LAST_SESSION_ID_AND_LAST_PLACE_ID("""
                     select t.*
                     from ticket t
                         inner join place p on t.place_id = p.id
                     where t.is_purchased = false
                     order by id, session_id, place_id
                     limit 1;
                    """);

    private final String sqlQuery;
}
