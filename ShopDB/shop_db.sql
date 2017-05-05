--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.1

-- Started on 2017-04-05 16:55:24

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
-- TOC entry 2104 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE ROLE shop_service LOGIN
  ENCRYPTED PASSWORD 'shop1234'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

CREATE DATABASE shop_db OWNER postgres;
  
\connect shop_db

--
-- TOC entry 181 (class 1259 OID 49206)
-- Name: shops; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE shops (
    faction text NOT NULL,
    items json[],
    rank_restrictions integer[]
);


ALTER TABLE shops OWNER TO postgres;

--
-- TOC entry 2096 (class 0 OID 49206)
-- Dependencies: 181
-- Data for Name: shops; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY shops (faction, items, rank_restrictions) FROM stdin;
Confederate	{"{\\"name\\":\\"Beetle\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"Scout\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":700}","{\\"name\\":\\"Omega\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":650}","{\\"name\\":\\"Shuttle\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":480}","{\\"name\\":\\"Avalon\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":2000}","{\\"name\\":\\"LaserBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":80}","{\\"name\\":\\"RocketLauncher\\",\\"type\\":2,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"FireBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"IceBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"GaussBeam\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":300}","{\\"name\\":\\"TorpedoPod\\",\\"type\\":2,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}"}	{0,1,1,1,2,1,1,2,2,2,3}
Rebel	{"{\\"name\\":\\"Mice\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"Eagle\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":600}","{\\"name\\":\\"Delta\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":720}","{\\"name\\":\\"Worker\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":550}","{\\"name\\":\\"Humble\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":2000}","{\\"name\\":\\"LaserBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":80}","{\\"name\\":\\"RocketLauncher\\",\\"type\\":2,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"FireBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"IceBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"GaussBeam\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":300}","{\\"name\\":\\"TorpedoPod\\",\\"type\\":2,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}"}	{0,1,1,1,2,1,1,2,2,2,3}
Neutral	{"{\\"name\\":\\"Gaujo\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":1200}","{\\"name\\":\\"Rhino\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":1200}","{\\"name\\":\\"Balance\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":2400}","{\\"name\\":\\"Drone\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":50}","{\\"name\\":\\"Degen\\",\\"type\\":0,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":650}","{\\"name\\":\\"RocketLauncher\\",\\"type\\":2,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":100}","{\\"name\\":\\"LaserBlaster\\",\\"type\\":1,\\"quantity\\":0,\\"maxStackSize\\":0,\\"price\\":80}"}	{0,0,0,0,0,0,0}
\.


--
-- TOC entry 1981 (class 2606 OID 49213)
-- Name: shop_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY shops
    ADD CONSTRAINT shop_pk PRIMARY KEY (faction);


--
-- TOC entry 2103 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2105 (class 0 OID 0)
-- Dependencies: 181
-- Name: shops; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE shops FROM PUBLIC;
REVOKE ALL ON TABLE shops FROM postgres;
GRANT ALL ON TABLE shops TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shops TO shop_service;


-- Completed on 2017-04-05 16:55:24

--
-- PostgreSQL database dump complete
--

