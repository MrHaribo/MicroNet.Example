--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.1

-- Started on 2017-04-05 15:28:56

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
-- TOC entry 2105 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE ROLE item_service LOGIN
  ENCRYPTED PASSWORD 'item1234'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

CREATE DATABASE item_db OWNER postgres;
  
\connect item_db

--
-- TOC entry 181 (class 1259 OID 24610)
-- Name: items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE items (
    user_id integer NOT NULL,
    location text NOT NULL,
    size integer DEFAULT 0,
    inventory json[]
);


ALTER TABLE items OWNER TO postgres;

--
-- TOC entry 2097 (class 0 OID 24610)
-- Dependencies: 181
-- Data for Name: items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY items (user_id, location, size, inventory) FROM stdin;
1031	Inventory	10	\N
1032	Inventory	10	\N
1033	Inventory	10	[0:4]={NULL,NULL,NULL,NULL,NULL}
1013	Inventory	10	[0:9]={"{\\"name\\":\\"TorpedoPod\\",\\"type\\":2,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"LaserBlaster\\",\\"type\\":1,\\"quantity\\":1,\\"maxStackSize\\":1,\\"price\\":100}","{\\"name\\":\\"FireBlaster\\",\\"type\\":1,\\"quantity\\":1,\\"maxStackSize\\":1,\\"price\\":100}","{\\"name\\":\\"RocketLauncher\\",\\"type\\":2,\\"quantity\\":1,\\"maxStackSize\\":1,\\"price\\":100}","{\\"name\\":\\"FireBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}",NULL,"{\\"name\\":\\"LaserBlaster\\",\\"type\\":1,\\"quantity\\":1,\\"maxStackSize\\":1,\\"price\\":100}","{\\"name\\":\\"IceBlaster\\",\\"type\\":1,\\"quantity\\":1,\\"maxStackSize\\":1,\\"price\\":100}","{\\"name\\":\\"LaserBlaster\\",\\"type\\":1,\\"quantity\\":1,\\"maxStackSize\\":1,\\"price\\":100}","{\\"name\\":\\"IceBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}"}
\.


--
-- TOC entry 1982 (class 2606 OID 24619)
-- Name: user_inventory_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY items
    ADD CONSTRAINT user_inventory_pk PRIMARY KEY (user_id, location);


--
-- TOC entry 2104 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2106 (class 0 OID 0)
-- Dependencies: 181
-- Name: items; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE items FROM PUBLIC;
REVOKE ALL ON TABLE items FROM postgres;
GRANT ALL ON TABLE items TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE items TO item_service;


-- Completed on 2017-04-05 15:28:56

--
-- PostgreSQL database dump complete
--

