--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.1

-- Started on 2017-04-05 11:10:05

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12355)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2106 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE ROLE avatar_service LOGIN
  ENCRYPTED PASSWORD 'avatar1234'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

CREATE DATABASE avatar_db OWNER postgres;
  
\connect avatar_db


--
-- TOC entry 181 (class 1259 OID 41020)
-- Name: avatars; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE avatars (
    user_id integer NOT NULL,
    name text NOT NULL,
    data json
);


ALTER TABLE avatars OWNER TO postgres;

--
-- TOC entry 2098 (class 0 OID 41020)
-- Dependencies: 181
-- Data for Name: avatars; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY avatars (user_id, name, data) FROM stdin;
1013	Rebo	{"name":"Rebo","faction":{"confederateReputation":-0.3,"rebelReputation":0.4},"job":"Support","regionID":{"types":[1],"values":[32]},"homeRegionID":{"types":[1],"values":[32]},"poiID":{"types":[],"values":[]},"landed":false,"position":{"x":0.0,"y":0.0},"credits":100000}
1013	Henri	{"name":"Henri","faction":{"confederateReputation":0.4,"rebelReputation":-0.3},"job":"Assault","regionID":{"types":[1],"values":[32]},"homeRegionID":{"types":[1],"values":[33]},"poiID":{"types":[],"values":[]},"landed":false,"position":{"x":0.0,"y":0.0},"credits":100000}
1013	Rebi	{"name":"Rebi","faction":{"confederateReputation":-0.3,"rebelReputation":0.4},"job":"Assault","regionID":{"types":[1],"values":[32]},"homeRegionID":{"types":[1],"values":[32]},"poiID":{"types":[1,6],"values":[32,0]},"landed":false,"position":{"x":-0.9,"y":0.1},"credits":100000}
\.


--
-- TOC entry 1981 (class 2606 OID 41029)
-- Name: avatar_name_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY avatars
    ADD CONSTRAINT avatar_name_unique UNIQUE (name);


--
-- TOC entry 1983 (class 2606 OID 41027)
-- Name: avatar_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY avatars
    ADD CONSTRAINT avatar_pk PRIMARY KEY (user_id, name);


--
-- TOC entry 2105 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2107 (class 0 OID 0)
-- Dependencies: 181
-- Name: avatars; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE avatars FROM PUBLIC;
REVOKE ALL ON TABLE avatars FROM postgres;
GRANT ALL ON TABLE avatars TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE avatars TO avatar_service;


-- Completed on 2017-04-05 11:10:05

--
-- PostgreSQL database dump complete
--

