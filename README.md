# WingGcodeBuilder

JavaFX / Spring Boot application used to create G-code files for 4 axis hotwire cutting machine. The application can import Selig format (for now) airfoil .dat
files from the University of Illinois [UIUC Airfoil Database]( http://m-selig.ae.illinois.edu/ads/coord_database.html). Airfoil, chord length,
and offset are editable for both the tip and root.  There is a 2D view of the airfoils, as well as a 3D view of the wing half or whole
wing. G-code can be generated for both the right and left (mirrored) wings. Created to generate G-code for low cost (?) hotwire foam
cutter.

## Workflow

* Download airfoils for root and tip (if different)
* Select airfoils in the 2D view
* Modify wing section parameters (chord, Y (span), offset)
* View in 3D view
* Modify G-code settings (feed rate, kerf, etc)
* Export G-code


## Snapshots

<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/2D.png?raw=true" width="320" height="240">
<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/3D.png?raw=true" width="320" height="240">
<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/GCODE.png?raw=true" width="320" height="240">

## Hardware

<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/model.png?raw=true" width="320" height="240">
<img src="https://raw.githubusercontent.com/c-devine/WingGcodeBuilder/snapshots/assets/img/4axis.jpg?raw=true" width="320" height="240">

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

*Not yet implemented.*

## Running the tests

```gradlew test```

## Deployment

*Not yet implemented.*

## Built With

* [Spring Boot](https://projects.spring.io/spring-boot/) - Application framework
* [Gradle](https://gradle.org/) - Dependency Management
* [e\(fx\)clipse](http://www.eclipse.org/efxclipse/index.html) - IDE

## Contributing

Submit a pull request if interested in contributing.

## Versioning

Really nothing to version yet.

## Authors

* **Chris** - *Initial work* - [c-devine](https://github.com/c-devine)


## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* tbd