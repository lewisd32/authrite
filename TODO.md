FIX: Updating displayName needs to be atomic/transactional so that multiple people can't change to the same
displayName at the same time.

FIX: disallow changing password to the existing password.

Add password reset functionality

Add email verification

Log loud warnings if running with secret that's the development default.