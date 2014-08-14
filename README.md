A prototype that builds and reloads a Play application without sbt

# Running
1. Build first (because error reporting is not properly implmeneted) in the router:
`./gradlew build`
2. Run (you can change the source and hit reload in the browser, but you need to check the command line output to see the error):
`./gradlew play-run`

