# SwtFoundation
Base classes for working with SWT in Java

# To Build
Building the library is trivial.

    ./gradlew build

You may also want to locally publish it for use with your own projects:

    ./gradlew publishToMavenLocal

# About Signing

I had to do this:

    gpg --export-secret-keys -o secring.gpg
    gpg --list-keys --keyid-format short
