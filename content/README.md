# Course Content

The course content source is held in a Gitbooks format. To interact with this content you should use a `gitbooks` cli or view the raw markdown directly.

The suggested development method is the following:

1. Install `gitbooks-cli` NPM package (you'll need node, and npm setup on your machine
2. To start the server execute `gitbooks serve ./book`.

Alternatively you can have gitbooks as a container:

1. in the `book` directory, run `docker run --rm -v "$PWD:/gitbook" -p 4000:4000 billryan/gitbook gitbook serve`

This will hot reload the content as you modify it.  You can view it at http://localhost:4000
