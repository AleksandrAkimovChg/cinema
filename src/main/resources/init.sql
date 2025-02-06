CREATE TABLE IF NOT EXISTS public.movie
(
    id serial PRIMARY KEY,
    name varchar(128),
    description text
);

CREATE TABLE IF NOT EXISTS public.place
(
    id serial PRIMARY KEY,
    name varchar(3) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.session
(
    id serial PRIMARY KEY,
    movie_id integer REFERENCES movie(movie_id),
    date_time timestamp NOT NULL,
    price numeric(10, 2)
);

CREATE TABLE IF NOT EXISTS public.ticket
(
    id serial PRIMARY KEY,
    place_id integer REFERENCES place(place_id),
    session_id integer REFERENCES session(session_id),
    is_purchased boolean DEFAULT FALSE
);
