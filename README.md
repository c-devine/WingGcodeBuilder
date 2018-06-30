# WingGcodeBuilder

JavaFX / Spring Boot application used to create G-code files for 4 axis hotwire cutting machine. The application can import Selig and Lednicer format airfoil .dat
files from the University of Illinois [UIUC Airfoil Database]( http://m-selig.ae.illinois.edu/ads/coord_database.html) and other sites. Airfoil, chord length, twist,
and offset are editable for both the tip and root.  There is a 2D view of the airfoils, as well as a 3D view of the wing half or whole
wing. G-code can be generated for both the right and left (mirrored) wings. Created to generate G-code for low cost (?) hotwire foam
cutter.  See [wiki](https://github.com/c-devine/WingGcodeBuilder/wiki).

## Workflow

* Download airfoils for root and tip (if different)
* Select airfoils in the 2D view
* Modify wing section parameters (chord, span, offset, y scale, twist)
* View in 3D view
* Modify G-code settings (feed rate, kerf, etc)
* Generate and export G-code (to file, or directly to OctoPrint server)
* Save the project for future use


## Snapshots

<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/2D.png?raw=true" width="320" height="240">
<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/3D-v101.png?raw=true" width="320" height="240">
<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/GCODE.png?raw=true" width="320" height="240">

## Hardware

<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/model.png?raw=true" width="320" height="240">
<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/4axis.jpg?raw=true" width="320" height="240">

## Latest Updates

* Updated 3D view to show more complex airfoil profiles
* Created Sketchup [scripts](https://github.com/c-devine/SketchupUtilities) to import .dat files and export edited .dat files
* Added unswept layout and GCODE creation options
<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/3d-complex.png?raw=true" width="320" height="240">
<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/layout.png?raw=true" width="320" height="240">

## Getting Started

Clone the repository:
```
git clone https://github.com/c-devine/WingGcodeBuilder.git
```
Build the project:
```
gradlew build
```
Execute the jar file:
```
java -jar ./build/libs/WingGcodeBuilder.jar
```

## Prerequisites

Requires Java 8 to build and deploy.

## Installing

*See Getting Started, or Deployment*

## Running the tests

```gradlew test```

## Deployment

Build the jar files:
```
gradlew build
```

Create the native executable install file:

```
gradlew javafxNative
```

## Built With

* [Spring Boot](https://projects.spring.io/spring-boot/) - Application framework
* [Gradle](https://gradle.org/) - Dependency Management
* [e\(fx\)clipse](http://www.eclipse.org/efxclipse/index.html) - IDE

## Contributing

Submit a pull request if interested in contributing.

## Versioning

[SemVer](http://semver.org/) for versioning.

## Authors

* **Chris** - *Initial work* - [c-devine](https://github.com/c-devine)


## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* [toxiclibs](http://toxiclibs.org/) - geometry manipulation routines
* [Spring Boot / Java FX](https://github.com/thomasdarimont/spring-labs/tree/master/spring-boot-javafx) - Spring Boot / JavaFX integration
* [updatefx](https://github.com/bitgamma/updatefx) - automatic version updates
