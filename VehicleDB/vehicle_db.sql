--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.1

-- Started on 2017-04-05 17:38:03

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
-- TOC entry 2113 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE ROLE vehicle_service LOGIN
  ENCRYPTED PASSWORD 'vehicle1234'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

CREATE DATABASE vehicle_db OWNER postgres;
  
\connect vehicle_db

--
-- TOC entry 181 (class 1259 OID 57398)
-- Name: vehicle_configurations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE vehicle_configurations (
    name text NOT NULL,
    data json
);


ALTER TABLE vehicle_configurations OWNER TO postgres;

--
-- TOC entry 182 (class 1259 OID 57406)
-- Name: vehicles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE vehicles (
    user_id integer NOT NULL,
    avatar_name text NOT NULL,
    current_vehicle_index integer DEFAULT 0,
    data json[]
);


ALTER TABLE vehicles OWNER TO postgres;

--
-- TOC entry 2104 (class 0 OID 57398)
-- Dependencies: 181
-- Data for Name: vehicle_configurations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY vehicle_configurations (name, data) FROM stdin;
Avalon	{"name":"Avalon","primaryWeapons":["LaserBlaster","LaserBlaster","LaserBlaster","LaserBlaster","LaserBlaster","LaserBlaster"],"heavyWeapons":["RocketLauncher"]}
Balance	{"name":"Balance","primaryWeapons":["LaserBlaster","LaserBlaster","LaserBlaster","LaserBlaster"],"heavyWeapons":[]}
Beetle	{"name":"Beetle","primaryWeapons":["LaserBlaster"],"heavyWeapons":[]}
Degen	{"name":"Degen","primaryWeapons":["LaserBlaster","LaserBlaster"],"heavyWeapons":[]}
Delta	{"name":"Delta","primaryWeapons":["LaserBlaster"],"heavyWeapons":[]}
Drone	{"name":"Drone","primaryWeapons":["LaserBlaster"],"heavyWeapons":[]}
Eagle	{"name":"Eagle","primaryWeapons":["LaserBlaster","LaserBlaster"],"heavyWeapons":["RocketLauncher"]}
Gaujo	{"name":"Gaujo","primaryWeapons":["LaserBlaster"],"heavyWeapons":[]}
Humble	{"name":"Humble","primaryWeapons":["LaserBlaster","LaserBlaster","LaserBlaster","LaserBlaster","LaserBlaster","LaserBlaster"],"heavyWeapons":["RocketLauncher"]}
Mice	{"name":"Mice","primaryWeapons":["LaserBlaster","LaserBlaster"],"heavyWeapons":[]}
Omega	{"name":"Omega","primaryWeapons":["LaserBlaster","LaserBlaster"],"heavyWeapons":["RocketLauncher","RocketLauncher"]}
Rhino	{"name":"Rhino","primaryWeapons":["LaserBlaster"],"heavyWeapons":[]}
Scout	{"name":"Scout","primaryWeapons":["LaserBlaster","LaserBlaster"],"heavyWeapons":[]}
Shuttle	{"name":"Shuttle","primaryWeapons":["LaserBlaster","LaserBlaster"],"heavyWeapons":[]}
Worker	{"name":"Worker","primaryWeapons":["LaserBlaster"],"heavyWeapons":[]}
\.


--
-- TOC entry 2105 (class 0 OID 57406)
-- Dependencies: 182
-- Data for Name: vehicles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY vehicles (user_id, avatar_name, current_vehicle_index, data) FROM stdin;
9	adadaaaaaa	0	{"{\\"name\\":\\"Mice\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1017	Dilda	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1031	Conf	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1032	Reb	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	asdffasfdasfda	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	Chewi	1	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	Para	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}"}
1013	Jacko	6	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[null]}","{\\"name\\":\\"Beetle\\",\\"primaryWeapons\\":[],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Scout\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Omega\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\",\\"RocketLauncher\\"]}","{\\"name\\":\\"Shuttle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Avalon\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}"}
1013	Tareg	0	{"{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	Jacko2	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[null]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Humble\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[null]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[null]}"}
1033	Test	2	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Humble\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}"}
1028	Hooters	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"CyanideGun\\",\\"CyanideGun\\"],\\"heavyWeapons\\":[]}"}
1029	Hans	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1030	Sepp	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	Jude	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	Rebo	1	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Shuttle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	Henri	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	Neutro	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"FireBlaster\\",\\"FireBlaster\\"],\\"heavyWeapons\\":[\\"TorpedoPod\\"]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Avalon\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Scout\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Omega\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\",\\"RocketLauncher\\"]}","{\\"name\\":\\"Shuttle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Avalon\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Scout\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Beetle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
8	Rebi	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
10	Karl	0	{"{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
1013	Rebi	2	{"{\\"name\\":\\"Mice\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"GaussBeam\\",\\"GaussBeam\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Worker\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Humble\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Scout\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Mice\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Mice\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Mice\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Mice\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"FireBlaster\\",\\"FireBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Eagle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Mice\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Avalon\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Shuttle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Omega\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\",\\"RocketLauncher\\"]}","{\\"name\\":\\"Avalon\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[\\"RocketLauncher\\"]}","{\\"name\\":\\"Delta\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
8	sdadadsad	1	{"{\\"name\\":\\"Beetle\\",\\"primaryWeapons\\":[\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}","{\\"name\\":\\"Scout\\",\\"primaryWeapons\\":[\\"LaserBlaster\\",\\"LaserBlaster\\"],\\"heavyWeapons\\":[]}"}
\.


--
-- TOC entry 1987 (class 2606 OID 57405)
-- Name: vehicle_configuration_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vehicle_configurations
    ADD CONSTRAINT vehicle_configuration_pk PRIMARY KEY (name);


--
-- TOC entry 1989 (class 2606 OID 57413)
-- Name: vehicles_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vehicles
    ADD CONSTRAINT vehicles_pk PRIMARY KEY (user_id, avatar_name);


--
-- TOC entry 2112 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2114 (class 0 OID 0)
-- Dependencies: 181
-- Name: vehicle_configurations; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE vehicle_configurations FROM PUBLIC;
REVOKE ALL ON TABLE vehicle_configurations FROM postgres;
GRANT ALL ON TABLE vehicle_configurations TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE vehicle_configurations TO vehicle_service;


--
-- TOC entry 2115 (class 0 OID 0)
-- Dependencies: 182
-- Name: vehicles; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE vehicles FROM PUBLIC;
REVOKE ALL ON TABLE vehicles FROM postgres;
GRANT ALL ON TABLE vehicles TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE vehicles TO vehicle_service;


-- Completed on 2017-04-05 17:38:04

--
-- PostgreSQL database dump complete
--

