# Gemosity Identity

###!!!-CODE-IS-WORK--IN--PROGRESS-!!!
Spring Boot, NoSQL based identity server using JWT based authentication.

### Current status
- Able to use the current code to login to Gemosity CMS.
- Migration code for Gemosity Auth RDBMS database is working. Available on /migrate endpoint.
- All the new code i.e. NoSQL persistence has been developed using Test Driven Development (TDD).

## ToDo
- Scoped collections for persisting domains as scopes.
- Write unit test for some of the authentication code imported from Gemosity Auth i.e. v1.
- Change user password
- Forgot password workflow
- USB dongle password-less login (need to investigate WebAuthn)
- Measure transactions per second
- Implement session support using auto-expiring documents

## Getting Started

Need to have authenticated (and pass authentication token) before the following commands can be executed.

Add a user to the user_profile collection:

```
curl -X POST -H 'Content-Type: application/json' -i http://localhost:9124/api/user/create --data '{"username": "USERNAME"}'
```

JSON response:

```
{"uuid":"<UUID>","username":"<USERNAME>","created":null,"modified":null,"activated":false}
```
