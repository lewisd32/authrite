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


## Features to (potentially) implement:

- Randomizing login check time to make timing attacks more difficult
  - ensure all the following take the same amount of time:
    - failing to find a user in the database
    - password being incorrect
- Lock out accounts after too many login attempts
  - able to be unlocked via email to prevent it being a DOS
- Support for HTTPS
  - Primarily code for redirecting insecure connections to HTTPS
- Email verification
- Password reset
- Appropriate DB transaction boundaries

### Switching to username or email for login

I would like to attempt to prevent disclosure of what email addresses are are
registered.  For some services, simple knowing that someone is signed up for
them could put people in a bad position. Even though it could have been someone
else that signed them up, using their email address, in many situations this may
not be considered until too late. eg. Someone checks to see if their partner's
email address is in use on a Ashley Madison like site.  When they find that it's
in use, telling them that someone else may have signed up with their parter's
email address, to make it look bad, may not help matters much.

The primary avenues of this disclosure are during signup, and when changing
your account's email address.  Currently, in both those cases, we check if
the email address is in use, and say "email address already in use" if it is.

To fix this for the signup case, we would need to allow multiple accounts with
the same email address.  Same for when changing email address.

This introduces challenges during login.  I recognize that people don't always
like having to remember screen names between multiple websites, to use it for
logging in.  Allowing people to login with an email address or screen name
would be useful.

To allow logging in with an email address, the system would have to try the
password against *all* accounts with that email address.  This has the potential
for attackers to use timing to determine whether an email address is registered
or not.

Disallowing logging in with an email address if there are multiple accounts
registered to that address presents a different problem, however.  If, when a
user tries to login, we return the generic login error, even when their credentials
may have been correct, this would be very confusing for the user.  However,
returning any other error message again allows for disclosure of whether that
email address is registered.
