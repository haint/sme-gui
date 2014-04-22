name := "sme-gui"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
	"org.sme.tools" % "jenkins" % "1.0-SNAPSHOT"
)

resolvers += (
	"Local Maven Repository" at "file:///home/public/java/dependencies/repository"		
)

play.Project.playJavaSettings
