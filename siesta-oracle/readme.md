# Oracle XE setup for tests

Run sqlplus as SYSTEM and enter

```
alter pluggable database all open;
alter pluggable database all save state;
alter session set container=xepdb1;
create user siesta identified by "siesta";
grant connect,resource,unlimited tablespace to siesta;
```
