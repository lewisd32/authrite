# Authrite

## Purpose of Authrite

Authrite is an attempt to demonstrate secure practices in user authentication, and the management
of users surrounding that, such changing email address or password, validating email addresses,
and handling forgotten passwords.

Additionally, this functionality is well tested through automated tests.  These tests are written
using a DSL in the fashion described by [LMAX Simple-DSL](https://github.com/LMAX-Exchange/Simple-DSL/wiki). It
uses (currently) [a fork](https://github.com/lewisd32/Simple-DSL)
of [LMAX Simple-DSL](https://github.com/LMAX-Exchange/Simple-DSL), and
the [Unacceptable acceptance testing framework](https://github.com/unacceptable/unacceptable).

Note: Two-factor auth is beyond the scope of this project.

## Why was Authrite written

When I started a new personal project, I decided to start with user registration, login, etc.
After about a week of research and some work on how to do this for a REST API, I realized that I hadn't seen
it all tied together nicely anywhere that was open source.  I also realized that it's a significant amount
of work to complete all the important bits of functionality around this with proper testing.

As many recent breaches have demonstrated, it's also hard to get all this right.  For example, "Lost password"
emails that aren't done quite right let people enumerate the database, change other users' passwords, or allow
easier cracking of passwords once the DB is compromised.  I wanted to, for my own benefit, and that of others,
put together a sample of how to do all those bits and pieces "right".


## Features

- REST API for basic user management
  - create user
  - change email, password, screen name
- Simple RBAC
  - 3 roles: User, Admin, Read-Only Admin
- JWT in a cookie for REST authentication
  - Ensures tokens are not expired, and signed correctly (correct secret and cipher) 
- BCrypt for one-way password hashing
  - Configurable cost factor (set lower when running tests, to speed up testing)
- Protection against SQL injection
  - Uses JDBI to build SQL statements safely
- Unpredictable ids
  - Prevents enumeration-type attacks against userIds, etc.
- Makes it easy to avoid the OWASP Top 10


## Features still to implement:

- Switching to allowing users to use either a username or an email for login
  - should be able to eliminate the email enumeration attacks via :
    - creating an account with an existing user's email
    - change email to another existing user's email
- Support for HTTPS
  - Primarily code for redirecting insecure connections to HTTPS
- Email verification
- Password reset
- Appropriate DB transaction boundaries
