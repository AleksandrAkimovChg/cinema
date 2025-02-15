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
    movie_id integer REFERENCES movie(id),
    date_time timestamp NOT NULL,
    price numeric(10, 2)
);

CREATE TABLE IF NOT EXISTS public.ticket
(
    id serial PRIMARY KEY,
    place_id integer REFERENCES place(id),
    session_id integer REFERENCES session(id),
    is_purchased boolean DEFAULT FALSE
);

insert into place (name) values
	('A1'), ('A2'), ('A3'), ('A4'), ('A5'),
	('B1'), ('B2'), ('B3'), ('B4'), ('B5');

insert into movie (name, description) values
	('Красная жара', 'Фильм с Шварцнеггером');
