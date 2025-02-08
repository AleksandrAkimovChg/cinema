package com.javaacademy.cinema.integration.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestUtilSqlQuery {

    LAST_MOVIE_ID(
            """
                    select id
                    from movie
                    order by id desc limit 1;
                    """),
    LAST_SESSION_ID(
            """
                    select id
                    from session
                    order by id desc
                    limit 1;
                    """),
    LAST_TICKET_ID(
            """
                    select id
                    from ticket
                    order by id desc
                    limit 1;
                    """),
    LAST_PLACE_ID(
            """
                    select id
                    from place
                    order by id desc
                    limit 1;
                    """),
    COUNT_ALL_PLACES(
            """
                    select count(*)
                    from place;
                    """),
    COUNT_ALL_MOVIES(
            """
                    select count(*)
                    from movie;
                    """),
    COUNT_ALL_SESSIONS(
            """
                    select count(*)
                    from session;
                    """),
    LAST_SESSION_WITH_SOLD_TICKET(
            """
                    select distinct session_id
                    from ticket
                    where is_purchased = true
                    order by session_id desc
                    limit 1;
                    """),
    COUNT_SOLD_TICKETS(
            """
                    select count(*)
                    from ticket
                    where session_id = ? and is_purchased = true;
                    """),
    COUNT_FREE_PLACES_ON_SESSION(
            """
                    select count(*)
                    from ticket
                    where session_id = ? and is_purchased = false;
                    """),
    LAST_TICKET_NOT_SOLD_BY_LAST_SESSION_ID_AND_LAST_PLACE_ID(
            """
                    select t.*
                    from ticket t
                        inner join place p on t.place_id = p.id
                    where t.is_purchased = false
                    order by id, session_id, place_id
                    limit 1;
                   """),;

    private final String sqlQuery;
}
