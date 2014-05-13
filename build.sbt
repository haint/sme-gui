name := "sme-gui"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
	"org.sme" % "tools" % "1.0-SNAPSHOT",
	"org.apache.cloudstack" % "cloud-api" % "4.3.0",
	"org.mongodb" % "mongo-java-driver" % "2.10.1"
)

resolvers += (
	"Local Maven Repository" at "file:///home/public/java/dependencies/repository"		
)

play.Project.playJavaSettings
